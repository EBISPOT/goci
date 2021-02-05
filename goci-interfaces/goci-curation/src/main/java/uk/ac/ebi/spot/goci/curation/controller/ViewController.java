package uk.ac.ebi.spot.goci.curation.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import uk.ac.ebi.spot.goci.model.DiseaseTrait;
import uk.ac.ebi.spot.goci.model.projection.DiseaseTraitProjection;
import uk.ac.ebi.spot.goci.repository.DiseaseTraitRepository;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Controller
class ViewController {

    @Autowired
    private DiseaseTraitRepository diseaseTraitRepository;
    private static final String VALUE = "value";


    @GetMapping("/disease-traits")
    public String masterdetailPage(Model model) {

        List<Map<String, Object>> dataList = new ArrayList<>();
        List<DiseaseTraitProjection> projections =
                diseaseTraitRepository.findAllOrOrderByStudiesLargest(new PageRequest(0, 30));

        projections.forEach(x->{
            Map<String, Object> result = new HashMap<>();
            result.put("trait", x.getTrait());
            result.put("studies", x.getStudiesCount());
            dataList.add(result);
        });

        List<Long> ids = new ArrayList<>();
        AtomicInteger count = new AtomicInteger(1);
        projections.forEach(projection -> {
            if (count.get() <= 4){
                ids.add(projection.getId());
                count.getAndIncrement();
            }
        });

        List<DiseaseTrait> diseaseTraits = diseaseTraitRepository.findAllByIdIsIn(ids);
        List<Object> parents = new ArrayList<>();
        diseaseTraits.forEach(diseaseTrait -> {
            String trait = diseaseTrait.getTrait();
            List<Map<String, Object>> children = new ArrayList<>();
            diseaseTrait.getStudies().forEach(study -> {
                Map<String, Object> child = new HashMap<>();
                child.put("name", study.getPublicationId().getPubmedId());
                child.put(VALUE, 40);
                children.add(child);
            });

            Map<String, Object> parent = new LinkedHashMap<>();
            parent.put("name", trait+" ("+children.size()+")");
            parent.put(VALUE, 102);
            parent.put("children", children);
            parents.add(parent);
        });


        List<Object> parents2 = new ArrayList<>();
        Map<String, Object> parenta = new LinkedHashMap<>();

        parenta.put("name", "core");
        parenta.put(VALUE, "10");
        parenta.put("children", parents);
        parents2.add(parenta);

        model.addAttribute("dataList", dataList);
        model.addAttribute("mapData", parents2);
        return "disease-traits";
    }
}