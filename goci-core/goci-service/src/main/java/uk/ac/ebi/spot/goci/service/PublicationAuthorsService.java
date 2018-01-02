package uk.ac.ebi.spot.goci.service;

import org.apache.xmlbeans.impl.xb.xsdschema.Public;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.model.Author;
import uk.ac.ebi.spot.goci.model.Publication;
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

    private Optional<PublicationAuthors> findOptionalByPrimaryKey(Author author, Publication publication) {
        PublicationAuthors entry = publicationAuthorsRepository.findByAuthorIdAndPublicationId(author.getId(),
                                   publication.getId());
        if (entry != null) {
            return Optional.of(entry);
        }
        return Optional.empty();
    }


    public PublicationAuthors findByPrimaryKey(Author author, Publication publication) {
        Optional<PublicationAuthors> entry = findOptionalByPrimaryKey(author, publication);
        if (entry.isPresent()){
            return entry.get();
        }
        return null;
    }

    public PublicationAuthors createOrFindByPrimaryKey(Author author, Publication publication) {
        PublicationAuthors entry = findByPrimaryKey(author, publication);
        if (entry == null) {
            entry = new PublicationAuthors(author,publication, 0);
        }
        return entry;
    }


    public void save(PublicationAuthors entry) {
        publicationAuthorsRepository.save(entry);
    }

    public void setSort(Author author, Publication publication, Integer sort) {
        PublicationAuthors entry = createOrFindByPrimaryKey(author, publication);
        entry.setSort(sort);
        save(entry);
    }
}
