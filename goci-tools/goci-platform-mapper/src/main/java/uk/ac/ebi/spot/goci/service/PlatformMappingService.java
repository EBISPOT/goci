package uk.ac.ebi.spot.goci.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.model.LocalArrayInfo;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dwelter on 08/03/16.
 */
@Service
public class PlatformMappingService {

    private static final String DBQUERY =
            "SELECT DISTINCT ID, PLATFORM FROM STUDY";


    private List<LocalArrayInfo> platformEntries;
    private List<String> manufacturers;
    private List<String> qualifiers;

    private JdbcTemplate jdbcTemplate;

    private Logger output = LoggerFactory.getLogger("output");

    protected Logger getOutput() {
        return output;
    }

    private Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    @Autowired
    public PlatformMappingService(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
        manufacturers = new ArrayList<>();
        manufacturers.add("Affymetrix");
        manufacturers.add("Illumina");
        manufacturers.add("Perlegen");

        this.qualifiers = new ArrayList<>();
        qualifiers.add("up to");
        qualifiers.add("at least");
        qualifiers.add("~");
        qualifiers.add(">");
        qualifiers.add("more than");

        platformEntries = new ArrayList<>();

    }
    public void mapAllValues() {
        getLog().info("Retrieving all platform entries");
        
        List<PlatformEntity> studies = retrieveData();

        int all = studies.size();
        getLog().info("Retrieved " + all + " studies");

        int counter = 0;

        for(PlatformEntity study : studies){
            if((counter % 100) == 0){
                getLog().info((all - counter) + " studies left to process");
            }

            String platform = study.getP();

            if(platform != null){
                System.out.println(platform);
                List<String> manufacturer = new ArrayList<>();
                boolean imputed = false;
                boolean pooled = false;
                Integer snpCount = null;
                List<String> qualifier = new ArrayList<>();
                String qual = null;
                String comment = null;

                if(platform.equals("NR")){
                    comment = platform;
                }
                else {
                    for(String man : manufacturers){
                        if(platform.contains(man)){
                            manufacturer.add(man);
                        }
                    }

                    if(manufacturer.size() == 0){
                        comment = platform;
                    }

                    if(platform.contains("imputed")){
                        imputed = true;
                    }
                    if(platform.contains("pooled")){
                        pooled = true;
                    }

                    if(platform.contains("SNP") || platform.contains("unsure") || platform.contains("UNSURE")){
                        comment = platform;
                    }
                    else{
                        if(platform.contains("[") && platform.indexOf("[") == platform.lastIndexOf("[")) {
                            int start = platform.indexOf("[") +1;
                            int finish = platform.indexOf("]");

                            String count = platform.substring(start, finish).trim();

                            if(!count.equals("NR")) {
                                for (String q : qualifiers) {
                                    if (count.contains(q)) {
                                        qualifier.add(q);

                                        count = count.replace(q, "").trim();
                                    }
                                }

                                if (count.contains("million")) {
                                    //                                System.out.print(count);
                                    count = count.replace("million", "").trim();

                                    if (count.contains(",")) {
                                        count = count.replace(",", "").trim();
                                    }

                                    //                                System.out.println("\t" + count);
                                    double c = Double.parseDouble(count);

                                    snpCount = (int) (c * 1000000);

                                }

                                else if (count.contains(",")) {
                                    //                                try {
                                    count = count.replace(",", "").trim();
                                    snpCount = Integer.parseInt(count);


                                    //                                    snpCount = NumberFormat.getNumberInstance(java.util.Locale.US).parse(count).intValue();
                                    //                                }
                                    //                                catch (ParseException e) {
                                    //                                    e.printStackTrace();
                                    //                                }

                                }
                                else {
                                    snpCount = Integer.parseInt(count);
                                }
                            }
                        }
                        else {
                            comment = platform;
                        }

                    }



                }

                if(qualifier.size() > 1){
                    comment = platform;
                }
                else if(qualifier.size() == 1){
                    qual = qualifier.get(0);
                }

                LocalArrayInfo info = new LocalArrayInfo(manufacturer, snpCount, qual, imputed, pooled, comment, study.getStudy(), platform);

                platformEntries.add(info);

            }
            counter++;
        }

        printArrays(platformEntries);
    }

    private void printArrays(List<LocalArrayInfo> localArrayInfo) {

        getOutput().info("Platform\tStudyID\tManufacturers\tQualifier\tSNPcount\tImputer\tPooled\tComment");

        for(LocalArrayInfo info : localArrayInfo){
            String platform = info.getPlatformString();

            String id = String.valueOf(info.getStudy());
            String manufacturer = "";

            if(info.getManufacturer().size() != 0){
                 for(String m : info.getManufacturer()){
                     if(manufacturer.equals("")){
                          manufacturer = m;
                     }
                     else {
                         manufacturer = manufacturer.concat(" & ").concat(m);
                     }
                 }
            }

            String q = "";

            if(info.getQualifier() != null){
                q = info.getQualifier();
            }

            String count = "NR";
            if(info.getSnps() != null){
                 count = info.getSnps().toString();
            }

            String comment = "";
            if(info.getComment() != null){
                comment = info.getComment();
            }

            String imputed = "";

            if(info.isImputed()) {
                imputed = "imputed";
            }

            String pooled = "";

            if(info.isPooled()){
                pooled = "pooled";
            }

            getOutput().info(platform + "\t" + id + "\t" + manufacturer  + "\t" + q  + "\t" + count  + "\t" + imputed  + "\t" + pooled  + "\t" + comment);
        }
    }

    public List<PlatformEntity> retrieveData(){
        return getJdbcTemplate().query(DBQUERY, new PlatformMapper());
    }

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    private class PlatformMapper implements RowMapper<PlatformEntity> {
        public PlatformEntity mapRow(ResultSet rs, int i) throws SQLException {
            int id = rs.getInt(1);
            String p = rs.getString(2).trim();

            return new PlatformEntity(id, p);
        }
    }

    private class PlatformEntity{

        private int study;
        private String p;

        public PlatformEntity(int study, String p){
            this.study = study;
            this.p = p;
        }

        public int getStudy() {
            return study;
        }

        public void setStudy(int study) {
            this.study = study;
        }

        public String getP() {
            return p;
        }

        public void setP(String p) {
            this.p = p;
        }
    }
}
