package uk.ac.ebi.spot.goci.ui.repository;

import org.springframework.stereotype.Repository;
import uk.ac.ebi.spot.goci.ui.model.SingleNucleotidePolymorphism;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Created by emma on 05/12/14.
 */
@Repository
public class SingleNucleotidePolymorphismRepositoryImpl implements SingleNucleotidePolymorphismRepositoryCustom {


    @PersistenceContext
    private EntityManager entityManager;


    @Override
    public SingleNucleotidePolymorphism retrieveAllSNPDetails(String rsID) {


        return entityManager.createQuery("SELECT snp FROM SingleNucleotidePolymorphism snp WHERE snp.rsID = :rsID",
                SingleNucleotidePolymorphism.class)
                .setParameter("rsID", rsID)
                .getSingleResult();

 /*       Query query = entityManager.createQuery("select  sn \n" +
                "from Region  r,\n" +
                "RegionXref x ,\n" +
                "Association s,\n" +
                "GeneXref gx,\n" +
                "Gene g,\n" +
                "SingleNucleotidePolymorphism sn,\n" +
                "SingleNucleotidePolymorphismXref sx\n" +
                "where r.id = x.regionID\n" +
                "and g.id = gx.geneID\n" +
                "and sn.id = sx.snpID\n" +
                "and s.id= x.associationID\n" +
                "and s.id = gx.associationID\n" +
                "and s.id = sx.associationID\n" +
                "and sn.rsID =?1");
        query.setParameter(1, rsID);

        return query.getSingleResult();
*/


    }

}
