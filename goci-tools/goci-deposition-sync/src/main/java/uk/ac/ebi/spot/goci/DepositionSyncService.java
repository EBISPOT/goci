package uk.ac.ebi.spot.goci;

import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.spot.goci.model.*;
import uk.ac.ebi.spot.goci.model.deposition.DepositionAuthor;
import uk.ac.ebi.spot.goci.model.deposition.DepositionPublication;
import uk.ac.ebi.spot.goci.repository.CurationStatusRepository;
import uk.ac.ebi.spot.goci.repository.CuratorRepository;
import uk.ac.ebi.spot.goci.service.DepositionPublicationService;
import uk.ac.ebi.spot.goci.service.PublicationService;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class DepositionSyncService {
    private final CurationStatus curationComplete;
    private final Curator levelOneCurator;
    private final CurationStatus awaitingCuration;
    private final CurationStatus awaitingLiterature;
    private PublicationService publicationService;

    private DepositionPublicationService depositionPublicationService;

    private CurationStatusRepository statusRepository;

    private CuratorRepository curatorRepository;

    public DepositionSyncService(@Autowired PublicationService publicationService,
                                 @Autowired DepositionPublicationService depositionPublicationService,
                                 @Autowired CurationStatusRepository statusRepository,
                                 @Autowired CuratorRepository curatorRepository) {
        this.curatorRepository = curatorRepository;
        this.statusRepository = statusRepository;
        this.depositionPublicationService = depositionPublicationService;
        this.publicationService = publicationService;
        curationComplete = statusRepository.findByStatus("Publish study");
        levelOneCurator = curatorRepository.findByLastName("Level 1 Curator");
        awaitingCuration = statusRepository.findByStatus("Awaiting Curation");
        awaitingLiterature = statusRepository.findByStatus("Awaiting literature");
    }

    private boolean isPublished(Publication publication) {

        for (Study study : publication.getStudies()) {
            Housekeeping housekeeping = study.getHousekeeping();
            if (!housekeeping.getIsPublished()) {
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
            if (curationStatus == null) {
                return false;
            } else if (!curationStatus.getId().equals(awaitingCuration.getId()) &&
                    !curationStatus.getId().equals(curationComplete.getId()) &&
                    !curationStatus.getId().equals(awaitingLiterature.getId())) {
                return false;
            } else if (!curator.getId().equals(levelOneCurator.getId())) {
                return false;
            }
        }
        return true;
    }

    private boolean isWaiting(Publication publication) {

        for (Study study : publication.getStudies()) {
            Housekeeping housekeeping = study.getHousekeeping();
            CurationStatus curationStatus = housekeeping.getCurationStatus();
            Curator curator = housekeeping.getCurator();
            if (curationStatus == null) {
                return false;
            } else if (!curationStatus.getId().equals(awaitingLiterature.getId())) {
                return false;
            } else if (!curator.getId().equals(levelOneCurator.getId())) {
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
    public void syncPublications(boolean initialSync) {
        //read all publications from GOCI
        List<Publication> gociPublications = publicationService.findAll();
        //if publication not in Deposition, insert
        Map<String, DepositionPublication> depositionPublications = depositionPublicationService.getAllPublications();
        for (Publication p : gociPublications) {
            String pubmedId = p.getPubmedId();
            System.out.println("checking pmid " + pubmedId);
            boolean isPublished = isPublished(p);
            boolean isAvailable = isAvailable(p);
            DepositionPublication newPublication = createPublication(p);
            DepositionPublication depositionPublication = depositionPublications.get(pubmedId);
            if(initialSync) { // add all publications to mongo
                if (newPublication != null && depositionPublication == null) {
                    if(isPublished) {
                        System.out.println("adding published publication" + pubmedId + " to mongo");
                    }
                    else if(isAvailable) {
                        newPublication.setStatus("ELIGIBLE");
                    }else {
                        newPublication.setStatus("CURATION_STARTED");
                    }
                    depositionPublicationService.addPublication(newPublication);
                }
            }else {
                if(depositionPublication == null) { // add new publication
                    if (newPublication != null) {
                        if(isPublished) {
                            System.out.println("adding published publication" + pubmedId + " to mongo");
                        }
                        else if(isAvailable) {
                            newPublication.setStatus("ELIGIBLE");
                        }else {
                            newPublication.setStatus("CURATION_STARTED");
                        }
                        depositionPublicationService.addPublication(newPublication);
                    }
                }else {//check publication status, update if needed
                    if (isPublished && !depositionPublication.getStatus().equals("PUBLISHED")) { //sync newly
                        // published publications
                        System.out.println("setting publication status to PUBLISHED for " + pubmedId);
                        depositionPublication.setStatus("PUBLISHED");
                        depositionPublicationService.updatePublication(depositionPublication);
                    }else if(!isPublished && !isAvailable && depositionPublication.getStatus().equals("ELIGIBLE")) {
                        // sync in-work publications
                        System.out.println("setting publication status to CURATION_STARTED for " + pubmedId);
                        depositionPublication.setStatus("CURATION_STARTED");
                        depositionPublicationService.updatePublication(depositionPublication);
                    }
                }
            }

        }
    }

    /**
     * fix publications is intended as a one-off execution to correct errors with loaded data, not as part of the
     * daily sync
     */
    public void fixPublications() {
        //read all publications from GOCI
        List<Publication> gociPublications = publicationService.findAll();
        //if publication not in Deposition, insert
        Map<String, DepositionPublication> depositionPublications = depositionPublicationService.getAllPublications();
        for (Publication p : gociPublications) {
            String pubmedId = p.getPubmedId();
            System.out.println("checking pmid " + pubmedId);
            boolean waiting = isWaiting(p);
            DepositionPublication depositionPublication = depositionPublications.get(pubmedId);
            if(waiting && depositionPublication.getStatus().equals("CURATION_STARTED")){
                depositionPublication.setStatus("ELIGIBLE");
                depositionPublicationService.updatePublication(depositionPublication);
                System.out.println(pubmedId + " changing status from CURATION_STARTED to ELIGIBLE");
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
            Collection<PublicationAuthors> authorIterator = p.getPublicationAuthors();
            if (authorIterator != null) {
                Author correspondingAuthor = null;
                for(PublicationAuthors authors: authorIterator) {
                    correspondingAuthor = authors.getAuthor();
                    if(correspondingAuthor.getFullname().equals(correspondingAuthor.getFullname())) {
                        break;
                    }
                }
                DepositionAuthor depositionAuthor = new DepositionAuthor();
                if(correspondingAuthor.getLastName() == null || correspondingAuthor.getInitials() == null){
                    depositionAuthor
                            .setAuthorName(correspondingAuthor.getFullname());
                }else {
                    depositionAuthor
                            .setAuthorName(correspondingAuthor.getLastName() + " " + correspondingAuthor.getInitials());
                }
                newPublication.setCorrespondingAuthor(depositionAuthor);
            }else {
                System.out.println("error: publication " + p.getPubmedId() + " has no corresponding authors");
            }
            newPublication.setStatus("PUBLISHED");
        } else {
            System.out.println("error: publication " + p.getPubmedId() + " has no authors");
        }
        return newPublication;
    }
}
