package uk.ac.ebi.spot.goci.curation.service;

import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.curation.model.StudyAssociationTableView;
import uk.ac.ebi.spot.goci.model.Association;
import uk.ac.ebi.spot.goci.model.EfoTrait;
import uk.ac.ebi.spot.goci.model.Study;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by emma on 09/10/2015.
 *
 * @author emma
 *         <p>
 *         Service that takes a Study object and creates a view object that can be returned and rendered by HTML.
 */
@Service
public class StudyAssociationTableViewService {

    public StudyAssociationTableViewService() {
    }

    public List<StudyAssociationTableView> createViews(List<Study> studies) {

        List<StudyAssociationTableView> views = new ArrayList<>();

        for (Study study : studies) {

            StudyAssociationTableView view = new StudyAssociationTableView();

            view.setStudyId(study.getId());
            view.setAuthor(study.getAuthor());
            view.setPublicationDate(study.getPublicationDate());
            view.setPublication(study.getPublication());
            view.setTitle(study.getTitle());
            view.setPubmedId(study.getPubmedId());
            view.setCurator(study.getHousekeeping().getCurator().getLastName());
            view.setCurationStatus(study.getHousekeeping().getCurationStatus().getStatus());
            view.setNotes(study.getHousekeeping().getNotes());

            // Study disease trait, which could potentially be null
            if (study.getDiseaseTrait() != null) {
                view.setStudyDiseaseTrait(study.getDiseaseTrait().getTrait());
            }

            // Study EFO traits
            Collection<String> traitNames = new ArrayList<>();
            for (EfoTrait efoTrait : study.getEfoTraits()) {
                traitNames.add(efoTrait.getTrait());
            }
            view.setStudyEfoTrait(String.join(",", traitNames));

            // Association details
            Collection<Association> studyAssociations = study.getAssociations();
            view.setTotalNumberOfAssociations(studyAssociations.size());

            Integer multiSnpHaplotypeCount = 0;
            Integer snpInteractionCount = 0;

            // Using SET here as I don't want duplicates
            Set<String> allAssociationsEfoTraits = new HashSet<>();

            // Go through each association and find out its type and efo traits
            for (Association association : studyAssociations) {

                if (association.getMultiSnpHaplotype()) {
                    multiSnpHaplotypeCount++;
                }
                if (association.getSnpInteraction()) {
                    snpInteractionCount++;
                }

                for (EfoTrait efoTrait : association.getEfoTraits()) {
                    allAssociationsEfoTraits.add(efoTrait.getTrait());
                }
            }

            view.setNumberOfMultiSnpHaplotypeAssociations(multiSnpHaplotypeCount);
            view.setNumberOfSnpInteractiionAssociations(snpInteractionCount);
            view.setAssociationEfoTraits(String.join(",", allAssociationsEfoTraits));
            views.add(view);
        }
        return views;
    }
}
