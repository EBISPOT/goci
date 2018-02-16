package uk.ac.ebi.spot.goci.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.model.Author;
import uk.ac.ebi.spot.goci.model.Publication;
import uk.ac.ebi.spot.goci.repository.AuthorRepository;

import java.util.List;
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


    private Optional<Author> findOptionalUniqueAuthor(String fullname, String firstName, String lastName,
                                                    String initial, String affiliation) {
        Author author = authorRepository.findByFullnameAndFirstNameAndLastNameAndInitialsAndAffiliation(fullname, firstName,
                lastName, initial, affiliation);
        return (author != null) ? Optional.of(author) : Optional.empty();
    }


    public Author findUniqueAuthor(String fullname, String firstName, String lastName,
                                   String initial, String affiliation){
        Optional<Author> author= findOptionalUniqueAuthor(fullname, firstName, lastName, initial, affiliation);
        return (author.isPresent()) ? author.get() : null;

    }

    public List<Author> findAll() {
        return authorRepository.findAll();
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
        publicationAuthorsService.setSort(author, publication, sort);
    }

}
