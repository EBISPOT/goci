package uk.ac.ebi.spot.goci.curation.component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import uk.ac.ebi.spot.goci.component.EnsemblRelease;
import uk.ac.ebi.spot.goci.curation.service.MailService;
import uk.ac.ebi.spot.goci.model.MappingMetadata;
import uk.ac.ebi.spot.goci.repository.MappingMetadataRepository;
import uk.ac.ebi.spot.goci.service.MappingService;

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
public class NightlyEnsemblReleaseCheck {

    private EnsemblRelease ensemblRelease;

    private MappingMetadataRepository mappingMetadataRepository;

    private MailService mailService;

    private MappingService mappingService;

    @Autowired
    public NightlyEnsemblReleaseCheck(EnsemblRelease ensemblRelease,
                                      MappingMetadataRepository mappingMetadataRepository,
                                      MailService mailService, MappingService mappingService) {
        this.ensemblRelease = ensemblRelease;
        this.mappingMetadataRepository = mappingMetadataRepository;
        this.mailService = mailService;
        this.mappingService = mappingService;
    }

    private final Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    // Scheduled for weekdays 19:30
    @Scheduled(cron = "0 30 19 ? * MON-FRI")
    public void checkRelease() {
        int latestEnsemblReleaseNumber = ensemblRelease.getReleaseVersion();

        List<MappingMetadata> mappingMetadataList = mappingMetadataRepository.findAll(sortByUsageStartDateDesc());

        // If there are no details in table then add them
        if (mappingMetadataList.isEmpty()) {
            getLog().info("No mapping metadata found, adding...");
            createMappingMetaData(latestEnsemblReleaseNumber);
        }
        else {
            Integer currentEnsemblReleaseNumberInDatabase = mappingMetadataList.get(0).getEnsemblReleaseNumber();

            // If the latest release in database does not match
            // the latest Ensembl release do mapping and send notification email
            if (!currentEnsemblReleaseNumberInDatabase.equals(latestEnsemblReleaseNumber)) {
                if (currentEnsemblReleaseNumberInDatabase < latestEnsemblReleaseNumber) {

                    // Create new entry in mapping_metadata table
                    createMappingMetaData(latestEnsemblReleaseNumber);

                    // Send email
                    mailService.sendReleaseChangeEmail(currentEnsemblReleaseNumberInDatabase,
                                                       latestEnsemblReleaseNumber);

                    // Perform remapping and set performer
                    getLog().info("New release identified: " + latestEnsemblReleaseNumber);
                    getLog().info("Remapping all database contents");
                    mappingService.mapCatalogContents("automatic_mapping_process");
                }
                else {
                    getLog().error("Current Ensembl release is " + latestEnsemblReleaseNumber +
                                           ". Database release number is set to " +
                                           currentEnsemblReleaseNumberInDatabase);
                }
            }
        }
    }

    /**
     * Method used to create and save new Ensembl release details in the database
     *
     * @param latestEnsemblReleaseNumber the latest Ensembl release number returned from Ensembl API
     */
    private void createMappingMetaData(int latestEnsemblReleaseNumber) {
        MappingMetadata newMappingMetadata = new MappingMetadata();
        newMappingMetadata.setEnsemblReleaseNumber(latestEnsemblReleaseNumber);
        newMappingMetadata.setUsageStartDate(new Date());
        mappingMetadataRepository.save(newMappingMetadata);
    }

    // Sort option
    private Sort sortByUsageStartDateDesc() {return new Sort(new Sort.Order(Sort.Direction.DESC, "usageStartDate"));}
}
