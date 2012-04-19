package uk.ac.ebi.fgpt.goci.service;

import uk.ac.ebi.fgpt.goci.dao.GociStudyDAO;
import uk.ac.ebi.fgpt.goci.model.GociStudy;

/**
 * A default tracker service that uses a study dao to enter new studies into the underlying datasource
 *
 * @author Tony Burdett
 * Date 27/10/11
 */
public class DefaultGociTrackerService implements GociTrackerService {
    private GociStudyDAO studyDAO;

    public GociStudyDAO getStudyDAO() {
        return studyDAO;
    }

    public void setStudyDAO(GociStudyDAO studyDAO) {
        this.studyDAO = studyDAO;
    }

    public void enterStudy(GociStudy study) {
        getStudyDAO().saveStudy(study);
    }

    public boolean isStudyEntered(String pubmedID) {
        return getStudyDAO().getStudyByPubMedID(pubmedID) != null;
    }
}
