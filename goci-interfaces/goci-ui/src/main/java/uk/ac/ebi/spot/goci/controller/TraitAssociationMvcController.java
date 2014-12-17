package uk.ac.ebi.spot.goci.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import uk.ac.ebi.spot.goci.model.SingleNucleotidePolymorphism;
import uk.ac.ebi.spot.goci.repository.SnpRepository;
import uk.ac.ebi.spot.goci.repository.StudyRepository;
import uk.ac.ebi.spot.goci.repository.TraitAssociationRepository;
import uk.ac.ebi.spot.goci.model.Study;
import uk.ac.ebi.spot.goci.model.TraitAssociation;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Javadocs go here!
 *
 * @author Tony Burdett
 * @date 14/11/14
 */
@Controller
public class TraitAssociationMvcController {
    private TraitAssociationRepository traitAssociationRepository;

    private StudyRepository studyRepository;
    private SnpRepository snpRepository;

    @Autowired
    TraitAssociationMvcController(TraitAssociationRepository traitAssociationRepository,
                                  StudyRepository studyRepository,
                                  SnpRepository snpRepository) {
        this.traitAssociationRepository = traitAssociationRepository;
        this.studyRepository = studyRepository;
        this.snpRepository = snpRepository;
    }

    @RequestMapping(value = "/traitAssociations", produces = MediaType.TEXT_HTML_VALUE)
    String snpTraits(Model model,
                     @RequestParam(required = false) String rsId,
                     @RequestParam(required = false) String pubmedId) {
        // rsId trumps pubmedId
        if (rsId != null) {
            SingleNucleotidePolymorphism snp = snpRepository.findByRsId(rsId);
            model.addAttribute("traits", traitAssociationRepository.findBySnp(snp));
        }
        else {
            if (pubmedId != null) {
                Study study = studyRepository.findByPubmedId(pubmedId);
                Collection<TraitAssociation> traitAssociations = new ArrayList<>();
                traitAssociations.addAll(traitAssociationRepository.findByStudy(study));
                model.addAttribute("traits", traitAssociations);
            }
            else {
                model.addAttribute("traits", traitAssociationRepository.findAll());
            }
        }

        return "traits";
    }
}
