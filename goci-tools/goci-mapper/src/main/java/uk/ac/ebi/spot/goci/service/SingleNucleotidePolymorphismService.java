package uk.ac.ebi.spot.goci.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.spot.goci.model.SingleNucleotidePolymorphism;
import uk.ac.ebi.spot.goci.repository.SingleNucleotidePolymorphismRepository;

import java.util.List;

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
public class SingleNucleotidePolymorphismService {

    // Repositories
    private SingleNucleotidePolymorphismRepository singleNucleotidePolymorphismRepository;

    @Autowired
    public SingleNucleotidePolymorphismService(SingleNucleotidePolymorphismRepository singleNucleotidePolymorphismRepository) {
        this.singleNucleotidePolymorphismRepository = singleNucleotidePolymorphismRepository;
    }

    @Transactional(readOnly = true)
    public List<SingleNucleotidePolymorphism> findByRsIdIgnoreCase(String rsId) {
        List<SingleNucleotidePolymorphism> singleNucleotidePolymorphisms =
                singleNucleotidePolymorphismRepository.findByRsIdIgnoreCase(rsId);
        singleNucleotidePolymorphisms.forEach(this::loadAssociatedData);
        return singleNucleotidePolymorphisms;
    }

    public void loadAssociatedData(SingleNucleotidePolymorphism snp) {
        snp.getLocations().size();
        snp.getGenomicContexts().size();
    }
}
