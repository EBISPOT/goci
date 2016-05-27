package db.migration;

import org.flywaydb.core.api.migration.spring.SpringJdbcMigration;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by emma on 28/05/2015.
 *
 * @author emma
 *         <p>
 *         Script related to JIRA ticket: https://www.ebi.ac.uk/panda/jira/browse/GOCI-791. Aim is to backfil certain
 *         association attributes that indicate the type of association.
 */
public class V1_9_9_048__Association_type_backfill implements SpringJdbcMigration {

    private static final String SELECT_SNP_INTERACTION_ASSOCIATIONS_FOR_UPDATE = "SELECT DISTINCT a.ID\n" +
            "FROM ASSOCIATION a, ASSOCIATION_LOCUS al, LOCUS l\n" +
            "WHERE al.ASSOCIATION_ID = a.ID AND\n" +
            "al.LOCUS_ID=l.ID AND\n" +
            "l.DESCRIPTION = 'SNP x SNP interaction'";

    private static final String SELECT_HAPLOTYPE_ASSOCIATIONS_FOR_UPDATE = "SELECT DISTINCT a.ID\n" +
            "FROM ASSOCIATION a, ASSOCIATION_LOCUS al, LOCUS l\n" +
            "WHERE al.ASSOCIATION_ID = a.ID AND\n" +
            "al.LOCUS_ID=l.ID AND\n" +
            "l.DESCRIPTION LIKE '%aplotype%'";

    private static final String SELECT_SINGLE_VARIANT_ASSOCIATIONS_FOR_UPDATE = "SELECT DISTINCT a.ID\n" +
            "FROM ASSOCIATION a, ASSOCIATION_LOCUS al, LOCUS l\n" +
            "WHERE al.ASSOCIATION_ID = a.ID AND\n" +
            "al.LOCUS_ID=l.ID AND\n" +
            "l.DESCRIPTION = 'Single variant'";

    private static final String UPDATE_ASSOCIATION =
            "UPDATE ASSOCIATION SET SNP_INTERACTION = ?, MULTI_SNP_HAPLOTYPE = ? WHERE ID = ?";

    @Override public void migrate(JdbcTemplate jdbcTemplate) throws Exception {

        final List<Long> snpInteractionAssociations = new ArrayList<>();
        final List<Long> multiSnpAssociations = new ArrayList<>();
        final List<Long> singleVariantAssociations = new ArrayList<>();

        jdbcTemplate.query(SELECT_SNP_INTERACTION_ASSOCIATIONS_FOR_UPDATE, (resultSet, i) -> {
            Long associationId = resultSet.getLong(1);
            snpInteractionAssociations.add(associationId);
            return null;
        });

        jdbcTemplate.query(SELECT_HAPLOTYPE_ASSOCIATIONS_FOR_UPDATE, (resultSet, i) -> {
            Long associationId = resultSet.getLong(1);
            multiSnpAssociations.add(associationId);
            return null;
        });

        jdbcTemplate.query(SELECT_SINGLE_VARIANT_ASSOCIATIONS_FOR_UPDATE, (resultSet, i) -> {
            Long associationId = resultSet.getLong(1);
            singleVariantAssociations.add(associationId);
            return null;
        });

        for (Long associationId : snpInteractionAssociations) {
            jdbcTemplate.update(UPDATE_ASSOCIATION, 1, 0, associationId);
        }

        for (Long associationId : multiSnpAssociations) {
            jdbcTemplate.update(UPDATE_ASSOCIATION, 0, 1, associationId);
        }

        for (Long associationId : singleVariantAssociations) {
            jdbcTemplate.update(UPDATE_ASSOCIATION, 0, 0, associationId);
        }
    }
}
