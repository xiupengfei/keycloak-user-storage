package com.keycloak.userstorage;

/**
 * 类似 Keycloak 默认提供用户
 * */

public class RemoteUser {

    private String id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String password;
    private Boolean enabled;

    public RemoteUser() {
    }

    public RemoteUser(String id, String username, String password, String email, Boolean enabled) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.enabled = enabled;
        this.firstName = null;
        this.lastName = null;
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public void setEnabled(Boolean enabled ) { this.enabled = enabled; }

    public Boolean getEnabled() { return this.enabled;}
}
