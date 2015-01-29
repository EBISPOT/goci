//package db.migration;
//
//import org.flywaydb.core.api.migration.spring.SpringJdbcMigration;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.jdbc.core.RowCallbackHandler;
//import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
//
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.Map;
//import java.util.Set;
//
///**
// * Javadocs go here!
// *
// * @author Tony Burdett
// * @date 29/01/15
// */
//public class Locus_reported_gene_links extends CommaSeparatedFieldSplitter implements SpringJdbcMigration {
//    private static final String SELECT_GENES =
//            "SELECT ID, GENE FROM GWASGENE";
//
//    private static final String SELECT_ASSOCIATIONS_AND_COMMA_SEPARATED_GENES =
//            "SELECT DISTINCT ID, GENE " +
//                    "FROM GWASSTUDIESSNP " +
//                    "WHERE SNP NOT LIKE '%:%'";
//
//    public void migrate(JdbcTemplate jdbcTemplate) throws Exception {
//        // get all genes
//        GeneRowHandler geneHandler = new GeneRowHandler();
//        jdbcTemplate.query(SELECT_GENES, geneHandler);
//        final Map<Long, String> geneIdToGeneName = geneHandler.getIdToGeneNameMap();
//
//        // get all associations and link to gene id
//        final Map<Long, Set<Long>> associationIdToGeneId = new HashMap<>();
//        jdbcTemplate.query(SELECT_ASSOCIATIONS_AND_COMMA_SEPARATED_GENES, (resultSet, i) -> {
//            long associationID = resultSet.getLong(1);
//            Set<String> genes = split(resultSet.getString(2).trim());
//            genes.forEach(gene -> {
//                for (Long geneID : geneIdToGeneName.keySet()) {
//                    if (geneIdToGeneName.get(geneID).equals(gene)) {
//                        if (!associationIdToGeneId.containsKey(associationID)) {
//                            associationIdToGeneId.put(associationID, new HashSet<>());
//                        }
//                        associationIdToGeneId.get(associationID).add(geneID);
//                        break;
//                    }
//                }
//            });
//            return null;
//        });
//
//        SimpleJdbcInsert insertLocus =
//                new SimpleJdbcInsert(jdbcTemplate)
//                        .withTableName("LOCUS")
//                        .usingColumns("HAPLOTYPE_SNP_COUNT", "DESCRIPTION")
//                        .usingGeneratedKeyColumns("ID");
//
//        for (Long associationId : associationIdToGeneId.keySet()) {
//            // create a single locus and get the locus ID
//            insertLocus.execute(null, null);
//        }
//
//        // now print
//        System.out.println(associationIdToGeneId);
//
//
//    }
//
//    public class GeneRowHandler implements RowCallbackHandler {
//        private Map<Long, String> idToGeneNameMap;
//
//        public GeneRowHandler() {
//            this.idToGeneNameMap = new HashMap<>();
//        }
//
//        @Override public void processRow(ResultSet resultSet) throws SQLException {
//            idToGeneNameMap.put(resultSet.getLong(1), resultSet.getString(2).trim());
//        }
//
//        public Map<Long, String> getIdToGeneNameMap() {
//            return idToGeneNameMap;
//        }
//    }
//}
