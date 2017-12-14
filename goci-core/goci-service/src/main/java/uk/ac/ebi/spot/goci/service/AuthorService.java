package uk.ac.ebi.spot.goci.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.model.Author;
import uk.ac.ebi.spot.goci.model.Publication;
import uk.ac.ebi.spot.goci.model.PublicationAuthors;
import uk.ac.ebi.spot.goci.repository.AuthorRepository;

import java.util.Collection;
import java.util.Optional;

/**
 * AuthorService provides a list of methods to query the Repository
 *
 * @author Cinzia
 * @date 23/10/17
 */


@Service
public class AuthorService {

    private AuthorRepository authorRepository;

    private PublicationAuthorsService publicationAuthorsService;

    @Autowired
    public AuthorService(AuthorRepository authorRepository,
                         PublicationAuthorsService publicationAuthorsService){
        this.publicationAuthorsService = publicationAuthorsService;
        this.authorRepository = authorRepository;
    }

    private Optional<Author> findOptionalById(Long authorId) {
        Author author = authorRepository.findOne(authorId);
        return (author != null) ? Optional.of(author) : Optional.empty();
    }

    public Author findById(Long authorId) {
        Optional<Author> author= findOptionalById(authorId);
        return (author.isPresent()) ? author.get() : null;
    }


    private Optional<Author> findOptionalByFullname(String fullname) {
        Author author = authorRepository.findByFullname(fullname);
        return (author != null) ? Optional.of(author) : Optional.empty();
    }

    public Author findByFullname(String fullname) {
        Optional<Author> author= findOptionalByFullname(fullname);
        return (author.isPresent()) ? author.get() : null;
    }

    public void save(Author author) {
        authorRepository.save(author);
    }


    public void addPublication(Author author, Publication publication, Integer sort) {
        // DO NOT DO THIS EVER! SORT IS RESET.
        //Collection<Publication> publications = author.getPublications();
        //publications.add(publication);
        //author.setPublication(publications);
        save(author);
        publicationAuthorsService.setSort(author.getId(), publication.getId(), sort);
    }

}
