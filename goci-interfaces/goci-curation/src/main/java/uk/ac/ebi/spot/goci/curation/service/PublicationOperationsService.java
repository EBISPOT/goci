package uk.ac.ebi.spot.goci.curation.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.curation.exception.NoStudyDirectoryException;
import uk.ac.ebi.spot.goci.model.Author;
import uk.ac.ebi.spot.goci.model.Publication;
import uk.ac.ebi.spot.goci.model.SecureUser;
import uk.ac.ebi.spot.goci.model.Study;
import uk.ac.ebi.spot.goci.service.*;
import uk.ac.ebi.spot.goci.service.exception.PubmedLookupException;
import uk.ac.ebi.spot.goci.utils.EuropePMCData;

import javax.transaction.Transactional;
import java.util.*;

/**
 * PublicationOperationService provides a list of methods to add/update the publication and the relative authors.
 *
 * @author Cinzia
 * @date 23/10/17
 */


@Service
public class PublicationOperationsService {


    private PublicationService publicationService;

    private StudyService studyService;

    private AuthorService authorService;

    private AuthorOperationsService authorOperationsService;

    private EuropepmcPubMedSearchService europepmcPubMedSearchService;

    private StudyOperationsService studyOperationsService;

    private StudyFileService studyFileService;

    private PublicationAuthorsService publicationAuthorsService;

    private Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    @Autowired
    public PublicationOperationsService(PublicationService publicationService,
                                        AuthorService authorService,
                                        AuthorOperationsService authorOperationsService,
                                        EuropepmcPubMedSearchService europepmcPubMedSearchService,
                                        StudyService studyService,
                                        StudyOperationsService studyOperationsService,
                                        StudyFileService studyFileService,
                                        PublicationAuthorsService publicationAuthorsService){
        this.publicationService = publicationService;
        this.europepmcPubMedSearchService = europepmcPubMedSearchService;
        this.authorService = authorService;
        this.authorOperationsService = authorOperationsService;
        this.studyService = studyService;
        this.studyFileService = studyFileService;
        this.studyOperationsService = studyOperationsService;
        this.publicationAuthorsService = publicationAuthorsService;
    }


    public List<String> listFirstAuthors() {
     return publicationService.findAllStudyAuthors();
    }


    public void addFirstAuthorToPublication(Publication publication, EuropePMCData europePMCResult) {
        Author firstAuthor = europePMCResult.getFirstAuthor();
        Author firstAuthorDB = authorService.findUniqueAuthor(firstAuthor.getFullname(),firstAuthor.getFirstName(),
                                             firstAuthor.getLastName(),firstAuthor.getInitials(),
                                             firstAuthor.getAffiliation());
        publication.setFirstAuthor(firstAuthorDB);
        publicationService.save(publication);
    }

    @Transactional
    public Publication addPublication(String pubmedId, EuropePMCData europePMCResult, Boolean newImport)
            throws Exception {

        Publication publication = publicationService.createOrFindByPumedId(pubmedId);
        publication.setPubmedId(pubmedId);
        publication.setPublication(europePMCResult.getPublication().getPublication());
        publication.setTitle(europePMCResult.getPublication().getTitle());

        // The date was already curated. So we don't want to import again this data
        if (newImport) {
            publication.setPublicationDate(europePMCResult.getPublication().getPublicationDate());
        }

        publicationService.save(publication);
        authorOperationsService.addAuthorsToPublication(publication, europePMCResult);

        addFirstAuthorToPublication(publication, europePMCResult);

        return publication;
    }


    public Publication importSinglePublication(String pubmedId, Boolean newImport) throws PubmedLookupException {
        Publication addedPublication = null;
        try {
            EuropePMCData europePMCResult = europepmcPubMedSearchService.createStudyByPubmed(pubmedId);
            addedPublication = addPublication(pubmedId, europePMCResult, newImport);
        } catch(Exception exception) {
            throw new PubmedLookupException(exception.getCause().getMessage());

        }
        return addedPublication;
    }


    public Boolean importPublicationsWithoutFirstAuthor() {
        ArrayList<HashMap<String,String>> result = new ArrayList<>();
        String pubmedId;

        List<Publication> listPublications = publicationService.findByFirstAuthorIsNull();

        for (Publication publication : listPublications) {
            pubmedId = publication.getPubmedId();
            try {
                    Publication importedPublication = importSinglePublication(pubmedId, false);
            } catch (Exception exception) {
                System.out.println("Something went wrong "+ pubmedId );
                
            }
        }
        return true;

    }


    // Import the a list of pubmedid
    public ArrayList<HashMap<String,String>> importNewPublications(String pubmedIdList, SecureUser currentUser) {
        ArrayList<HashMap<String,String>> listPublications = new ArrayList<>();

        String regex = "[0-9, /,]+";
        // Remove whitespace
        String pubmedIdListTrim = pubmedIdList.trim();

        if (!pubmedIdListTrim.matches(regex)) {
            HashMap<String, String> error = new HashMap<String, String>();
            error.put("pubmedId", "general");
            error.put("error", "Pubmed List must be number follow by comma");
            listPublications.add(error);
            return listPublications;
        }


        List<String> pubmedIds = Arrays.asList(pubmedIdListTrim.split("\\s*,\\s*"));
        for (String pubmedId: pubmedIds) {
            HashMap<String, String> pubmedResult = new HashMap<String, String>();
            pubmedId = pubmedId.trim();
            if (pubmedId == "") {
                pubmedResult.put("pubmedId", "Empty");
                pubmedResult.put("error", "Empty pubmed id - The pubmedId is mandatory");
                listPublications.add(pubmedResult);
            }

            // Check if there is an existing study with the same pubmed id
            Collection<Study> existingStudies = publicationService.findStudiesByPubmedId(pubmedId);
            if (existingStudies != null) {
                pubmedResult.put("pubmedId", pubmedId);
                pubmedResult.put("error", "This pubmed already exists.");
                listPublications.add(pubmedResult);
            } else {
                //Study importedStudy = defaultPubMedSearchService.findPublicationSummary(pubmedId);
                try {
                    getLog().debug("Publication ");
                    getLog().debug(pubmedId);
                    Publication publication = importSinglePublication(pubmedId, true);
                    Study importedStudy = new Study();
                    importedStudy.setPublicationId(publication);
                    Study savedStudy = studyOperationsService.createStudy(importedStudy,currentUser);

                    pubmedResult.put("pubmedId", pubmedId);
                    pubmedResult.put("author", savedStudy.getPublicationId().getFirstAuthor().getFullname());
                    pubmedResult.put("title", savedStudy.getPublicationId().getTitle());
                    pubmedResult.put("study_id", "studies/" + savedStudy.getId().toString());

                    listPublications.add(pubmedResult);

                    // Create directory to store associated files
                    studyFileService.createStudyDir(savedStudy.getId());
                } catch (NoStudyDirectoryException e) {
                    getLog().error("No study directory exception");
                    pubmedResult.put("pubmedId", pubmedId);
                    pubmedResult.put("error", "No study directory exception");
                    listPublications.add(pubmedResult);
                } catch (PubmedLookupException ple) {
                    getLog().error("Something went wrong quering EuropePMC.");
                    pubmedResult.put("pubmedId", pubmedId);
                    pubmedResult.put("error", ple.getMessage());
                    listPublications.add(pubmedResult);
                } catch (Exception e) {
                    getLog().error("Something went wrong. Please, contact the helpdesk.");
                    pubmedResult.put("pubmedId", pubmedId);
                    pubmedResult.put("error", "Something went wrong. Please, contact the helpdesk.");
                    listPublications.add(pubmedResult);
                }

            }
        }
        return listPublications;
    }


    public Boolean changeFirstAuthorByStudyId(Long studyId, Long authorId) {
        Boolean success = false;
        Publication publication = publicationService.findByStudyId(studyId);
        if (publication != null) {
            Author author = authorService.findById(authorId);
            if (author != null) {
                publicationService.updatePublicationFirstAuthor(publication, author);
                success = true;
            }
        }
        return success;
    }

    // Delete the publication if there are no studies linked.
    // Other issue you can import it again or duplicate it.
    public void deletePublicationWithNoStudies(Publication publication) {
        if (publication.getStudies().size() == 0) {
            publicationAuthorsService.deleteByPublication(publication);
            Boolean delete = publicationService.deletePublication(publication);
        }
    }
}
