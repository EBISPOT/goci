package uk.ac.ebi.spot.goci.curation.service;


import org.apache.http.auth.AUTH;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.model.Author;
import uk.ac.ebi.spot.goci.model.Publication;
import uk.ac.ebi.spot.goci.service.AuthorService;
import uk.ac.ebi.spot.goci.utils.EuropePMCData;

import java.util.Collection;


/**
 * AuthorOperationService provides a list of methods to add/update the authors.
 *
 * @author Cinzia
 * @date 23/10/17
 */

@Service
public class AuthorOperationsService {

    private AuthorService authorService;

    @Autowired
    public AuthorOperationsService(AuthorService authorService) {
        this.authorService = authorService;
    }


    public Author findAuthorByFullname(String fullname) {
        Author author = authorService.findByFullname(fullname);
        return author;
    }

    public void addAuthorsToPublication(Publication publication, EuropePMCData europePMCResult) {
        Collection<Author> authorList = europePMCResult.getAuthors();
        for (Author author : authorList) {
            Author authorDB = findAuthorByFullname(author.getFullname());
            if (authorDB == null) {
                author.setPublication(publication);
                authorService.save(author);
            } else {
                authorDB.setPublication(publication);
                authorService.save(authorDB);
            }
        }
    }


}
