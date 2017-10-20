package uk.ac.ebi.spot.goci.curation.service.reports;

import org.apache.xmlbeans.impl.xb.xsdschema.Public;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.model.Author;
import uk.ac.ebi.spot.goci.model.Publication;
import uk.ac.ebi.spot.goci.model.Study;
import uk.ac.ebi.spot.goci.service.EuropepmcPubMedSearchService;
import uk.ac.ebi.spot.goci.service.PublicationService;
import uk.ac.ebi.spot.goci.utils.EuropePMCData;

import javax.swing.text.html.Option;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PublicationOperationsService {


    private PublicationService publicationService;

    private EuropepmcPubMedSearchService europepmcPubMedSearchService;

    @Autowired
    public PublicationOperationsService(PublicationService publicationService,
                                        EuropepmcPubMedSearchService europepmcPubMedSearchService){
        this.publicationService = publicationService;
        this.europepmcPubMedSearchService = europepmcPubMedSearchService;
    }


    public List<String> listFirstAuthors() {
     return publicationService.findAllStudyAuthors();
    }


    public Publication addPublication(String pubmedId, EuropePMCData europePMCResult) throws Exception {

        Publication publication = publicationService.createOrFindByPumedId(pubmedId);
        publication.setPublication(europePMCResult.getPublication().getPublication());
        publication.setPublicationDate(europePMCResult.getPublication().getPublicationDate());
        publication.setTitle(europePMCResult.getPublication().getTitle());

        // Can be an exist publication or a new. Id needed.
        publicationService.save(publication);

        Collection<Author> authorList = europePMCResult.getAuthors();
        for (Author author : authorList){
            Author authorDB = authorRepository.findByFullname(author.getFullname());
            //System.out.println(author.getFullname());
            if (authorDB == null) {
                author.setPublication(chi);
                authorRepository.save(author);
            }
            else {
                authorDB.setPublication(chi);
                authorRepository.save(authorDB);
            }
        }
        Author firstAuthor = createdStudyByPubmedId.getFirstAuthor();
        Author firstAuthorDB = authorRepository.findByFullname(firstAuthor.getFullname());
        chi.setFirstAuthor(firstAuthorDB);
        publicationRepository.save(chi);
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
