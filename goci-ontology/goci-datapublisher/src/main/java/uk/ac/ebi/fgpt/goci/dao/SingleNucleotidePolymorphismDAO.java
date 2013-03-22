package uk.ac.ebi.fgpt.goci.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import uk.ac.ebi.fgpt.goci.lang.FilterProperties;
import uk.ac.ebi.fgpt.goci.lang.UniqueID;
import uk.ac.ebi.fgpt.goci.model.SingleNucleotidePolymorphism;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

/**
 * A data access object capable of retrieving {@link uk.ac.ebi.fgpt.goci.model.SingleNucleotidePolymorphism} objects
 * from the GWAS database
 *
 * @author Tony Burdett
 * Date 24/01/12
 */
public class SingleNucleotidePolymorphismDAO {
    private static final String SNP_SELECT_MAIN =
            "select distinct ID, SNP, REGION, CHROMOSOME, LOCATION from (" +
                    "select s.ID, s.SNP, r.REGION, g.CHR_ID as CHROMOSOME, g.CHR_POS as LOCATION from GWASSNP s " +
                    "join GWASSNPXREF sx on s.ID=sx.SNPID " +
                    "join GWASSTUDIESSNP g on sx.GWASSTUDIESSNPID=g.ID " +
                    "join GWASREGIONXREF rx on rx.GWASSTUDIESSNPID=g.ID " +
                    "join GWASREGION r on r.ID=rx.REGIONID " +
                    "where g.ID is not null and s.SNP is not null and r.REGION is not null " +
                    "and g.SNPPENDING = 0 " +
                    "and g.CHR_ID is not null and g.CHR_POS is not null ";

    private static final String SNP_SELECT_END = ")";

    private JdbcTemplate jdbcTemplate;

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Collection<SingleNucleotidePolymorphism> retrieveAllSNPs() {
        if(FilterProperties.getPvalueFilter() == null){
            String snp_select = SNP_SELECT_MAIN.concat(SNP_SELECT_END);
            return getJdbcTemplate().query(snp_select, new SNPMapper());        }
        else{
            String pval_filter = "and g.PVALUEFLOAT <= " + FilterProperties.getPvalueFilter();
            String filtered_query = SNP_SELECT_MAIN.concat(pval_filter).concat(SNP_SELECT_END);
            return getJdbcTemplate().query(filtered_query, new SNPMapper());
        }
    }


    private class SNPMapper implements RowMapper<SingleNucleotidePolymorphism> {
        public SingleNucleotidePolymorphism mapRow(ResultSet resultSet, int i) throws SQLException {
            String id = resultSet.getString(1);
            String rsID = resultSet.getString(2);
            String bandName = resultSet.getString(3);
            String chromosomeName = resultSet.getString(4);
            String location = resultSet.getString(5);

            return new SnpFromDB(id, rsID, chromosomeName, bandName, location);
        }
    }

    private class SnpFromDB implements SingleNucleotidePolymorphism {
        private String id;
        private String rsID;
        private String chromosomeName;
        private String bandName;
        private String location;

        public SnpFromDB(String id, String rsID, String chromosomeName, String bandName, String location) {
            this.id = id;
            this.rsID = rsID;

            if(chromosomeName == null){
                if(bandName.contains("p")){
                    this.chromosomeName = bandName.split("p")[0];
                }
                else{
                    this.chromosomeName = bandName.split("q")[0];
                }
            }
            else{
                this.chromosomeName = chromosomeName;
            }
            this.bandName = bandName;
            this.location = location;
        }

//        @UniqueID
        private String getID() {
            return id;
        }

        @UniqueID
        public String getRSID() {
            return rsID;
        }

        public String getChromosomeName() {
            return chromosomeName;
        }

        public String getCytogeneticBandName() {
            return bandName;
        }

        public String getSNPLocation() {
            return location;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            SnpFromDB snpFromDB = (SnpFromDB) o;
            return !(id != null ? !id.equals(snpFromDB.id) : snpFromDB.id != null);
        }

        @Override
        public int hashCode() {
            return id != null ? id.hashCode() : 0;
        }
    }
}
