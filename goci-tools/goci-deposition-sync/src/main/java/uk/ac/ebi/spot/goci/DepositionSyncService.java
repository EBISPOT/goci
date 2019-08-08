package uk.ac.ebi.spot.goci;

import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.spot.goci.curation.service.StudyOperationsService;
import uk.ac.ebi.spot.goci.model.*;
import uk.ac.ebi.spot.goci.repository.CurationStatusRepository;
import uk.ac.ebi.spot.goci.repository.CuratorRepository;
import uk.ac.ebi.spot.goci.service.DepositionPublicationService;
import uk.ac.ebi.spot.goci.service.PublicationService;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class DepositionSyncService {
    private final CurationStatus levelOneCurationComplete;
    private final Curator levelOneCurator;
    private final CurationStatus awaitingCuration;
    private PublicationService publicationService;

    private DepositionPublicationService depositionPublicationService;

    private CurationStatusRepository statusRepository;

    private CuratorRepository curatorRepository;

    private StudyOperationsService studyOperationsService;

    public DepositionSyncService(@Autowired StudyOperationsService studyOperationsService,
                                 @Autowired PublicationService publicationService,
                                 @Autowired DepositionPublicationService depositionPublicationService,
                                 @Autowired CurationStatusRepository statusRepository,
                                 @Autowired CuratorRepository curatorRepository) {
        this.studyOperationsService = studyOperationsService;
        this.curatorRepository = curatorRepository;
        this.statusRepository = statusRepository;
        this.depositionPublicationService = depositionPublicationService;
        this.publicationService = publicationService;
        levelOneCurationComplete = statusRepository.findByStatus("Publish study");
        levelOneCurator = curatorRepository.findByLastName("Level 1 Curator");
        awaitingCuration = statusRepository.findByStatus("Awaiting Curation");

    }

    private boolean isPublished(Publication publication) {

        for (Study study : publication.getStudies()) {
            if (study.getHousekeeping().getCurationStatus() == null ||
                    !study.getHousekeeping().getCurationStatus().equals(levelOneCurationComplete)) {
                return false;
            }
        }
        return true;
    }

    private boolean isAvailable(Publication publication) {

        for (Study study : publication.getStudies()) {
            Housekeeping housekeeping = study.getHousekeeping();
            CurationStatus curationStatus = housekeeping.getCurationStatus();
            Curator curator = housekeeping.getCurator();
            if (curationStatus == null || !(curationStatus.getId() == awaitingCuration.getId()) ||
                    !(curator.getId() == levelOneCurator.getId())) {
                return false;
            }
        }
        return true;
    }

    /**
     * syncPublications intends to keep publications syned between GOCI and Deposition. It checks the GOCI catalog
     * against Deposition. If a publication is in GOCI but not in Deposition, it checks the state of the studies in
     * GOCI.
     * If the study is awaiting curation (assigned to Level 1 Curator + Awating Curation status), the publication is
     * sent
     * to Deposition as AVAILABLE. If the study is published (Publish Study status) the publication is sent to
     * Deposition
     * as EXPORTED and a submission is created containing the study data from GOCI.
     * This is the reverse of the Import endpoint, where a submission is received from Curation and added into GOCI.
     */
    public void syncPublications(boolean syncAll) {
        //read all publications from GOCI
        List<Publication> gociPublications = publicationService.findAll();
        Map<String, DepositionPublication> depositionPublications = depositionPublicationService.getAllPublications();
        //if publication not in Deposition, insert
        for (Publication p : gociPublications) {
            String pubmedId = p.getPubmedId();
            System.out.println("checking pmid " + pubmedId);
            boolean isPublished = isPublished(p);
            boolean isAvailable = isAvailable(p);
            if (isPublished) {
                DepositionPublication depositionPublication = depositionPublications.get(pubmedId);
                if (depositionPublication == null) {
                    DepositionPublication newPublication = createPublication(p);
                    if (newPublication != null) {
                        System.out.println("adding published publication" + pubmedId + " to mongo");
                        newPublication.setStatus("EXPORTED");
                        depositionPublicationService.addPublication(newPublication);
                    }
                }
            } else if (isAvailable && syncAll) {
                DepositionPublication depositionPublication = depositionPublications.get(pubmedId);
                if (depositionPublication == null) {
                    DepositionPublication newPublication = createPublication(p);
                    if (newPublication != null) {
                        System.out.println("adding new publication" + pubmedId + " to mongo");
                        newPublication.setStatus("AVAILABLE");
                        depositionPublicationService.addPublication(newPublication);
                    }
                }
            }
        }
    }

    private DepositionPublication createPublication(Publication p) {
        Author author = p.getFirstAuthor();
        DepositionPublication newPublication = null;
        if (author != null) { //error check for invalid publications
            newPublication = new DepositionPublication();
            newPublication.setPmid(p.getPubmedId());
            newPublication.setFirstAuthor(author.getLastName() + " " + author.getInitials());
            newPublication.setPublicationDate(new LocalDate(p.getPublicationDate()));
            newPublication.setPublicationId(p.getId().toString());
            newPublication.setTitle(p.getTitle());
            newPublication.setJournal(p.getPublication());
            Iterator<Author> authorIterator = p.getAuthors().iterator();
            if (authorIterator != null) {
                Author correspondingAuthor = authorIterator.next();
                DepositionAuthor depositionAuthor = new DepositionAuthor();
                depositionAuthor
                        .setAuthorName(correspondingAuthor.getLastName() + " " + correspondingAuthor.getInitials());
                newPublication.setCorrespondingAuthor(depositionAuthor);
            }
        } else {
            System.out.println("error: publication " + p.getPubmedId() + " has no authors");
        }
        return newPublication;
    }
}
