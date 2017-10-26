package uk.ac.ebi.spot.goci.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.model.Author;
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

    @Autowired
    public AuthorService(AuthorRepository authorRepository){
        this.authorRepository = authorRepository;
    }

    private Optional<Author> findOptionalByFullname(String fullname) {
        Author author = authorRepository.findByFullname(fullname);
        if (author != null) {
            return Optional.of(author);
        }
        return Optional.empty();
    }


    public Author findByFullname(String fullname) {
        Optional<Author> author= findOptionalByFullname(fullname);
        if (author.isPresent()){
            return author.get();
        }
        return null;
    }

    public void save(Author author) {
        authorRepository.save(author);
    }


}
