package uk.ac.ebi.spot.goci.curation.model.mail;

/**
 * Created by emma on 08/03/2016.
 *
 * @author emma
 *         <p>
 *         Concrete implementation of emails sent to developers
 */
public class CurationSystemEmailToDevelopers extends CurationSystemEmail {

    public void createReleaseChangeEmail(Integer currentEnsemblReleaseNumberInDatabase,
                                         int latestEnsemblReleaseNumber) {

        this.setSubject("New Ensembl Release Identified");
        this.setBody(
                "The latest Ensembl release is number "
                        + latestEnsemblReleaseNumber
                        + "."
                        + "\n"
                        + "The GWAS catalog is mapped to Ensembl release "
                        + currentEnsemblReleaseNumberInDatabase
                        + "."
                        + "\n\n"
                        + "Please remap all catalog associations."
                        + "\n\n"
                        + "An error report will be available here once mapping is complete: " + getLink() +
                        "mappingerrorreport");
    }

    public void createReleaseNotIdentifiedProblem() {
        this.setSubject("Problem Determining Latest Ensembl Release");
        this.setBody(
                "Problem identifying the latest Ensembl release, genome build version or dbSNP version via Ensembl REST API. Please check logs");
    }

    public void createEnsemblPingFailureMail() {
        this.setSubject("Ensembl Daily Ping Failed");
        this.setBody(
                "Daily ping of Ensembl API failed. The service may be down. Please check logs for further details.");
    }
}