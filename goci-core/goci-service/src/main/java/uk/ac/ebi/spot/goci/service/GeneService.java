package uk.ac.ebi.spot.goci.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.spot.goci.model.Gene;
import uk.ac.ebi.spot.goci.repository.GeneRepository;

import java.util.Collection;

/**
 * Created by dwelter on 12/02/15.
 */
public class GeneService {

    private GeneRepository geneRepository;

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    public GeneService(GeneRepository geneRepository) {
        this.geneRepository = geneRepository;
    }

    protected Logger getLog() {
        return log;
    }

    @Transactional(readOnly = true)
    public Collection<Gene> deepFindByStudyId(Long studyId) {
        Collection<Gene> genes = geneRepository.findByLociAssociationStudyId(studyId);
        genes.forEach(this::loadAssociatedData);
        return genes;
    }

    public void loadAssociatedData(Gene gene) {
//        int efoTraitCount = gene.getEfoTraits().size();
//        int associationCount = gene.getAssociations().size();
//        Date publishDate = gene.getHousekeeping().getPublishDate();
//        if (publishDate != null) {
//            getLog().info(
//                    "Study '" + gene.getId() + "' is mapped to " + efoTraitCount + " traits, " +
//                            "has " + associationCount + " associations and was published on " + publishDate.toString());
//        }
//        else {
//            getLog().info(
//                    "Study '" + gene.getId() + "' is mapped to " + efoTraitCount + " traits, " +
//                            "has " + associationCount + " associations and is not yet published");
//        }
    }
}
