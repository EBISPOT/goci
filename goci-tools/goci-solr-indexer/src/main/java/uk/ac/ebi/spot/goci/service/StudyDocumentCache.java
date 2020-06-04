package uk.ac.ebi.spot.goci.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.model.Study;
import uk.ac.ebi.spot.goci.model.StudyDocument;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Service
public class StudyDocumentCache {
    private Map<Long, StudyDocument> documentMap = new HashMap<>();

    public StudyDocumentCache() {
    }

    public boolean hasDocument(Long id){return documentMap.containsKey(id);}
    public void addDocument(Long id, StudyDocument studyDocument){
        documentMap.put(id, studyDocument);
    }

    public StudyDocument getDocument(Long id){
        return documentMap.get(id);
    }

}
