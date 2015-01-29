package db.migration;

import org.flywaydb.core.api.migration.spring.SpringJdbcMigration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Javadocs go here!
 *
 * @author Tony Burdett
 * @date 29/01/15
 */
public class V1_9_9_010__Association_locus_links_for_single_snp extends CommaSeparatedFieldSplitter
        implements SpringJdbcMigration {
    private static final String SELECT_SNPS =
            "SELECT ID, SNP FROM GWASSNP";

    private static final String SELECT_ASSOCIATIONS_AND_SNPS =
            "SELECT DISTINCT ID, STRONGESTALLELE, SNP " +
                    "FROM GWASSTUDIESSNP " +
                    "WHERE SNP NOT LIKE '%,%' " +
                    "AND SNP NOT LIKE '%:%'";

    public void migrate(JdbcTemplate jdbcTemplate) throws Exception {
        // get all genes
        SnpRowHandler snpHandler = new SnpRowHandler();
        jdbcTemplate.query(SELECT_SNPS, snpHandler);
        final Map<Long, String> snpIdToRsIdMap = snpHandler.getIdToRsIdMap();
        final Map<Long, String> snpIdToRiskAlleleMap = new HashMap<>();

        // get all associations and link to gene id
        final Map<Long, Long> associationIdToSnpId = new HashMap<>();
        jdbcTemplate.query(SELECT_ASSOCIATIONS_AND_SNPS, (resultSet, i) -> {
            long associationID = resultSet.getLong(1);

            Set<String> snps = split(resultSet.getString(3).trim());
            String riskAlleleStr = resultSet.getString(2);
            Set<String> riskAlleles;
            if (riskAlleleStr != null) {
                riskAlleles = split(resultSet.getString(2).trim());
            }
            else {
                riskAlleles = new HashSet<>();
            }
            snps.forEach(snp -> {
                for (Long snpID : snpIdToRsIdMap.keySet()) {
                    if (snpIdToRsIdMap.get(snpID).equals(snp)) {
                        if (associationIdToSnpId.containsKey(associationID)) {
                            // check for equality of SNP names
                            String rsExisting = snpIdToRsIdMap.get(associationIdToSnpId.get(associationID));
                            String rsNew = snpIdToRsIdMap.get(snpID);
                            if (!rsExisting.equals(rsNew)) {
                                // can't safely ignore, this isn't simply duplicate entries in SNP table
                                throw new RuntimeException(
                                        "Can't link association '" + associationID + "' to single SNP - " +
                                                "more than one connected rsID (" +
                                                "existing = " + associationIdToSnpId.get(associationID) + ", " +
                                                "new = " + snpID + ")");
                            }
                        }
                        else {
                            if (riskAlleles.size() > 1) {
                                throw new RuntimeException("Single SNP with multiple risk alleles for SNP - " +
                                                                   snpID + " (risk alleles = " + riskAlleles + ")");
                            }
                            else {
                                if (!riskAlleles.isEmpty()) {
                                    associationIdToSnpId.put(associationID, snpID);
                                    snpIdToRiskAlleleMap.put(snpID, riskAlleles.iterator().next());
                                }
                            }
                        }
                    }
                }
            });
            return null;
        });

        SimpleJdbcInsert insertLocus =
                new SimpleJdbcInsert(jdbcTemplate)
                        .withTableName("LOCUS")
                        .usingColumns("HAPLOTYPE_SNP_COUNT", "DESCRIPTION")
                        .usingGeneratedKeyColumns("ID");

        SimpleJdbcInsert insertAssociationLocus =
                new SimpleJdbcInsert(jdbcTemplate)
                        .withTableName("ASSOCIATION_LOCUS")
                        .usingColumns("ASSOCIATION_ID", "LOCUS_ID");

        Map<Long, Long> associationIdToLocusIdMap = new HashMap<>();
        for (Long associationId : associationIdToSnpId.keySet()) {
            // create a single locus and get the locus ID
            Map<String, Object> locusArgs = new HashMap<>();
            locusArgs.put("HAPLOTYPE_SNP_COUNT", 1);
            locusArgs.put("DESCRIPTION", "Single variant");
            Number locusId = insertLocus.executeAndReturnKey(locusArgs);
            associationIdToLocusIdMap.put(associationId, locusId.longValue());

            // now create the ASSOCIATION_LOCUS link
            Map<String, Object> associationLocusArgs = new HashMap<>();
            associationLocusArgs.put("ASSOCIATION_ID", associationId);
            associationLocusArgs.put("LOCUS_ID", locusId);
            insertAssociationLocus.execute(associationLocusArgs);
        }

        SimpleJdbcInsert insertRiskAllele =
                new SimpleJdbcInsert(jdbcTemplate)
                        .withTableName("RISK_ALLELE")
                        .usingColumns("RISK_ALLELE_NAME")
                        .usingGeneratedKeyColumns("ID");

        SimpleJdbcInsert insertRiskAlleleSnp =
                new SimpleJdbcInsert(jdbcTemplate)
                        .withTableName("RISK_ALLELE_SNP")
                        .usingColumns("RISK_ALLELE_ID", "SNP_ID");

        Map<Long, Long> snpIdToRiskAlleleIdMap = new HashMap<>();
        for (Long snpId : snpIdToRiskAlleleMap.keySet()) {
            // create a single risk allele and get the risk allele id
            Map<String, Object> riskAlleleArgs = new HashMap<>();
            riskAlleleArgs.put("RISK_ALLELE_NAME", snpIdToRiskAlleleMap.get(snpId));
            Number riskAlleleId = insertRiskAllele.execute(riskAlleleArgs);
            snpIdToRiskAlleleIdMap.put(snpId, riskAlleleId.longValue());

            // now create the RISK_ALLELE_SNP link
            Map<String, Object> riskAlleleSnpArgs = new HashMap<>();
            riskAlleleSnpArgs.put("RISK_ALLELE_ID", riskAlleleId);
            riskAlleleSnpArgs.put("SNP_ID", snpId);
            insertRiskAlleleSnp.execute(riskAlleleSnpArgs);
        }

        // finally, create the locus -> risk allele link
        SimpleJdbcInsert insertLocusRiskAllele =
                new SimpleJdbcInsert(jdbcTemplate)
                        .withTableName("LOCUS_RISK_ALLELE")
                        .usingColumns("LOCUS_ID", "RISK_ALLELE_ID");
        for (Map.Entry<Long, Long> associationSnpIdPair : associationIdToSnpId.entrySet()) {
            Map<String, Object> locusRiskAlleleArgs = new HashMap<>();
            locusRiskAlleleArgs.put("LOCUS_ID", associationIdToLocusIdMap.get(associationSnpIdPair.getKey()));
            locusRiskAlleleArgs.put("RISK_ALLELE_ID", snpIdToRiskAlleleIdMap.get(associationSnpIdPair.getValue()));
            insertLocusRiskAllele.execute(locusRiskAlleleArgs);
        }
    }

    public class SnpRowHandler implements RowCallbackHandler {
        private Map<Long, String> idToRsIdMap;

        public SnpRowHandler() {
            this.idToRsIdMap = new HashMap<>();
        }

        @Override public void processRow(ResultSet resultSet) throws SQLException {
            idToRsIdMap.put(resultSet.getLong(1), resultSet.getString(2).trim());
        }

        public Map<Long, String> getIdToRsIdMap() {
            return idToRsIdMap;
        }
    }

}
