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
 * @author Tony Burdett
 * @date 16/01/15
 */

@Service
public class PublicationService {

    private PublicationRepository publicationRepository;

    @Autowired
    public PublicationService(PublicationRepository publicationRepository) {
        this.publicationRepository = publicationRepository;
    }


    public Optional<Collection<Study>> getStudiesByPubmedId(String pubmedId) {
        Publication publication = publicationRepository.findByPubmedId(pubmedId);
        if (publication != null) {
            return Optional.of(publication.getStudies());
        }
        return Optional.empty();
    }

    public Collection<Study> findStudiesByPubmedId(String pubmedId) {
        return publicationRepository.findByPubmedId(pubmedId).getStudies();
    }

    public List<String> findAllStudyAuthors() {
        return publicationRepository.findAllStudyAuthors();
    }

}
