package com.keycloak.userstorage;

class Constants {
    static final String PROVIDER_NAME = "provider.name";
    static final String DB_USERNAME     = "db.username";
    static final String DB_PASSWORD     = "db.password";
    static final String DB_URL          = "db.url";
    static final String DB_DRIVER_CLASS = "driver.class.name";

    // SQL
    static final String SQL_UPDATE_USER_CREDENTIAL = "UPDATE users SET password=? WHERE username=?";
    static final String SQL_QUERY_ALL_USER = "SELECT * FROM users";
    static final String SQL_DELETE_USER = "DELETE FROM users WHERE id=?";
    static final String SQL_CREATE_USER = "INSERT INTO users(username) VALUES(?)";
}
