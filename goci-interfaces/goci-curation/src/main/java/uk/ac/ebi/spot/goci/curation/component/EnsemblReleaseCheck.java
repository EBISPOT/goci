
package uk.ac.ebi.spot.goci.curation.component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import uk.ac.ebi.spot.goci.component.EnsemblDbsnpVersion;
import uk.ac.ebi.spot.goci.component.EnsemblGenomeBuildVersion;
import uk.ac.ebi.spot.goci.component.EnsemblRelease;
import uk.ac.ebi.spot.goci.curation.service.mail.MailService;
import uk.ac.ebi.spot.goci.exception.EnsemblMappingException;
import uk.ac.ebi.spot.goci.exception.EnsemblRestIOException;
import uk.ac.ebi.spot.goci.model.MappingMetadata;
import uk.ac.ebi.spot.goci.repository.MappingMetadataRepository;

import java.util.Date;
import java.util.List;

/**
 * Created by emma on 28/09/2015.
 *
 * @author emma
 *         <p>
 *         Scheduled component that runs nightly and pings Ensembl REST API to check for changes in release number.
 */

@Component
public class EnsemblReleaseCheck {
    @Value("${ensembl.server}")
    private String ensemblRestServer;

    @Value("${ensembl.db_version}")
    private String ensemblDbVersion;

    private EnsemblRelease ensemblRelease;

    private EnsemblGenomeBuildVersion ensemblGenomeBuildVersion;

    private EnsemblDbsnpVersion ensemblDbsnpVersion;

    private MappingMetadataRepository mappingMetadataRepository;

    private MailService mailService;

    @Autowired
    public EnsemblReleaseCheck(EnsemblRelease ensemblRelease,
                               EnsemblGenomeBuildVersion ensemblGenomeBuildVersion,
                               EnsemblDbsnpVersion ensemblDbsnpVersion,
                               MappingMetadataRepository mappingMetadataRepository,
                               MailService mailService) {
        this.ensemblRelease = ensemblRelease;
        this.ensemblGenomeBuildVersion = ensemblGenomeBuildVersion;
        this.ensemblDbsnpVersion = ensemblDbsnpVersion;
        this.mappingMetadataRepository = mappingMetadataRepository;
        this.mailService = mailService;
    }

    private final Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    /**
     * Method used to determine if there has been a new Ensembl release
     */
    @Scheduled(cron = "0 00 14,20 * * *")
    public void checkRelease() throws EnsemblMappingException {

        // Get relevant metadata
        try {
            int latestEnsemblReleaseNumber = ensemblRelease.getReleaseVersion();
            String genomeBuildVersion = ensemblGenomeBuildVersion.getGenomeBuildVersion();
            int dbsnpVersion = ensemblDbsnpVersion.getDbsnpVersion();

            // If we have all the required values
            if (latestEnsemblReleaseNumber != 0 && genomeBuildVersion != null && dbsnpVersion != 0) {

                List<MappingMetadata> mappingMetadataList =
                        mappingMetadataRepository.findAll(sortByUsageStartDateDesc());

                // If there are no details in table then add them
                if (mappingMetadataList.isEmpty()) {
                    getLog().info(
                            "No mapping metadata found, adding information to database " +
                                    latestEnsemblReleaseNumber);
                    createMappingMetaData(latestEnsemblReleaseNumber, genomeBuildVersion, dbsnpVersion);

                    // Send email
                    mailService.sendReleaseChangeEmail(null,
                                                       latestEnsemblReleaseNumber, ensemblRestServer, ensemblDbVersion);

                }
                else {
                    Integer currentEnsemblReleaseNumberInDatabase =
                            mappingMetadataList.get(0).getEnsemblReleaseNumber();

                    // If the latest release in database does not match
                    // the latest Ensembl release and send notification email
                    if (!currentEnsemblReleaseNumberInDatabase.equals(latestEnsemblReleaseNumber)) {
                        if (currentEnsemblReleaseNumberInDatabase < latestEnsemblReleaseNumber) {

                            // Create new entry in mapping_metadata table
                            createMappingMetaData(latestEnsemblReleaseNumber, genomeBuildVersion, dbsnpVersion);

                            // Send email
                            mailService.sendReleaseChangeEmail(currentEnsemblReleaseNumberInDatabase,
                                                               latestEnsemblReleaseNumber, ensemblRestServer, ensemblDbVersion);

                            // Perform remapping and set performer
                            getLog().info("New Ensembl release identified: " + latestEnsemblReleaseNumber);
                        }
                        else {
                            getLog().error("Ensembl Release Integrity Issue: Current Ensembl release is " +
                                                   latestEnsemblReleaseNumber +
                                                   ". Database release number is set to " +
                                                   currentEnsemblReleaseNumberInDatabase);
                        }
                    }
                    else {
                        getLog().info("Current Ensembl release is " + latestEnsemblReleaseNumber +
                                              ", the current release used to map database is " +
                                              currentEnsemblReleaseNumberInDatabase);
                    }
                }
            }
            else {
                getLog().error(
                        "Querying Ensembl release, genome build version or dbSNP version returned null values");
                mailService.sendReleaseNotIdentifiedProblem();
            }
        }
        catch (EnsemblRestIOException e) {
            // Handle potential errors
            List<String> restErrors = e.getRestErrors();
            mailService.sendReleaseNotIdentifiedProblem();
            if (!restErrors.isEmpty()) {
                String allRestErrors = "";
                for (String error : restErrors) {
                    allRestErrors = allRestErrors + error + ". ";
                }
                getLog().error(
                        "Problem identifying Ensembl release, genome build version or dbSNP version: " + allRestErrors,
                        e);
            }
            else {
                getLog().error("Problem identifying Ensembl release, genome build version or dbSNP version ", e);
            }
        }
    }


    /**
     * Method used to create and save new Ensembl release details in the database
     *
     * @param ensemblReleaseNumber the latest Ensembl release number returned from Ensembl API
     * @param genomeBuildVersion   the latest Genome build version returned from Ensembl API
     * @param dbsnpVersion         the latest dbSNP version returned from Ensembl API
     *                             <p>
     *                             Method used to create a sorting option for a database query
     * @return Sort object used by database query
     */

    private void createMappingMetaData(int ensemblReleaseNumber,
                                       String genomeBuildVersion,
                                       int dbsnpVersion) {
        MappingMetadata newMappingMetadata = new MappingMetadata();
        newMappingMetadata.setEnsemblReleaseNumber(ensemblReleaseNumber);
        newMappingMetadata.setGenomeBuildVersion(genomeBuildVersion);
        newMappingMetadata.setDbsnpVersion(dbsnpVersion);
        newMappingMetadata.setUsageStartDate(new Date());
        mappingMetadataRepository.save(newMappingMetadata);
    }


    /**
     * Method used to create a sorting option for a database query
     *
     * @return Sort object used by database query
     */

    private Sort sortByUsageStartDateDesc() {return new Sort(new Sort.Order(Sort.Direction.DESC, "usageStartDate"));}
}
