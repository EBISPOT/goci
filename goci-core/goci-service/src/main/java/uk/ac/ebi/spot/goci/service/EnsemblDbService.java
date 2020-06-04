package uk.ac.ebi.spot.goci.service;

import com.mashape.unirest.http.JsonNode;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.model.RestResponseResult;

import javax.persistence.NonUniqueResultException;
import java.sql.*;
import java.util.*;

@Service
public class EnsemblDbService{

    @Value("${ensembl.db_version}")
    private String ensemblVersion;

    public enum FeatureType {BAND("band"), GENE("gene");
        public final String label;
        private FeatureType(String label){
            this.label = label;
        }};

    @Autowired
    private Environment env;

    private Connection getConnection() throws SQLException {

        Properties connectionProps = new Properties();
        connectionProps.put("user", "anonymous");
        Connection conn = DriverManager.getConnection(env.getProperty("ensembl.datasource.url"), connectionProps);
        return conn;
    }

//    public RestResponseResult getVariation(String rsId) {
//        RestResponseResult result = new RestResponseResult();
//        String query = "select variation_feature.seq_region_id, seq_region_start, seq_region_end, allele_string, " +
//                "variation_name, consequence_types, variation.minor_allele_freq, seq_region.name, variation" +
//                ".minor_allele  from homo_sapiens_variation_" + ensemblVersion + ".variation_feature\n" +
//                "    join homo_sapiens_core_" + ensemblVersion + ".seq_region on seq_region.seq_region_id = variation_feature" +
//                ".seq_region_id\n" +
//                "    join homo_sapiens_variation_" + ensemblVersion + ".variation on homo_sapiens_variation_" + ensemblVersion + ".variation" +
//                ".variation_id = homo_sapiens_variation_" + ensemblVersion + ".variation_feature.variation_id\n" +
//                "    where homo_sapiens_variation_" + ensemblVersion + ".variation.name = ?;";
//        Map<String, String> data = null;
//        try (Connection c = getConnection()) {
//            PreparedStatement ps = c.prepareStatement(query);
//            ps.setString(1, rsId);
//            data = executeQuery(ps);
//            ps.close();
//            result.setStatus(200);
//            result.setRestResult(buildNode(data));
//        } catch (SQLException e) {
//            e.printStackTrace();
//            result.setStatus(500);
//            result.setError(e.getMessage());
//        }
//        return result;
//    }

    public RestResponseResult getLookupSymbol(String reportedGene) {
        RestResponseResult result = new RestResponseResult();
        String query =
                "select biotype, seq_region_start start, seq_region_end end, seq_region_strand strand, source, gene" +
                        ".description, stable_id id, seq_region.name seq_region_name, display_label display_name, " +
                        "coord_system.version  " +
                        "assembly_name, gene.version  from homo_sapiens_core_" + ensemblVersion +
                        ".gene join homo_sapiens_core_" + ensemblVersion +
                        ".seq_region on  gene.seq_region_id = seq_region.seq_region_id join homo_sapiens_core_" +
                        ensemblVersion + ".coord_system on " + "homo_sapiens_core_" + ensemblVersion +
                        ".coord_system.coord_system_id = homo_sapiens_core_" + ensemblVersion +
                        ".seq_region.coord_system_id join " + "homo_sapiens_core_" + ensemblVersion +
                        ".xref on gene.display_xref_id = xref.xref_id where display_label = ?;";
        Map<String, String> data = null;
        try (Connection c = getConnection()) {
            PreparedStatement ps = c.prepareStatement(query);
            ps.setString(1, reportedGene);
            data = executeQuery(ps);
            ps.close();
            result.setStatus(200);
            result.setRestResult(buildNode(data));
        } catch (SQLException e) {
            e.printStackTrace();
            result.setStatus(500);
            result.setError(e.getMessage());
        }
        return result;
    }

//    public RestResponseResult getOverlapRegion(String chromosome, Integer position1, Integer position2, FeatureType feature) {
//        RestResponseResult result = new RestResponseResult();
//        String query = null;
//        if(feature == FeatureType.BAND){
//            query = "select seq_region.name seq_region_name, seq_region_end end, seq_region_start start, band id, " +
//                    "version assembly_name, stain from homo_sapiens_core_" + ensemblVersion + ".seq_region join homo_sapiens_core_" + ensemblVersion + "" +
//                    ".coord_system on homo_sapiens_core_" + ensemblVersion + ".coord_system.coord_system_id = homo_sapiens_core_" + ensemblVersion + "" +
//                    ".seq_region.coord_system_id join homo_sapiens_core_" + ensemblVersion + ".karyotype on karyotype.seq_region_id = " +
//                    "seq_region.seq_region_id where ? >= seq_region_start and ? <= seq_region_end and " +
//                    "homo_sapiens_core_" + ensemblVersion + ".seq_region.name=?;";
//        }else if(feature == FeatureType.GENE){
//            query =
//                    "select coord_system.version assembly_name, seq_region_start start, seq_region_end end, stable_id" +
//                            " id, gene.description, gene.version, seq_region_strand strand, source, display_label " +
//                            "external_name from homo_sapiens_core_" + ensemblVersion + ".seq_region join homo_sapiens_core_" + ensemblVersion + "" +
//                            ".coord_system on homo_sapiens_core_" + ensemblVersion + ".coord_system.coord_system_id = " +
//                            "homo_sapiens_core_" + ensemblVersion + ".seq_region.coord_system_id join homo_sapiens_core_" + ensemblVersion + ".gene on " +
//                            "gene.seq_region_id = seq_region.seq_region_id join homo_sapiens_core_" + ensemblVersion + ".xref on gene" +
//                            ".display_xref_id = xref.xref_id where ? >= seq_region_start and ? <= seq_region_end and " +
//                            "homo_sapiens_core_" + ensemblVersion + ".seq_region.name=?;";
//        }
//        Map<String, String> data = null;
//        if(query != null) {
//            try (Connection c = getConnection()) {
//                PreparedStatement ps = c.prepareStatement(query);
//                ps.setInt(1, position1);
//                ps.setInt(2, position2);
//                ps.setString(3, chromosome);
//                data = executeQuery(ps);
//                ps.close();
//                result.setStatus(200);
//                result.setRestResult(buildArrayNode(data));
//            } catch (SQLException e) {
//                e.printStackTrace();
//                result.setStatus(500);
//                result.setError(e.getMessage());
//            }
//        }
//        return result;
//    }
//
    private Map<String, String> executeQuery(PreparedStatement preparedStatement) throws SQLException {
        ResultSet rs = preparedStatement.executeQuery();
        ResultSetMetaData rsmd = rs.getMetaData();
        Map<String, String> data = new TreeMap<>();
        while(rs.next()){
            for(int i = 1; i <= rsmd.getColumnCount(); i++){
                String colLabel = rsmd.getColumnLabel(i);
                String colType = rsmd.getColumnTypeName(i);
                if(colLabel == null){
                    colLabel = rsmd.getColumnName(i);
                }
                String oldValue = data.put(colLabel, rs.getString(i));
                if(oldValue != null){
                    throw new NonUniqueResultException();
                }
            }
        }
        return data;
    }

    private JsonNode buildArrayNode(Map<String, String> data){
        JSONArray array = new JSONArray();
        JSONObject dataObj = new JSONObject();
        data.forEach((k, v) -> {
            dataObj.put(k, v);
        });
        array.put(dataObj);
        return new JsonNode(array.toString());
    }
    private JsonNode buildNode(Map<String, String> data){
        JSONObject dataObj = new JSONObject();
        data.forEach((k, v) -> {
            dataObj.put(k, v);
        });
        return new JsonNode(dataObj.toString());
    }

}
