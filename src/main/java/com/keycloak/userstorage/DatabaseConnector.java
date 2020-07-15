package com.keycloak.userstorage;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseConnector {

    private static final String DB_MIN_POOL_SIZE     = "db.minPoolSize";
    private static final String DB_MAX_POOL_SIZE     = "db.maxPoolSize";
    private static final String DB_ACQUIRE_INCREMENT = "db.acquireIncrement";
    private static final String DB_MAX_IDLE_TIME    = "db.maxIdleTime";
    private static final String DB_IDLE_CONNECTION_TEST_PERIOD = "db.idleConnectionTestPeriod";

    private static DatabaseConnector instance = null;
    private static ComboPooledDataSource dataSource = new ComboPooledDataSource();
    private ManageProperties mp = ManageProperties.getInstance();
    private static final Log logger = Log.getLogger(CustomUserStorageProviderFactory.class.getName());

    public static DatabaseConnector getInstance() {
        if(instance == null) {
            try {
                instance = new DatabaseConnector();
            } catch (PropertyVetoException e) {
                // 数据库连接失败
                logger.warning("Failed to connect to the database: " + e.getMessage());
            }
        }
        return instance;
    }

//    public static DatabaseConnector getInstance(String user, String password, String url, String driver) {
//        if(instance==null) {
//            try {
//                instance = new DatabaseConnector(user, password, url, driver);
//            } catch (PropertyVetoException e) {
//                logger.warning("Failed to connect to the database: " + e.getMessage());
//            }
//        }
//        return instance;
//    }

    public static void refresh(String user, String password, String url, String driver) throws PropertyVetoException {
        dataSource.setDriverClass(driver);
        dataSource.setJdbcUrl(url);
        dataSource.setUser(user);
        dataSource.setPassword(password);
    }

    private DatabaseConnector() throws PropertyVetoException {
        dataSource.setDriverClass(mp.getStrPropertyValue(Constants.DB_DRIVER_CLASS));
        dataSource.setJdbcUrl(mp.getStrPropertyValue(Constants.DB_URL));
        dataSource.setUser(mp.getStrPropertyValue(Constants.DB_USERNAME));
        dataSource.setPassword(mp.getStrPropertyValue(Constants.DB_PASSWORD));

        dataSource.setMinPoolSize(mp.getIntPropertyValue(DB_MIN_POOL_SIZE, 3));
        dataSource.setMaxPoolSize(mp.getIntPropertyValue(DB_MAX_POOL_SIZE, 24));
        dataSource.setAcquireIncrement(mp.getIntPropertyValue(DB_ACQUIRE_INCREMENT, 4));

        //https://github.com/metabase/metabase/issues/10063
        dataSource.setMaxIdleTime(mp.getIntPropertyValue(DB_MAX_IDLE_TIME, 600));
        dataSource.setIdleConnectionTestPeriod(mp.getIntPropertyValue(DB_IDLE_CONNECTION_TEST_PERIOD, 300));
    }

//    private DatabaseConnector(String user, String password, String url, String driver) throws PropertyVetoException {
//        this();
//        dataSource.setDriverClass(driver);
//        dataSource.setJdbcUrl(url);
//        dataSource.setUser(user);
//        dataSource.setPassword(password);
//    }

    public boolean updateCredentials(String username, String password) {
        logger.info("Update User Credentials, Username: " + username + ", Password: [" + password + "]");
        Connection con = null;
        try {
            con = dataSource.getConnection();
            PreparedStatement pstmt = con.prepareStatement(Constants.SQL_UPDATE_USER_CREDENTIAL);
            pstmt.setString(1, password);
            pstmt.setString(2, username);
            pstmt.execute();
            return true;
        } catch (SQLException e) {
            logger.warning("Update User Credentials Field: " + e.toString());
        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    logger.warning("Close DB Connection Field: " + e.toString());
                }
            }
        }
        return false;
    }

    public List<RemoteUser> getAllUsers() {
        List<RemoteUser> remoteUsers = new ArrayList<>();
        logger.info("getAllUsers");
        Connection con = null;
        try {
            con = dataSource.getConnection();
            PreparedStatement pstmt = con.prepareStatement(Constants.SQL_QUERY_ALL_USER);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String id = "" + rs.getInt(1);
                String username = rs.getString(2);
                Boolean enabled = rs.getBoolean(3);
                String email = rs.getString(4);
                String password = rs.getString(5);
                remoteUsers.add(new RemoteUser(id, username, password, email, enabled));
            }
        } catch (SQLException e) {
            logger.warning("Get All Users Field: " + e.toString());
        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    logger.warning("Close DB Connection Field: " + e.toString());
                }
            }
        }
        return remoteUsers;
    }

    public void deleteUser(String userId) {
        logger.info("deleteUser, userId: " + userId);
        Connection con = null;
        try {
            con = dataSource.getConnection();
            PreparedStatement pstmt = con.prepareStatement(Constants.SQL_DELETE_USER);
            pstmt.setString(1, userId);
            pstmt.execute();
        } catch (SQLException e) {
            logger.warning("Delete User Field: " + e.toString());
        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    logger.warning("Close DB Connection Field: " + e.toString());
                }
            }
        }
    }

    public RemoteUser addUser(RemoteUser user) {
        logger.info("addUser, userName: " + user.getUsername());
        Connection con = null;
        try {
            con = dataSource.getConnection();
            PreparedStatement pstmt = con.prepareStatement(Constants.SQL_CREATE_USER);
            pstmt.setString(1, user.getUsername());
            pstmt.execute();
            return user;
        } catch (SQLException e) {
            logger.warning("Add User Field: " + e.toString());
        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    logger.warning("Close DB Connection Field: " + e.toString());
                }
            }
        }
        return null;
    }
}
