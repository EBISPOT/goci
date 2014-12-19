package uk.ac.ebi.spot.goci.ui.repository;

//import uk.ac.ebi.spot.goci.model.SingleNucleotidePolymorphism;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import uk.ac.ebi.spot.goci.ui.model.Snp;


/**
 * Created by Dani on 27/11/2014.
 */
@RepositoryRestResource
public interface SnpRepository  extends JpaRepository<Snp, Long> {
    Snp findByRsId(String rsId);

//    List<Snp> findAll(Pageable pageable);
}
