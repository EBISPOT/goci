package db.migration;

/**
 * Created by emma on 28/05/2015.
 *
 * @author emma
 * <p>
 * Script related to JIRA ticket: https://www.ebi.ac.uk/panda/jira/browse/GOCI-791. Aim is to backfil certain
 * association attributes that indicate the type of association. This script handles cases where no locus description s
 * available thus we need to look at number of loci or risk alleles to determine.
 */


import org.flywaydb.core.api.migration.spring.SpringJdbcMigration;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by emma on 28/05/2015.
 *
 * @author emma
 *         <p>
 *         Script related to JIRA ticket: https://www.ebi.ac.uk/panda/jira/browse/GOCI-791. Aim is to backfil certain
 *         association attributes that indicate the type of association. Will backfill based on locus or risk allele
 *         count and will only be used on studies with no locus description. V_1_9_9_048 script used locus descriptions
 *         to set values.
 */
public class V1_9_9_049__Association_type_backfill_associations_with_no_description implements SpringJdbcMigration {

    // This query will find all associations that have a locus and a risk allele
    private static final String SELECT_ASSOCIATIONS_FOR_UPDATE = "SELECT a.id, l.id, r.id\n" +
            "FROM ASSOCIATION a, ASSOCIATION_LOCUS al,\n" +
            "LOCUS l, LOCUS_RISK_ALLELE lra, RISK_ALLELE r\n" +
            "WHERE al.ASSOCIATION_ID = a.id AND\n" +
            "al.LOCUS_ID=l.id AND\n" +
            "lra.LOCUS_ID = l.id AND\n" +
            "lra.RISK_ALLELE_ID=r.id AND (a.SNP_INTERACTION IS NULL OR\n" +
            "a.MULTI_SNP_HAPLOTYPE IS NULL) \n" +
            "ORDER BY a.id asc";

    private static final String UPDATE_ASSOCIATION =
            "UPDATE ASSOCIATION SET SNP_INTERACTION = ?, MULTI_SNP_HAPLOTYPE = ? WHERE ID = ?";

    @Override public void migrate(JdbcTemplate jdbcTemplate) throws Exception {

        final Map<Long, Set<Long>> associationIdsToLociIds = new HashMap<>();
        final Map<Long, Set<Long>> lociIdsToRiskAlleleIds = new HashMap<>();

        // Get association ids
        jdbcTemplate.query(SELECT_ASSOCIATIONS_FOR_UPDATE, (resultSet, i) -> {
            Long associationId = resultSet.getLong(1);
            Long locusId = resultSet.getLong(2);
            Long riskAlleleId = resultSet.getLong(3);

            // Create map of associations to loci
            if (associationIdsToLociIds.containsKey(associationId)) {
                associationIdsToLociIds.get(associationId).add(locusId);
            }

            else {
                Set<Long> lociIds = new HashSet<>();
                lociIds.add(locusId);
                associationIdsToLociIds.put(associationId, lociIds);
            }

            // Create map of loci to risk allele
            if (lociIdsToRiskAlleleIds.containsKey(locusId)) {
                lociIdsToRiskAlleleIds.get(locusId).add(riskAlleleId);
            }

            else {
                Set<Long> riskAlleleIds = new HashSet<>();
                riskAlleleIds.add(riskAlleleId);
                lociIdsToRiskAlleleIds.put(locusId, riskAlleleIds);
            }

            return null;
        });


        for (Long associationId : associationIdsToLociIds.keySet()) {
            Set<Long> lociIds = associationIdsToLociIds.get(associationId);

            // Determine number of loci linked to as associations
            if (lociIds.size() > 1) {
                // This is a snp interaction association
                jdbcTemplate.update(UPDATE_ASSOCIATION, 1, 0, associationId);
            }

            // A single locus indicates a multi-snp haplotype or standard snp
            else if (lociIds.size() == 1) {
                Long locusId = lociIds.iterator().next();
                Set<Long> riskAlleleIds = lociIdsToRiskAlleleIds.get(locusId);

                // Multi-snp haplotype
                if (riskAlleleIds.size() > 1) {
                    jdbcTemplate.update(UPDATE_ASSOCIATION, 0, 1, associationId);
                }
                // Standard case or single variant
                else { jdbcTemplate.update(UPDATE_ASSOCIATION, 0, 0, associationId);}

            }

            // Rare case where we have no loci
            else {
                jdbcTemplate.update(UPDATE_ASSOCIATION, 0, 0, associationId);
            }

        }
    }
}
