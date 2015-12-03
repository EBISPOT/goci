package uk.ac.ebi.spot.goci.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.spot.goci.model.EnsemblGene;
import uk.ac.ebi.spot.goci.model.Gene;
import uk.ac.ebi.spot.goci.repository.EnsemblGeneRepository;

/**
 * Created by emma on 14/08/2015.
 *
 * @author emma
 *         <p>
 *         Based on classes in  goci-core/goci-service/src/main/java/uk/ac/ebi/spot/goci/service/
 *         <p>
 *         A facade service around a {@link uk.ac.ebi.spot.goci.repository.EnsemblGeneRepository} that retrieves all
 *         associations, and then within the same datasource transaction additionally loads other objects referenced by
 *         this association like Loci.
 *         <p>
 *         Use this when you know you will need deep information about a association and do not have an open session
 *         that can be used to lazy load extra data.
 */
@Service
public class EnsemblGeneQueryService {

    private EnsemblGeneRepository ensemblGeneRepository;

    @Autowired
    public EnsemblGeneQueryService(EnsemblGeneRepository ensemblGeneRepository) {
        this.ensemblGeneRepository = ensemblGeneRepository;
    }

    @Transactional(readOnly = true)
    public EnsemblGene findByEnsemblGeneId(String id) {
        EnsemblGene ensemblGene = ensemblGeneRepository.findByEnsemblGeneId(id);

        if (ensemblGene != null) {
            loadAssociatedData(ensemblGene);
        }
        return ensemblGene;
    }

    public void loadAssociatedData(EnsemblGene ensemblGene) {
        ensemblGene.getEnsemblGeneId();
        ensemblGene.getId();
        if (ensemblGene.getGene() != null) {
            ensemblGene.getGene();
            loadAssociatedGeneData(ensemblGene.getGene());
        }
    }

    private void loadAssociatedGeneData(Gene gene) {
        if (gene.getEnsemblGeneIds() != null) {
            gene.getEnsemblGeneIds().size();
        }
    }
}
