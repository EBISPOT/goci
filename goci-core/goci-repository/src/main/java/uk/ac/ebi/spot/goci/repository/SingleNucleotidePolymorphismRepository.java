package uk.ac.ebi.spot.goci.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import uk.ac.ebi.spot.goci.model.SingleNucleotidePolymorphism;

import java.util.Collection;
import java.util.List;


/**
 * Created by emma on 21/11/14.
 *
 * @author emma
 *         <p>
 *         Repository accessing Single Nucleotide Polymorphism entity objectls
 */

@RepositoryRestResource
public interface SingleNucleotidePolymorphismRepository extends JpaRepository<SingleNucleotidePolymorphism, Long> {
    SingleNucleotidePolymorphism findByRsId(String rsId);

    SingleNucleotidePolymorphism findByRsIdIgnoreCase(@Param("rsId") String rsId);

    Collection<SingleNucleotidePolymorphism> findByRiskAllelesLociAssociationStudyId(Long studyId);

    Collection<SingleNucleotidePolymorphism> findByRiskAllelesLociAssociationId(Long associationId);

    Collection<SingleNucleotidePolymorphism> findByRiskAllelesLociAssociationStudyDiseaseTraitId(Long traitId);

    List<SingleNucleotidePolymorphism> findByLocationsId(Long locationId);

    List<SingleNucleotidePolymorphism> findByLocationsChromosomePosition(@Param("bpLocation") int chromosomePosition);

    List<SingleNucleotidePolymorphism> findByLocationsChromosomeNameAndLocationsChromosomePositionBetween(@Param("chrom") String chromosomeName, @Param("bpStart") int start, @Param("bpEnd") int end);

    Collection<SingleNucleotidePolymorphism> findByRiskAllelesLociId(Long locusId);
}

