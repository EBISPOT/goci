package db.migration;

import org.flywaydb.core.api.migration.spring.SpringJdbcMigration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by emma on 16/06/2015.
 *
 * @author emma
 *         <p>
 *         Script related to JIRA ticket: https://www.ebi.ac.uk/panda/jira/browse/GOCI-848. Aim is populate LOCATION and
 *         SNP_LOCATION tables
 */
public class V2_0_1_005__Populate_locations_and_snp_location implements SpringJdbcMigration {

    private static final String SELECT_LOCATION_DETAILS =
            "SELECT s.ID, s.CHROMOSOME_NAME, s.CHROMOSOME_POSITION, r.ID " +
                    "FROM SINGLE_NUCLEOTIDE_POLYMORPHISM s , SNP_REGION rs, REGION r  " +
                    "WHERE r.ID = rs.REGION_ID and s.ID = rs.SNP_ID";

    @Override public void migrate(JdbcTemplate jdbcTemplate) throws Exception {

        // Go through list of potential locations
        jdbcTemplate.query(SELECT_LOCATION_DETAILS, (resultSet, i) -> {
            Long snpId = resultSet.getLong(1);
            String chromosomeName = resultSet.getString(2);
            String chromosomePosition = resultSet.getString(3);
            Long regionId = resultSet.getLong(4);

            // Insert into LOCATION table
            SimpleJdbcInsert insertLocation =
                    new SimpleJdbcInsert(jdbcTemplate)
                            .withTableName("LOCATION")
                            .usingColumns("CHROMOSOME_NAME", "CHROMOSOME_POSITION", "REGION_ID")
                            .usingGeneratedKeyColumns("ID");


            Map<String, Object> locationArgs = new HashMap<>();
            locationArgs.put("CHROMOSOME_NAME", chromosomeName);
            locationArgs.put("CHROMOSOME_POSITION", chromosomePosition);
            locationArgs.put("REGION_ID", regionId);
            Number locationId = insertLocation.executeAndReturnKey(locationArgs);

            // Insert into SNP_LOCATION table
            SimpleJdbcInsert insertSnpLocation =
                    new SimpleJdbcInsert(jdbcTemplate)
                            .withTableName("SNP_LOCATION")
                            .usingColumns("SNP_ID", "LOCATION_ID");

            Map<String, Object> snpLocationArgs = new HashMap<>();
            snpLocationArgs.put("SNP_ID", snpId);
            snpLocationArgs.put("LOCATION_ID", locationId.longValue());
            insertSnpLocation.execute(snpLocationArgs);

            return null;
        });
    }
}
