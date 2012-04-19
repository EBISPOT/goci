package uk.ac.ebi.fgpt.goci.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import uk.ac.ebi.fgpt.goci.model.GociUser;
import uk.ac.ebi.fgpt.goci.model.SimpleGociUser;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

/**
 * A basic implementation of a user DAO that can read and write GOCI users from an underlying JDBC datasource.
 *
 * @author Tony Burdett
 * Date 27/10/11
 */
public class JDBCGociUserDAO implements GociUserDAO {
    public static final String SEQUENCE_SELECT =
            "call next value for SEQ_GOCI";

    public static final String USER_SELECT =
            "select ID, USER_NAME, FIRST_NAME, LAST_NAME, EMAIL, RESTAPIKEY, PERMISSIONS " +
                    "from USER";
    public static final String USER_COUNT =
            "select count(*) from USER where ID = ?";
    public static final String USER_SELECT_BY_USERNAME = USER_SELECT + " " +
            "where USER_NAME = ?";
    public static final String USER_SELECT_BY_USER_ID = USER_SELECT + " " +
            "where ID = ?";
    public static final String USER_SELECT_BY_EMAIL = USER_SELECT + " " +
            "where EMAIL = ?";
    public static final String USER_SELECT_BY_REST_API_KEY = USER_SELECT + " " +
            "where RESTAPIKEY = ?";
    public static final String USER_INSERT =
            "insert into USER (ID, USER_NAME, FIRST_NAME, LAST_NAME, EMAIL, RESTAPIKEY, PERMISSIONS) " +
                    "values (?, ?, ?, ?, ?, ?, ?)";
    public static final String USER_UPDATE =
            "update USER " +
                    "set USER_NAME = ?, FIRST_NAME = ?, LAST_NAME = ?, EMAIL = ?, RESTAPIKEY = ?, PERMISSIONS = ? " +
                    "where ID = ?";
    public static final String USER_DELETE =
            "delete from USER where ID = ?";

    private JdbcTemplate jdbcTemplate;

    private Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Collection<GociUser> getUserByUserName(String userName) {
        return getJdbcTemplate().query(USER_SELECT_BY_USERNAME,
                                       new Object[]{userName},
                                       new GociUserMapper());
    }

    public GociUser getUser(String userID) {
        return getJdbcTemplate().queryForObject(USER_SELECT_BY_USER_ID,
                                                new Object[]{userID},
                                                new GociUserMapper());
    }

    public Collection<GociUser> getUserByEmail(String userEmailAddress) {
        return getJdbcTemplate().query(USER_SELECT_BY_EMAIL,
                                       new Object[]{userEmailAddress},
                                       new GociUserMapper());
    }

    public GociUser getUserByRestApiKey(String restApiKey) {
        return getJdbcTemplate().queryForObject(USER_SELECT_BY_REST_API_KEY,
                                                new Object[]{restApiKey},
                                                new GociUserMapper());
    }

    public GociUser saveUser(GociUser user) {
        int userCheck = 0;

        if (user.getId() != null) {
            userCheck = getJdbcTemplate().queryForInt(USER_COUNT,
                                                      user.getId());
        }

        //There is no such user in database
        if (userCheck == 0) {
            int userID = getJdbcTemplate().queryForInt(SEQUENCE_SELECT);
            getJdbcTemplate().update(USER_INSERT,
                                     userID,
                                     user.getUserName(),
                                     user.getFirstName(),
                                     user.getSurname(),
                                     user.getEmail(),
                                     user.getRestApiKey(),
                                     user.getPermissions().toString());
            if (user instanceof SimpleGociUser) {
                ((SimpleGociUser) user).setId(Integer.toString(userID));
            }
            else {
                getLog().warn("User acquired from database was of unexpected type " +
                                      user.getClass().getSimpleName() + ", cannot set user ID");
            }
        }
        else {
            getJdbcTemplate().update(USER_UPDATE,
                                     user.getUserName(), user.getFirstName(), user.getSurname(),
                                     user.getEmail(),
                                     user.getRestApiKey(), user.getPermissions().toString(), user.getId());
        }

        return user;
    }

    public Collection<GociUser> getUsers() {
        return getJdbcTemplate().query(USER_SELECT,
                                       new GociUserMapper());
    }

    public void deleteUser(GociUser user) {
        getJdbcTemplate().update(USER_DELETE,
                                 user.getId());
    }

    /**
     * Maps database rows to GociTask objects
     */
    private class GociUserMapper implements RowMapper<GociUser> {

        public GociUser mapRow(ResultSet resultSet, int i) throws
                SQLException {
            GociUser.Permissions permissions = GociUser.Permissions.GUEST;
            if (resultSet.getString(7) != null) {
                permissions = GociUser.Permissions.valueOf(resultSet.getString(7));
            }
            SimpleGociUser user = new SimpleGociUser(resultSet.getString(3),
                                                     resultSet.getString(4),
                                                     resultSet.getString(5),
                                                     resultSet.getString(6),
                                                     permissions);
            user.setId(resultSet.getString(1));
            return user;
        }
    }
}
