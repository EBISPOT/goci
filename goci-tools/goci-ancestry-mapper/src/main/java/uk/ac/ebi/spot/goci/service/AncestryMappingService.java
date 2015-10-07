package uk.ac.ebi.spot.goci.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.model.AncestralGroupAnnotation;
import uk.ac.ebi.spot.goci.model.CoOAnnotation;
import uk.ac.ebi.spot.goci.model.CoRAnnotation;
import uk.ac.ebi.spot.goci.model.Ethnicity;
import uk.ac.ebi.spot.goci.ontology.owl.OntologyLoader;
import uk.ac.ebi.spot.goci.repository.EthnicityRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dwelter on 06/10/15.
 */
@Service
public class AncestryMappingService {

    private EthnicityRepository ethnicityRepository;
    private OntologyLoader ontologyLoader;
    private OntologyExtractionService ontologyExtractionService;

    List<AncestralGroupAnnotation> ancestralGroups;
    List<CoOAnnotation> coOAnnotations;
    List<CoRAnnotation> coRAnnotations;

    @Autowired
    public AncestryMappingService(EthnicityRepository ethnicityRepository,
                                  OntologyLoader ontologyLoader,
                                  OntologyExtractionService ontologyExtractionService){
        this.ethnicityRepository = ethnicityRepository;
        this.ontologyLoader = ontologyLoader;
        this.ontologyExtractionService = ontologyExtractionService;

        ancestralGroups = new ArrayList<AncestralGroupAnnotation>();
        coOAnnotations = new ArrayList<CoOAnnotation>();
        coRAnnotations = new ArrayList<CoRAnnotation>();
    }

    private Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    public List<Ethnicity> getAllAncestries(){
        List<Ethnicity> ancestries = ethnicityRepository.findAll();
        getLog().debug("Found " + ancestries.size() + " ancestry records");
        return  ancestries;
    }

    public void processAncestries() {

        List<Ethnicity> allAncestries = getAllAncestries();

        for(Ethnicity ancestry : allAncestries){

            Long id = ancestry.getId();
            String ancestralGroup = ancestry.getEthnicGroup();
            String coo = ancestry.getCountryOfOrigin();
            String cor = ancestry.getCountryOfRecruitment();

            if(ancestralGroup != null) {
                if (ancestralGroup.contains(",")) {
                    String[] groups = ancestralGroup.split(",");

                    for (String group : groups) {
                        mapAncestralGroup(id, group);
                    }
                } else {
                    mapAncestralGroup(id, ancestralGroup);
                }
            }

            if(coo != null ) {
                if (coo.contains(",")) {
                    String[] countries = coo.split(",");

                    for (String country : countries) {
                        mapCoO(id, country);
                    }
                } else {
                    mapCoO(id, coo);
                }
            }

            if(cor != null) {
                if (cor.contains(",")) {
                    String[] countries = cor.split(",");

                    for (String country : countries) {
                        mapCoR(id, country);
                    }
                } else {
                    mapCoR(id, cor);
                }
            }

        }
        getLog().debug("All ancestries processed");

        printResult();
    }



    private void mapCoR(Long id, String country) {
        if(ontologyExtractionService.getCountryURI(country) != null){
            coRAnnotations.add(new CoRAnnotation(id, country, ontologyExtractionService.getCountryURI(country)));
        }
        else {
            getLog().debug("No matching ontology found for CoR " + country + " (ID) " + id);
        }
    }

    private void mapCoO(Long id, String country) {
        if(ontologyExtractionService.getCountryURI(country) != null){
            coOAnnotations.add(new CoOAnnotation(id, country, ontologyExtractionService.getCountryURI(country)));
        }
        else {
            getLog().debug("No matching ontology found for CoO " + country + " (ID) " + id);
        }
    }

    private void mapAncestralGroup(Long id, String group) {
        if(ontologyExtractionService.getCountryURI(group) != null){
            ancestralGroups.add(new AncestralGroupAnnotation(id, group, ontologyExtractionService.getAncestralGroupURI(group)));
        }
        else {
            getLog().debug("No matching ontology found for ancestral group " + group + " (ID " + id + ")");
        }
    }

    public void printResult() {
        System.out.println("Countries of origin: ");
        for(CoOAnnotation annot : coOAnnotations){
            System.out.println(annot.getId() + " " + annot.getCountryOfOrigin() + " " + annot.getOntologyURI());
        }

        System.out.println("Countries of recruitment: ");

        for(CoRAnnotation annot : coRAnnotations){
            System.out.println(annot.getId() + " " + annot.getCountryOfRecruitment() + " " + annot.getOntologyURI());
        }

        System.out.println("Ancestral groups: ");
        for(AncestralGroupAnnotation annot : ancestralGroups){
            System.out.println(annot.getId() + " " + annot.getEthnicGroup() + " " + annot.getOntologyURI());
        }
    }
}
