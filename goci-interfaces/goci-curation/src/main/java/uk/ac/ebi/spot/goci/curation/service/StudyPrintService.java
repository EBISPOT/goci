package uk.ac.ebi.spot.goci.curation.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.curation.model.SnpAssociationTableView;
import uk.ac.ebi.spot.goci.model.Association;
import uk.ac.ebi.spot.goci.model.Study;
import uk.ac.ebi.spot.goci.repository.AssociationRepository;
import uk.ac.ebi.spot.goci.repository.StudyRepository;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by emma on 06/06/2016.
 *
 * @author emma
 *         <p>
 *         Service to create a printable view of all study details
 */
@Service
public class StudyPrintService {

    private StudyRepository studyRepository;
    private AssociationRepository associationRepository;
    private SnpAssociationTableViewService snpAssociationTableViewService;

    @Autowired
    public StudyPrintService(StudyRepository studyRepository,
                             AssociationRepository associationRepository,
                             SnpAssociationTableViewService snpAssociationTableViewService) {
        this.studyRepository = studyRepository;
        this.associationRepository = associationRepository;
        this.snpAssociationTableViewService = snpAssociationTableViewService;
    }

    public Collection<SnpAssociationTableView> generatePrintView(Long studyId) {
        // Get relevant study details
        Study studyToView = studyRepository.findOne(studyId);

        // Association information
        Collection<Association> associations = new ArrayList<>();
        associations.addAll(associationRepository.findByStudyId(studyId));

        // For our associations create a table view object and return
        Collection<SnpAssociationTableView> snpAssociationTableViews = new ArrayList<SnpAssociationTableView>();
        for (Association association : associations) {
            SnpAssociationTableView snpAssociationTableView =
                    snpAssociationTableViewService.createSnpAssociationTableView(association);
            snpAssociationTableViews.add(snpAssociationTableView);
        }
        return snpAssociationTableViews;
    }
}