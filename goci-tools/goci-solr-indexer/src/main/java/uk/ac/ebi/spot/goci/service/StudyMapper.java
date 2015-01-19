package uk.ac.ebi.spot.goci.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.index.StudyIndex;
import uk.ac.ebi.spot.goci.model.Study;
import uk.ac.ebi.spot.goci.model.StudyDocument;

/**
 * Javadocs go here!
 *
 * @author Tony Burdett
 * @date 19/01/15
 */
@Service
public class StudyMapper extends ObjectDocumentMapper<Study, StudyDocument> {
    @Autowired
    public StudyMapper(StudyIndex studyIndex) {
        super(StudyDocument.class, studyIndex);
    }
}
