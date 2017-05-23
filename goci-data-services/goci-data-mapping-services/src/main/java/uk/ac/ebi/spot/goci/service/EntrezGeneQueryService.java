package uk.ac.ebi.spot.goci.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.spot.goci.model.EntrezGene;
import uk.ac.ebi.spot.goci.model.Gene;
import uk.ac.ebi.spot.goci.repository.EntrezGeneRepository;

/**
 * Created by emma on 14/08/2015.
 *
 * @author emma
 *         <p>
 *         Based on classes in  goci-core/goci-service/src/main/java/uk/ac/ebi/spot/goci/service/
 *         <p>
 *         A facade service around a {@link uk.ac.ebi.spot.goci.repository.EntrezGeneRepository} that retrieves all
 *         associations, and then within the same datasource transaction additionally loads other objects referenced by
 *         this association like Loci.
 *         <p>
 *         Use this when you know you will need deep information about a association and do not have an open session
 *         that can be used to lazy load extra data.
 */
@Service
public class EntrezGeneQueryService {

    private EntrezGeneRepository entrezGeneRepository;

    @Autowired
    public EntrezGeneQueryService(EntrezGeneRepository entrezGeneRepository) {
        this.entrezGeneRepository = entrezGeneRepository;
    }

    @Transactional(readOnly = true)
    public EntrezGene findByEntrezGeneId(String id) {
        EntrezGene entrezGene = entrezGeneRepository.findByEntrezGeneId(id);
        if (entrezGene != null) {
            loadAssociatedData(entrezGene);
        }
        return entrezGene;
    }

    public void loadAssociatedData(EntrezGene entrezGene) {
        entrezGene.getEntrezGeneId();
        entrezGene.getId();
        if (entrezGene.getGene() != null) {
            entrezGene.getGene();
            loadAssociatedGeneData(entrezGene.getGene());
        }
    }

    private void loadAssociatedGeneData(Gene gene) {
        if (gene.getEntrezGeneIds() != null) {
            gene.getEntrezGeneIds().size();
        }
    }
}
