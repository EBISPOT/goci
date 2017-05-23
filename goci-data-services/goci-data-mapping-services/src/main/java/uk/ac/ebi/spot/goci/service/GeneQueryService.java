package uk.ac.ebi.spot.goci.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.spot.goci.model.Gene;
import uk.ac.ebi.spot.goci.repository.GeneRepository;

/**
 * Created by emma on 14/08/2015.
 *
 * @author emma
 *         <p>
 *         Based on classes in  goci-core/goci-service/src/main/java/uk/ac/ebi/spot/goci/service/
 *         <p>
 *         A facade service around a {@link uk.ac.ebi.spot.goci.repository.GeneRepository} that retrieves all
 *         associations, and then within the same datasource transaction additionally loads other objects referenced by
 *         this association like Loci.
 *         <p>
 *         Use this when you know you will need deep information about a association and do not have an open session
 *         that can be used to lazy load extra data.
 */
@Service
public class GeneQueryService {

    private GeneRepository geneRepository;

    @Autowired
    public GeneQueryService(GeneRepository geneRepository) {
        this.geneRepository = geneRepository;
    }

    @Transactional(readOnly = true)
    public Gene findByGeneName(String geneName) {
        Gene gene = geneRepository.findByGeneName(geneName);
        if (gene != null) {
            loadAssociatedData(gene);
        }
        return gene;
    }

    public void loadAssociatedData(Gene gene) {

        if (gene.getEntrezGeneIds() != null) {
            gene.getEntrezGeneIds().size();
        }

        if (gene.getEnsemblGeneIds() != null) {
            gene.getEnsemblGeneIds().size();
        }
    }
}
