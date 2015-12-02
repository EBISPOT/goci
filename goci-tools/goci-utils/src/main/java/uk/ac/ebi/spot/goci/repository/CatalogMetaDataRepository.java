package uk.ac.ebi.spot.goci.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

/**
 * Created by dwelter on 19/03/15.
 */
@Repository
public class CatalogMetaDataRepository {

    private final DateFormat df;

    private JdbcTemplate jdbcTemplate;

    private static final String STUDY_COUNT =
            "SELECT COUNT(*) FROM(" +
                    "SELECT DISTINCT S.PUBMED_ID " +
                    "FROM STUDY S " +
                    "JOIN HOUSEKEEPING H ON H.ID = S.HOUSEKEEPING_ID " +
                    "WHERE H.CATALOG_PUBLISH_DATE IS NOT NULL AND H.CATALOG_UNPUBLISH_DATE IS NULL)";

    private static final String SNP_COUNT = "" +
            "SELECT COUNT(*) FROM(" +
            "SELECT DISTINCT SNP.RS_ID " +
            "FROM SINGLE_NUCLEOTIDE_POLYMORPHISM SNP " +
            "JOIN STUDY_SNP SN ON SN.SNP_ID = SNP.ID " +
            "JOIN STUDY S ON S.ID = SN.STUDY_ID " +
            "JOIN HOUSEKEEPING H ON H.ID = S.HOUSEKEEPING_ID " +
            "WHERE H.CATALOG_PUBLISH_DATE IS NOT NULL AND H.CATALOG_UNPUBLISH_DATE IS NULL)";


    private static final String ASSOCIATION_COUNT =
            "SELECT COUNT(*) FROM(" +
                    "SELECT DISTINCT SNP.RS_ID, D.TRAIT " +
                    "FROM SINGLE_NUCLEOTIDE_POLYMORPHISM SNP " +
                    "JOIN STUDY_SNP SN ON SN.SNP_ID = SNP.ID " +
                    "JOIN STUDY S ON S.ID = SN.STUDY_ID " +
                    "JOIN HOUSEKEEPING H ON H.ID = S.HOUSEKEEPING_ID " +
                    "JOIN STUDY_DISEASE_TRAIT SD ON SD.STUDY_ID = S.ID " +
                    "JOIN DISEASE_TRAIT D ON D.ID = SD.DISEASE_TRAIT_ID " +
                    "WHERE H.CATALOG_PUBLISH_DATE IS NOT NULL AND H.CATALOG_UNPUBLISH_DATE IS NULL)";


    @Autowired//(required = false)
    public CatalogMetaDataRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.df = new SimpleDateFormat("yyyy-MM-dd");
    }


    public void getMetaData(File statsFile) throws IOException {
        //open the file and copy all content into properties,
        //close file
        //get new data and added to file
        //write properties to new file


        Integer studycount = getStudyCount();
        System.out.println(studycount);
        Integer snpcount = getSNPCount();
        System.out.println(snpcount);
        Integer associationCount = getAssociationCount();
        System.out.println(associationCount);


        Date date = new Date();
        String today = df.format(date);

        FileInputStream in = new FileInputStream(statsFile);
        Properties props = new Properties();
        props.load(in);
        in.close();

        FileOutputStream out = new FileOutputStream(statsFile);
        props.setProperty("releasedate", today);
        props.setProperty("studycount", studycount.toString());
        props.setProperty("snpcount", snpcount.toString());
        props.setProperty("associationcount", associationCount.toString());
        props.store(out, null);
        out.close();

    }

    private Integer getAssociationCount() {
        return jdbcTemplate.queryForObject(ASSOCIATION_COUNT, Integer.class);
    }

    private Integer getSNPCount() {
        return jdbcTemplate.queryForObject(SNP_COUNT, Integer.class);
    }

    private Integer getStudyCount() {
        return jdbcTemplate.queryForObject(STUDY_COUNT, Integer.class);
    }
}
