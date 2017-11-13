package uk.ac.ebi.spot.goci.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.model.Author;
import uk.ac.ebi.spot.goci.model.PublicationAuthors;
import uk.ac.ebi.spot.goci.repository.PublicationAuthorsRepository;

import java.util.Optional;

@Service
public class PublicationAuthorsService {

    private PublicationAuthorsRepository publicationAuthorsRepository;

    @Autowired
    public PublicationAuthorsService(PublicationAuthorsRepository publicationAuthorsRepository ) {
        this.publicationAuthorsRepository = publicationAuthorsRepository;
    }

    private Optional<PublicationAuthors> findOptionalByPrimaryKey(Long author_id, Long publication_id) {
        PublicationAuthors entry = publicationAuthorsRepository.findByAuthorIdAndPublicationId(author_id, publication_id);
        if (entry != null) {
            return Optional.of(entry);
        }
        return Optional.empty();
    }


    public PublicationAuthors findByPrimaryKey(Long author_id, Long publication_id) {
        Optional<PublicationAuthors> entry = findOptionalByPrimaryKey(author_id, publication_id);
        if (entry.isPresent()){
            return entry.get();
        }
        return null;
    }

    public PublicationAuthors createOrFindByPrimaryKey(Long author_id, Long publication_id) {
        PublicationAuthors entry = findByPrimaryKey(author_id, publication_id);
        if (entry == null) {
            entry = new PublicationAuthors(author_id,publication_id, 0);
        }
        return entry;
    }


    public void save(PublicationAuthors entry) {
        publicationAuthorsRepository.save(entry);
    }

    public void setSort(Long author_id, Long publication_id, Integer sort) {
        PublicationAuthors entry = createOrFindByPrimaryKey(author_id, publication_id);
        entry.setSort(sort);
        save(entry);
    }
}
