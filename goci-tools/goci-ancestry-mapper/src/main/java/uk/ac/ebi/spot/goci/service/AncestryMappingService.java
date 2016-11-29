package uk.ac.ebi.spot.goci.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.model.AncestralGroupAnnotation;
import uk.ac.ebi.spot.goci.model.CoOAnnotation;
import uk.ac.ebi.spot.goci.model.CoRAnnotation;
import uk.ac.ebi.spot.goci.model.Ancestry;
import uk.ac.ebi.spot.goci.ontology.owl.OntologyLoader;
import uk.ac.ebi.spot.goci.repository.AncestryRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by dwelter on 06/10/15.
 */
@Service
public class AncestryMappingService {

    private AncestryRepository ancestryRepository;
    private OntologyLoader ontologyLoader;
    private OntologyExtractionService ontologyExtractionService;

    List<AncestralGroupAnnotation> ancestralGroups;
    List<CoOAnnotation> coOAnnotations;
    List<CoRAnnotation> coRAnnotations;

    @Autowired
    public AncestryMappingService(AncestryRepository ancestryRepository,
                                  OntologyLoader ontologyLoader,
                                  OntologyExtractionService ontologyExtractionService) {
        this.ancestryRepository = ancestryRepository;
        this.ontologyLoader = ontologyLoader;
        this.ontologyExtractionService = ontologyExtractionService;

        ancestralGroups = new ArrayList<AncestralGroupAnnotation>();
        coOAnnotations = new ArrayList<CoOAnnotation>();
        coRAnnotations = new ArrayList<CoRAnnotation>();
    }

    //    private Logger log = LoggerFactory.getLogger(getClass());
    //
    //    protected Logger getLog() {
    //        return log;
    //    }

    private Logger output = LoggerFactory.getLogger("output");

    protected Logger getOutput() {
        return output;
    }

    private Logger errors = LoggerFactory.getLogger("errors");

    protected Logger getErrors() {
        return errors;
    }

    public List<Ancestry> getAllAncestries() {
        List<Ancestry> ancestries = ancestryRepository.findAll();
        getErrors().info("Found " + ancestries.size() + " ancestry records");
        return ancestries;
    }

    public void printOutOntologyContent() {
        getErrors().info("Available ancestral groups:");
        Set<String> groups = ontologyExtractionService.getAncestralGroups().keySet();

        for (String grp : groups) {
            getErrors().info(grp);
        }

        getErrors().info("Available countries:");
        Set<String> countries = ontologyExtractionService.getCountries().keySet();

        for (String ctr : countries) {
            getErrors().info(ctr);
        }


    }

    public void processAncestries() {
        printOutOntologyContent();
        ;

        List<Ancestry> allAncestries = getAllAncestries();

        for (Ancestry ancestry : allAncestries) {

            Long id = ancestry.getId();
            String ancestralGroup = ancestry.getAncestralGroup();
            String coo = ancestry.getCountryOfOrigin();
            String cor = ancestry.getCountryOfRecruitment();

            if (ancestralGroup != null) {
                if (ancestralGroup.contains(",")) {
                    String[] groups = ancestralGroup.split(",");

                    for (String group : groups) {
                        mapAncestralGroup(id, group.toLowerCase());
                    }
                }
                else {
                    mapAncestralGroup(id, ancestralGroup.toLowerCase());
                }
            }

            if (coo != null) {
                if (coo.contains(",")) {
                    String[] countries = coo.split(",");

                    for (String country : countries) {
                        mapCoO(id, country.toLowerCase());
                    }
                }
                else {
                    mapCoO(id, coo.toLowerCase());
                }
            }

            if (cor != null) {
                if (cor.contains(",")) {
                    String[] countries = cor.split(",");

                    for (String country : countries) {
                        mapCoR(id, country.toLowerCase());
                    }
                }
                else {
                    mapCoR(id, cor.toLowerCase());
                }
            }

        }
        getErrors().info("All ancestries processed");

        printResult();
    }


    private void mapCoR(Long id, String country) {
        if (ontologyExtractionService.getCountryURI(country) != null) {
            coRAnnotations.add(new CoRAnnotation(id, country, ontologyExtractionService.getCountryURI(country)));
        }
        else {
            getErrors().info("No matching ontology found for CoR " + country + " (ID " + id + ")");
        }
    }

    private void mapCoO(Long id, String country) {
        if (ontologyExtractionService.getCountryURI(country) != null) {
            coOAnnotations.add(new CoOAnnotation(id, country, ontologyExtractionService.getCountryURI(country)));
        }
        else {
            getErrors().info("No matching ontology found for CoO " + country + " (ID " + id + ")");
        }
    }

    private void mapAncestralGroup(Long id, String group) {
        String label;
        if (group.contains("unspecified")) {
            label = group.split(" ")[0];
        }
        else {
            label = group;
        }
        if (ontologyExtractionService.getAncestralGroupURI(label) != null) {
            ancestralGroups.add(new AncestralGroupAnnotation(id,
                                                             group,
                                                             label,
                                                             ontologyExtractionService.getAncestralGroupURI(group)));
        }
        else {
            getErrors().info("No matching ontology found for ancestral group " + group + " (ID " + id + ")");
        }
    }

    public void printResult() {
        getOutput().info("Countries of origin: ");
        for (CoOAnnotation annot : coOAnnotations) {
            getOutput().info(annot.getId() + ", " + annot.getCountryOfOrigin() + ", " + annot.getOntologyURI());
        }

        getOutput().info("Countries of recruitment: ");

        for (CoRAnnotation annot : coRAnnotations) {
            getOutput().info(annot.getId() + ", " + annot.getCountryOfRecruitment() + ", " + annot.getOntologyURI());
        }

        getOutput().info("Ancestral groups: ");
        for (AncestralGroupAnnotation annot : ancestralGroups) {
            getOutput().info(annot.getId() + ", " + annot.getAncestralGroup() + ", " + annot.getOntologyLabel() + ", " +
                                     annot.getOntologyURI());
        }
    }
}
