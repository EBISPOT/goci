package uk.ac.ebi.spot.goci.curation.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.model.Author;
import uk.ac.ebi.spot.goci.model.Publication;
import uk.ac.ebi.spot.goci.service.EuropepmcPubMedSearchService;
import uk.ac.ebi.spot.goci.service.PublicationService;
import uk.ac.ebi.spot.goci.utils.EuropePMCData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * PublicationOperationService provides a list of methods to add/update the publication and the relative authors.
 *
 * @author Cinzia
 * @date 23/10/17
 */


@Service
public class PublicationOperationsService {


    private PublicationService publicationService;

    private AuthorOperationsService authorOperationsService;

    private EuropepmcPubMedSearchService europepmcPubMedSearchService;

    @Autowired
    public PublicationOperationsService(PublicationService publicationService,
                                        AuthorOperationsService authorOperationsService,
                                        EuropepmcPubMedSearchService europepmcPubMedSearchService){
        this.publicationService = publicationService;
        this.europepmcPubMedSearchService = europepmcPubMedSearchService;
        this.authorOperationsService = authorOperationsService;
    }


    public List<String> listFirstAuthors() {
     return publicationService.findAllStudyAuthors();
    }



    public void addFirstAuthorToPublication(Publication publication, EuropePMCData europePMCResult) {
        Author firstAuthor = europePMCResult.getFirstAuthor();
        Author firstAuthorDB = authorOperationsService.findAuthorByFullname(firstAuthor.getFullname());
        publication.setFirstAuthor(firstAuthorDB);
        publicationService.save(publication);
    }

    public Publication addPublication(String pubmedId, EuropePMCData europePMCResult) throws Exception {

        Publication publication = publicationService.createOrFindByPumedId(pubmedId);
        publication.setPublication(europePMCResult.getPublication().getPublication());
        publication.setPublicationDate(europePMCResult.getPublication().getPublicationDate());
        publication.setTitle(europePMCResult.getPublication().getTitle());

        publicationService.save(publication);
        authorOperationsService.addAuthorsToPublication(publication, europePMCResult);
        addFirstAuthorToPublication(publication,europePMCResult);

        System.out.println("=======");

        return publication;
    }

    public Boolean reImportAllPublication() {
        ArrayList<HashMap<String,String>> result = new ArrayList<>();
        String pubmedId;

        List<Publication> allPublications = publicationService.findAll();

        for (Publication publication : allPublications) {
            pubmedId = publication.getPubmedId();
            //pubmedId = "27927641";
            System.out.println("Retriving Pubmed: "+pubmedId+"---");

            try {
                EuropePMCData europePMCResult = europepmcPubMedSearchService.createStudyByPubmed(pubmedId);
                Publication addedPublication = addPublication(pubmedId, europePMCResult);
            } catch (Exception exception) {
                HashMap<String, String> pubmedResult = new HashMap<String, String>();
                pubmedResult.put(pubmedId,"Something happend. TO DO imporve");
                result.add(pubmedResult);
            }
        }
        return true;

    }
}
