package uk.ac.ebi.spot.goci.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.model.DiseaseTrait;
import uk.ac.ebi.spot.goci.model.DiseaseTraitDocument;
import uk.ac.ebi.spot.goci.repository.DiseaseTraitRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DiseaseTraitDocumentCache {
    private Map<String, DiseaseTraitDocument> documentMap = new HashMap<>();

    private DiseaseTraitRepository diseaseTraitRepository;

    public DiseaseTraitDocumentCache(@Autowired DiseaseTraitRepository diseaseTraitRepository) {
        this.diseaseTraitRepository = diseaseTraitRepository;
        initCache();
    }

    private void initCache(){
        System.out.println("caching disease traits...");
        List<DiseaseTrait> traits = diseaseTraitRepository.findAll();
        for(DiseaseTrait trait: traits){
            DiseaseTraitDocument document = new DiseaseTraitDocument(trait);
            documentMap.put(trait.getTrait(), document);
        }
        System.out.println("disease traits cached: " + documentMap.size());
    }

    public DiseaseTraitDocument getDocument(String traitId){
        return documentMap.get(traitId);
    }
}
