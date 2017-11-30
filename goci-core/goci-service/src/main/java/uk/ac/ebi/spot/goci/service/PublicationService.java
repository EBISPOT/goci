package uk.ac.ebi.spot.goci.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.spot.goci.model.Publication;
import uk.ac.ebi.spot.goci.model.Study;
import uk.ac.ebi.spot.goci.repository.PublicationRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Sort;

/**
 * PublicationService provides a list of methods to query the Repository
 *
 * @author Cinzia
 * @date 16/10/17
 */

@Service
public class PublicationService {

    private PublicationRepository publicationRepository;

    @Autowired
    public PublicationService(PublicationRepository publicationRepository) {
        this.publicationRepository = publicationRepository;
    }


    public Optional<Publication> findOptionalByPubmedId(String pubmedId) {
        Publication publication = publicationRepository.findByPubmedId(pubmedId);
        if (publication != null) {
            return Optional.of(publication);
        }
        return Optional.empty();
    }

    public Optional<Collection<Study>> findOptionalStudiesByPubmedId(String pubmedId) {
        Publication publication = publicationRepository.findByPubmedId(pubmedId);
        if (publication != null) {
            return Optional.of(publication.getStudies());
        }
        return Optional.empty();
    }


    public Publication findByPumedId(String pubmedId) {
        Optional<Publication> publication= findOptionalByPubmedId(pubmedId);
        if (publication.isPresent()){
            return publication.get();
        }
        return null;
    }

    public Publication createOrFindByPumedId(String pubmedId) {
        Optional<Publication> publication= findOptionalByPubmedId(pubmedId);
        if (publication.isPresent()){
            return publication.get();
        }
        return new Publication();
    }

    public Collection<Study> findStudiesByPubmedId(String pubmedId) {
        Optional<Collection<Study>> listStudies= findOptionalStudiesByPubmedId(pubmedId);
        if (listStudies.isPresent()){
            return listStudies.get();
        }
        return null;
    }

    public List<Publication> findAll() {
        Sort ascPubmedId = new Sort(new Sort.Order(Sort.Direction.DESC, "pubmedId"));
        return publicationRepository.findAll(ascPubmedId);
    }

    public List<String> findAllStudyAuthors() {
        return publicationRepository.findAllStudyAuthors();
    }

    public List<Publication> findByFirstAuthorIsNull() {
        return  publicationRepository.findByFirstAuthorIsNull();
    }

    public void save(Publication publication) {
        publicationRepository.save(publication);
    }

}
