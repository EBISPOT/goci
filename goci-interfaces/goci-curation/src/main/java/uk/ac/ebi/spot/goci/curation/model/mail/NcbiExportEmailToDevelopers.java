package uk.ac.ebi.spot.goci.curation.model.mail;

import uk.ac.ebi.spot.goci.model.GenericEmail;

public class NcbiExportEmailToDevelopers extends GenericEmail{

    public void createNCBIFTPEmail(String subject) {
        this.setBody("NCBI FTP file upload process. See the subject.");
        this.setSubject(subject);
    }
}
