package uk.ac.ebi.spot.goci.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.spot.goci.model.SingleNucleotidePolymorphism;
import uk.ac.ebi.spot.goci.repository.SingleNucleotidePolymorphismRepository;

import java.util.Collection;

/**
 * Created by emma on 14/08/2015.
 *
 * @author emma
 *         <p>
 *         Based on classes in  goci-core/goci-service/src/main/java/uk/ac/ebi/spot/goci/service/
 *         <p>
 *         A facade service around a {@link uk.ac.ebi.spot.goci.repository.SingleNucleotidePolymorphismRepository} that
 *         retrieves all associations, and then within the same datasource transaction additionally loads other objects
 *         referenced by this association like Loci.
 *         <p>
 *         Use this when you know you will need deep information about a association and do not have an open session
 *         that can be used to lazy load extra data.
 */
@Service
public class SingleNucleotidePolymorphismQueryService {

    // Repositories
    private SingleNucleotidePolymorphismRepository singleNucleotidePolymorphismRepository;

    @Autowired
    public SingleNucleotidePolymorphismQueryService(SingleNucleotidePolymorphismRepository singleNucleotidePolymorphismRepository) {
        this.singleNucleotidePolymorphismRepository = singleNucleotidePolymorphismRepository;
    }

    @Transactional(readOnly = true)
    public SingleNucleotidePolymorphism findByRsIdIgnoreCase(String rsId) {
        SingleNucleotidePolymorphism singleNucleotidePolymorphism =
                singleNucleotidePolymorphismRepository.findByRsIdIgnoreCase(rsId);
        loadAssociatedData(singleNucleotidePolymorphism);
        return singleNucleotidePolymorphism;
    }

    @Transactional(readOnly = true)
    public Collection<SingleNucleotidePolymorphism> findByRiskAllelesLociId(Long locusId) {
        Collection<SingleNucleotidePolymorphism> snpsLinkedToLocus =
                singleNucleotidePolymorphismRepository.findByRiskAllelesLociId(locusId);
        snpsLinkedToLocus.forEach(this::loadAssociatedData);
        return snpsLinkedToLocus;
    }

    public void loadAssociatedData(SingleNucleotidePolymorphism snp) {

        if (snp.getLocations() != null) {
            snp.getLocations().size();
        }
        if (snp.getGenomicContexts() != null) {
            snp.getGenomicContexts().size();
        }
    }
}
