package db.migration;

import org.flywaydb.core.api.migration.spring.SpringJdbcMigration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Javadocs go here!
 *
 * @author Tony Burdett
 * @date 29/01/15
 */
public class V1_9_9_010__Association_locus_links_for_single_snp extends FieldSplitter
        implements SpringJdbcMigration {
    private static final String SELECT_GENES =
            "SELECT ID, GENE FROM GWASGENE";

    private static final String SELECT_SNPS =
            "SELECT ID, SNP FROM GWASSNP";

    private static final String SELECT_ASSOCIATIONS_AND_SNPS =
            "SELECT DISTINCT ID, STRONGESTALLELE, GENE, SNP " +
                    "FROM GWASSTUDIESSNP " +
                    "WHERE SNP NOT LIKE '%,%' " +
                    "AND SNP NOT LIKE '%:%'";

    private final Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    public void migrate(JdbcTemplate jdbcTemplate) throws Exception {
        // get all genes
        IdAndStringRowHandler geneHandler = new IdAndStringRowHandler();
        jdbcTemplate.query(SELECT_GENES, geneHandler);
        final Map<Long, String> geneIdToNameMap = geneHandler.getIdToStringMap();

        // get all snps
        IdAndStringRowHandler snpHandler = new IdAndStringRowHandler();
        jdbcTemplate.query(SELECT_SNPS, snpHandler);
        final Map<Long, String> snpIdToRsIdMap = snpHandler.getIdToStringMap();

        // get all associations and link to gene id
        final Map<Long, Set<Long>> associationIdToGeneIds = new HashMap<>();
        final Map<Long, Long> associationIdToSnpId = new HashMap<>();
        final Map<Long, String> associationIdToRiskAlleleName = new HashMap<>();
        jdbcTemplate.query(SELECT_ASSOCIATIONS_AND_SNPS, (resultSet, i) -> {
            long associationID = resultSet.getLong(1);

            List<String> riskAlleles;
            List<String> geneNames;
            List<String> rsIds;

            String riskAlleleStr = resultSet.getString(2);
            if (riskAlleleStr != null) {
                riskAlleles = split(resultSet.getString(2).trim());
            }
            else {
                riskAlleles = new ArrayList<>();
            }

            String genesStr = resultSet.getString(3);
            if (genesStr != null) {
                geneNames = split(genesStr.trim());
            }
            else {
                geneNames = new ArrayList<>();
            }

            String snpsStr = resultSet.getString(4);
            if (snpsStr != null) {
                rsIds = split(snpsStr.trim());
            }
            else {
                rsIds = new ArrayList<>();
            }

            // in case we need to add new genes
            SimpleJdbcInsert insertGene =
                    new SimpleJdbcInsert(jdbcTemplate)
                            .withTableName("GENE")
                            .usingColumns("GENE_NAME")
                            .usingGeneratedKeyColumns("ID");

            for (String geneName : geneNames) {
                boolean found = false;
                for (long geneID : geneIdToNameMap.keySet()) {
                    if (geneIdToNameMap.get(geneID).equals(geneName)) {
                        if (!associationIdToGeneIds.containsKey(associationID)) {
                            associationIdToGeneIds.put(associationID, new HashSet<>());
                        }
                        if (!associationIdToGeneIds.get(associationID).contains(geneID)) {
                            // add the new associated gene
                            associationIdToGeneIds.get(associationID).add(geneID);
                        }
                        found = true;
                        break; // we break here to handle duplicate entries in the gene table of the database
                    }
                }

                if (!found) {
                    // the GENE with the GENE_NAME in GWASSTUDIESSNP doesn't exist in GWASGENE,
                    // so create a new GENE entry
                    Map<String, Object> geneArgs = new HashMap<>();
                    geneArgs.put("GENE_NAME", geneName);
                    long geneID = insertGene.executeAndReturnKey(geneArgs).longValue();
                    geneIdToNameMap.put(geneID, geneName);
                    if (!associationIdToGeneIds.containsKey(associationID)) {
                        associationIdToGeneIds.put(associationID, new HashSet<>());
                    }
                    if (!associationIdToGeneIds.get(associationID).contains(geneID)) {
                        // add the new associated gene
                        associationIdToGeneIds.get(associationID).add(geneID);
                    }
                }
            }

            // in case we need to add new SNPs
            SimpleJdbcInsert insertSnp =
                    new SimpleJdbcInsert(jdbcTemplate)
                            .withTableName("SINGLE_NUCLEOTIDE_POLYMORPHISM")
                            .usingColumns("RS_ID")
                            .usingGeneratedKeyColumns("ID");

            for (String rsId : rsIds) {
                boolean foundSnp = false;
                for (long snpID : snpIdToRsIdMap.keySet()) {
                    if (snpIdToRsIdMap.get(snpID).equals(rsId)) {
                        if (associationIdToSnpId.containsKey(associationID)) {
                            // check for equality of SNP names
                            String rsExisting =
                                    snpIdToRsIdMap.get(associationIdToSnpId.get(associationID));
                            String rsNew = snpIdToRsIdMap.get(snpID);
                            if (!rsExisting.equals(rsNew)) {
                                // can't safely ignore, this isn't simply duplicate entries in SNP table
                                throw new RuntimeException(
                                        "Can't link association '" + associationID + "' to single SNP - " +
                                                "more than one connected rsID (" +
                                                "existing = " + associationIdToSnpId.get(associationID) +
                                                ", " +
                                                "new = " + snpID + ")");
                            }
                        }
                        else {
                            if (riskAlleles.size() > 1) {
                                throw new RuntimeException(
                                        "Single SNP with multiple risk alleles for SNP - " +
                                                snpID + " (risk alleles = " + riskAlleles + ")");
                            }
                            else {
                                if (!riskAlleles.isEmpty()) {
                                    associationIdToSnpId.put(associationID, snpID);
                                    associationIdToRiskAlleleName.put(associationID,
                                                                      riskAlleles.iterator().next());
                                }
                            }
                        }
                        foundSnp = true;
                        break; // we break here to handle duplicate entries in the snp table of the database
                    }
                }

                if (!foundSnp) {
                    // the SNP with the RS_ID in GWASSTUDIESSNP doesn't exist in GWASSNP,
                    // so create a new SINGLE_NUCLEOTIDE_POLYMORPHISM entry
                    Map<String, Object> snpArgs = new HashMap<>();
                    snpArgs.put("RS_ID", rsId);
                    long snpID = insertSnp.executeAndReturnKey(snpArgs).longValue();
                    snpIdToRsIdMap.put(snpID, rsId);
                    associationIdToSnpId.put(associationID, snpID);
                }
            }
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

        SimpleJdbcInsert insertAuthorReportedGene =
                new SimpleJdbcInsert(jdbcTemplate)
                        .withTableName("AUTHOR_REPORTED_GENE")
                        .usingColumns("LOCUS_ID", "REPORTED_GENE_ID");

        for (Long associationID : associationIdToSnpId.keySet()) {
            // create a single LOCUS and get the locus ID
            Map<String, Object> locusArgs = new HashMap<>();
            locusArgs.put("HAPLOTYPE_SNP_COUNT", null);
            locusArgs.put("DESCRIPTION", "Single variant");
            Number locusID = insertLocus.executeAndReturnKey(locusArgs);

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
            locusRiskAlleleArgs.put("LOCUS_ID", locusID.longValue());
            locusRiskAlleleArgs.put("RISK_ALLELE_ID", riskAlleleID.longValue());
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

            // finally create the AUTHOR_REPORTED_GENE link
            if (associationIdToGeneIds.containsKey(associationID)) {
                for (Long geneID : associationIdToGeneIds.get(associationID)) {
                    try {
                        Map<String, Object> authorReportedGeneArgs = new HashMap<>();
                        authorReportedGeneArgs.put("LOCUS_ID", locusID.longValue());
                        authorReportedGeneArgs.put("REPORTED_GENE_ID", geneID);
                        insertAuthorReportedGene.execute(authorReportedGeneArgs);
                    }
                    catch (DataIntegrityViolationException e) {
                        throw new RuntimeException(
                                "Failed to insert link between locus = " + locusID + " and reported gene  = " + geneID,
                                e);
                    }
                }
            }
        }
    }

    public class IdAndStringRowHandler implements RowCallbackHandler {
        private Map<Long, String> idToStringMap;

        public IdAndStringRowHandler() {
            this.idToStringMap = new HashMap<>();
        }

        @Override public void processRow(ResultSet resultSet) throws SQLException {
            idToStringMap.put(resultSet.getLong(1), resultSet.getString(2).trim());
        }

        public Map<Long, String> getIdToStringMap() {
            return idToStringMap;
        }
    }

}
