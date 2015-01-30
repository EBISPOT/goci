package db.migration;

import org.flywaydb.core.api.migration.spring.SpringJdbcMigration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
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

    private final Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    public void migrate(JdbcTemplate jdbcTemplate) throws Exception {
        // get all genes
        SnpRowHandler snpHandler = new SnpRowHandler();
        jdbcTemplate.query(SELECT_SNPS, snpHandler);
        final Map<Long, String> snpIdToRsIdMap = snpHandler.getIdToRsIdMap();

        // get all associations and link to gene id
        final Map<Long, Long> associationIdToSnpId = new HashMap<>();
        final Map<Long, String> associationIdToRiskAlleleName = new HashMap<>();
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
                                    associationIdToRiskAlleleName.put(associationID, riskAlleles.iterator().next());
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

        SimpleJdbcInsert insertRiskAllele =
                new SimpleJdbcInsert(jdbcTemplate)
                        .withTableName("RISK_ALLELE")
                        .usingColumns("RISK_ALLELE_NAME")
                        .usingGeneratedKeyColumns("ID");

        SimpleJdbcInsert insertLocusRiskAllele =
                new SimpleJdbcInsert(jdbcTemplate)
                        .withTableName("LOCUS_RISK_ALLELE")
                        .usingColumns("LOCUS_ID", "RISK_ALLELE_ID");

        SimpleJdbcInsert insertRiskAlleleSnp =
                new SimpleJdbcInsert(jdbcTemplate)
                        .withTableName("RISK_ALLELE_SNP")
                        .usingColumns("RISK_ALLELE_ID", "SNP_ID");

        Map<Long, Long> associationIdToLocusIdMap = new HashMap<>();
        for (Long associationID : associationIdToSnpId.keySet()) {
            // create a single LOCUS and get the locus ID
            Map<String, Object> locusArgs = new HashMap<>();
            locusArgs.put("HAPLOTYPE_SNP_COUNT", 1);
            locusArgs.put("DESCRIPTION", "Single variant");
            Number locusID = insertLocus.executeAndReturnKey(locusArgs);
            associationIdToLocusIdMap.put(associationID, locusID.longValue());

            // now create the ASSOCIATION_LOCUS link
            Map<String, Object> associationLocusArgs = new HashMap<>();
            associationLocusArgs.put("ASSOCIATION_ID", associationID);
            associationLocusArgs.put("LOCUS_ID", locusID);
            insertAssociationLocus.execute(associationLocusArgs);

            // now create a single RISK_ALLELE and get the risk allele ID
            Map<String, Object> riskAlleleArgs = new HashMap<>();
            riskAlleleArgs.put("RISK_ALLELE_NAME", associationIdToRiskAlleleName.get(associationID));
            Number riskAlleleID = insertRiskAllele.executeAndReturnKey(riskAlleleArgs);

            // now create the LOCUS_RISK_ALLELE link
            Map<String, Object> locusRiskAlleleArgs = new HashMap<>();
            locusRiskAlleleArgs.put("LOCUS_ID", associationIdToLocusIdMap.get(locusID.longValue()));
            locusRiskAlleleArgs.put("RISK_ALLELE_ID", associationIdToRiskAlleleName.get(riskAlleleID.longValue()));
            insertLocusRiskAllele.execute(locusRiskAlleleArgs);

            // now create the RISK_ALLELE_SNP link
            Long snpID = associationIdToSnpId.get(associationID);
            try {
                Map<String, Object> riskAlleleSnpArgs = new HashMap<>();
                riskAlleleSnpArgs.put("RISK_ALLELE_ID", riskAlleleID.longValue());
                riskAlleleSnpArgs.put("SNP_ID", snpID);
                insertRiskAlleleSnp.execute(riskAlleleSnpArgs);
            }
            catch (DataIntegrityViolationException e) {
                throw new RuntimeException(
                        "Failed to insert link between snp = " + snpID + " and risk allele = " + riskAlleleID, e);
            }
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
