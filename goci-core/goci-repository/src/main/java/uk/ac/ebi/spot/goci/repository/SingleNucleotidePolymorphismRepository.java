package uk.ac.ebi.spot.goci.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
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
    @RestResource(exported = false)
    SingleNucleotidePolymorphism findByRsId(@Param("rsId") String rsId);

    @RestResource(path = "findByRsId", rel = "findByRsId")
    SingleNucleotidePolymorphism findByRsIdIgnoreCase(@Param("rsId") String rsId);

    @RestResource(exported = false)
    Collection<SingleNucleotidePolymorphism> findByRiskAllelesLociAssociationStudyId(Long studyId);

    @RestResource(path = "findByPubmedId", rel = "findByPubmedId")
    Page<SingleNucleotidePolymorphism> findByRiskAllelesLociAssociationStudyPublicationIdPubmedId(String pubmedId, Pageable pageable);


    @RestResource(exported = false)
    Collection<SingleNucleotidePolymorphism> findByRiskAllelesLociAssociationId(Long associationId);

    @RestResource(exported = false)
    Collection<SingleNucleotidePolymorphism> findByRiskAllelesLociAssociationStudyDiseaseTraitId(Long traitId);

    @RestResource(path = "findByDiseaseTrait", rel = "findByDiseaseTrait")
    Collection<SingleNucleotidePolymorphism> findByRiskAllelesLociAssociationStudyDiseaseTraitTrait(String diseaseTrait);

    @RestResource(path = "findByEfoTrait", rel = "findByEfoTrait")
    Collection<SingleNucleotidePolymorphism> findByRiskAllelesLociAssociationEfoTraitsTrait(String efoTrait);

    @RestResource(exported = false)
    List<SingleNucleotidePolymorphism> findByLocationsId(Long locationId);

    @RestResource(path = "findByBpLocation", rel = "findByBpLocation")
    List<SingleNucleotidePolymorphism> findByLocationsChromosomePosition(@Param("bpLocation") int chromosomePosition);

//    List<SingleNucleotidePolymorphism> findByLocationsChromosomeNameAndLocationsChromosomePositionBetween(@Param("chrom") String chromosomeName, @Param("bpStart") int start, @Param("bpEnd") int end);

    @RestResource(path = "findByChromBpLocationRange", rel = "findByChromBpLocationRange")
    Page<SingleNucleotidePolymorphism> findByLocationsChromosomeNameAndLocationsChromosomePositionBetween(@Param("chrom") String chromosomeName, @Param("bpStart") int start, @Param("bpEnd") int end, Pageable pageable);

    @RestResource(exported = false)
    Collection<SingleNucleotidePolymorphism> findByRiskAllelesLociId(Long locusId);

    @RestResource(path = "findByGene", rel = "findByGene")
//    Page<SingleNucleotidePolymorphism> findByGenesGeneName(String geneName, Pageable pageable);
    Page<SingleNucleotidePolymorphism> findByGenomicContextsGeneGeneName(String geneName, Pageable pageable);

}

