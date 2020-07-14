package com.keycloak.userstorage;

import java.util.List;
import java.util.stream.Collectors;

class UserRepository {
    private static final Log logger = Log.getLogger(UserRepository.class.getName());
    private DatabaseConnector dbc;

    public UserRepository(DatabaseConnector dbc) {
        this.dbc = dbc;
    }

    private List<RemoteUser> users() {
        return dbc.getAllUsers();
    }

    public List<RemoteUser> getAllUsers() {
        return users();
    }

    public int getUsersCount() {
        return users().size();
    }

    public RemoteUser findUserById(String id) {
        return users().stream().filter(user -> user.getId().equals(id)).findFirst().orElse(null);
    }

    public RemoteUser findUserByUsernameOrEmail(String username) {
        // TODO 待优化
        logger.info("findUserByUsernameOrEmail: " + username);
        for(RemoteUser user : users()) {
            if(user.getUsername().equals(username) || (user.getEmail() != null && user.getEmail().equalsIgnoreCase(username))) {
                logger.info("User exist, userId: " + user.getId() + ", username: " + user.getUsername());
                return user;
            }
        }
        logger.info("User does not exist: " + username);
        return null;
        // return new RemoteUser("1000", username, "admin", null, null, null);
    }

    public List<RemoteUser> findUsers(String query) {
        return users().stream()
                .filter(user -> user.getUsername().contains(query) || user.getEmail().contains(query))
                .collect(Collectors.toList());
    }

    /**
     * 凭据验证
     * */
    public boolean validateCredentials(String username, String password) {
        logger.info("***********登录验证凭据***********: ["+username+"] ["+password+"]");
        RemoteUser user = findUserByUsernameOrEmail(username);
        if (user == null) {
            return false;
        }
        String pass = user.getPassword();
        return pass !=null && pass.equals(password);
    }

    public boolean updateCredentials(String username, String password) {
        return dbc.updateCredentials(username, password);
    }

    public void deleteUser(String userId) {
        dbc.deleteUser(userId);
    }

    public RemoteUser createUser(RemoteUser user) {
        return dbc.addUser(user);
    }

}
