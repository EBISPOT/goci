package db.migration;

import org.flywaydb.core.api.migration.spring.SpringJdbcMigration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by dwelter on 29/04/15.
 */
public class V1_9_9_039__Proxy_snp_cleanup implements SpringJdbcMigration {

    private static final String SELECT_DATA_FOR_UPDATE =
            "SELECT distinct lra.locus_id, ras.risk_allele_id, ras.snp_id, snp.RS_ID, l.MIGRATED_DESCRIPTION \n" +
                    "FROM LOCUS l \n" +
                    "JOIN LOCUS_RISK_ALLELE lra ON lra.LOCUS_ID = l.ID\n" +
                    "JOIN RISK_ALLELE_SNP ras ON lra.RISK_ALLELE_ID = ras.RISK_ALLELE_ID\n" +
                    "JOIN SINGLE_NUCLEOTIDE_POLYMORPHISM snp ON snp.ID = ras.SNP_ID\n" +
                    "WHERE l.HAPLOTYPE_SNP_COUNT IS NOT NULL \n" +
                    "AND l.DESCRIPTION != l.MIGRATED_DESCRIPTION\n" +
                    "AND (MIGRATED_DESCRIPTION LIKE 'rs%'\n" +
                    "OR MIGRATED_DESCRIPTION LIKE '%rs%')\n" +
                    "AND MIGRATED_DESCRIPTION NOT LIKE '%+ rs%'";

    private static final String DELETE_FROM_LOCUS_RISK_ALLELE =
            "DELETE FROM LOCUS_RISK_ALLELE WHERE RISK_ALLELE_ID = ?";

    private static final String DELETE_FROM_RISK_ALLELE =
            "DELETE FROM RISK_ALLELE WHERE ID = ?";

    private static final String DELETE_FROM_RISK_ALLELE_SNP =
            "DELETE FROM RISK_ALLELE_SNP WHERE RISK_ALLELE_ID = ?";

    private static final String UPDATE_PROXY_SNP_MAIN_LOCUS =
            "UPDATE LOCUS SET HAPLOTYPE_SNP_COUNT = ''," +
                    "DESCRIPTION = ''  " +
                    "WHERE ID = ?";


    public void migrate(JdbcTemplate jdbcTemplate) throws Exception {

        final Map<String, List<ProxySNP>> descriptionToProxyElements = new HashMap<>();
        final List<Long> riskAlleleIdsForRemoval = new ArrayList<>();
        final Map<Long, List<Long>> riskAlleleIdToProxySNPids = new HashMap<>();

        jdbcTemplate.query(SELECT_DATA_FOR_UPDATE, (resultSet, i) -> {
            long locus_id = resultSet.getLong(1);
            long risk_allele_id = resultSet.getLong(2);
            long snp_id = resultSet.getLong(3);
            String rs_id = resultSet.getString(4).trim();
            String migrated_description = resultSet.getString(5).trim();

            //create a map that links a migrated description to all the entries with that description
            if (!descriptionToProxyElements.containsKey(migrated_description)) {
                descriptionToProxyElements.put(migrated_description, new ArrayList<>());
            }
            descriptionToProxyElements.get(migrated_description)
                    .add(new ProxySNP(locus_id, risk_allele_id, snp_id, rs_id));

            return null;
        });

        Set<String> migratedDescriptions = descriptionToProxyElements.keySet();

        /*find the primary SNP (rsID same as rsID in description) and make it the risk allele, and make the
        other SNPs for this association proxy SNPs*/
        for (String description : migratedDescriptions) {
            List<ProxySNP> snps = descriptionToProxyElements.get(description);
            List<Long> snpIds = new ArrayList<>();
            long risk_allele_id = 0;

            for (ProxySNP snp : snps) {
                if (description.contains(snp.getRs_id())) {
                    risk_allele_id = snp.getRisk_allele_id();
                }
                else {
                    snpIds.add(snp.getSnp_id());
                    riskAlleleIdsForRemoval.add(snp.getRisk_allele_id());
                }
            }

            if (risk_allele_id != 0) {
                riskAlleleIdToProxySNPids.put(risk_allele_id, snpIds);
            }
        }

        //delete proxy SNP entries from RISK_ALLELE, LOCUS_RISK_ALLELE and RISK_ALLELE_SNP using RISK_ALLELE_ID
        for (long risk_allele_id : riskAlleleIdsForRemoval) {
            jdbcTemplate.update(DELETE_FROM_RISK_ALLELE_SNP, risk_allele_id);
            jdbcTemplate.update(DELETE_FROM_LOCUS_RISK_ALLELE, risk_allele_id);
            jdbcTemplate.update(DELETE_FROM_RISK_ALLELE, risk_allele_id);
        }


        //insert new risk allele/SNP tuples into the RISK_ALLELE_PROXY_SNP
        SimpleJdbcInsert insertRiskAlleleProxySNP =
                new SimpleJdbcInsert(jdbcTemplate)
                        .withTableName("RISK_ALLELE_PROXY_SNP")
                        .usingColumns("RISK_ALLELE_ID", "SNP_ID");

        Set<Long> riskAlleles = riskAlleleIdToProxySNPids.keySet();

        for (long ra : riskAlleles) {
            List<Long> snps = riskAlleleIdToProxySNPids.get(ra);
            for (long snp : snps) {
                Map<String, Long> riskAlleleProxySNPargs = new HashMap<>();
                riskAlleleProxySNPargs.put("RISK_ALLELE_ID", ra);
                riskAlleleProxySNPargs.put("SNP_ID", snp);
                insertRiskAlleleProxySNP.execute(riskAlleleProxySNPargs);
            }

            //remove the haplotype count and new description for the primary risk allele in LOCUS
            jdbcTemplate.update(UPDATE_PROXY_SNP_MAIN_LOCUS, ra);
        }

    }

    private class ProxySNP {

        private Long locus_id, risk_allele_id, snp_id;
        private String rs_id;

        private ProxySNP(Long locus_id, Long risk_allele_id, Long snp_id, String rs_id) {
            this.locus_id = locus_id;
            this.risk_allele_id = risk_allele_id;
            this.snp_id = snp_id;
            this.rs_id = rs_id;
        }

        public Long getLocus_id() {
            return locus_id;
        }

        public Long getRisk_allele_id() {
            return risk_allele_id;
        }

        public Long getSnp_id() {
            return snp_id;
        }

        public String getRs_id() {
            return rs_id;
        }
    }
}
