package com.keycloak.userstorage;

import org.keycloak.component.ComponentModel;
import org.keycloak.credential.CredentialInput;
import org.keycloak.credential.CredentialInputUpdater;
import org.keycloak.credential.CredentialInputValidator;
import org.keycloak.models.*;
import org.keycloak.models.credential.PasswordCredentialModel;
import org.keycloak.storage.StorageId;
import org.keycloak.storage.UserStorageProvider;
import org.keycloak.storage.user.UserLookupProvider;
import org.keycloak.storage.user.UserQueryProvider;
import org.keycloak.storage.user.UserRegistrationProvider;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class CustomUserStorageProvider implements
        UserStorageProvider,
        UserRegistrationProvider,
        UserLookupProvider,
        UserQueryProvider,
        CredentialInputUpdater,
        CredentialInputValidator {

    private static final Log logger = Log.getLogger(UserRepository.class.getName());

    private final KeycloakSession session;
    private final ComponentModel model;
    private final UserRepository repository;

    public CustomUserStorageProvider(KeycloakSession session, ComponentModel model, UserRepository repository) {
        this.session = session;
        this.model = model;
        this.repository = repository;
    }

    @Override
    public boolean supportsCredentialType(String credentialType) {
        return PasswordCredentialModel.TYPE.equals(credentialType);
    }

    @Override
    public boolean isConfiguredFor(RealmModel realm, UserModel user, String credentialType) {
        return supportsCredentialType(credentialType);
    }

    // 登陆验证
    @Override
    public boolean isValid(RealmModel realm, UserModel user, CredentialInput input) {
        logger.info("Login is valid");
        if (!supportsCredentialType(input.getType()) || !(input instanceof UserCredentialModel)) {
            return false;
        }
        UserCredentialModel cred = (UserCredentialModel) input;
        // 用户名密码
        return repository.validateCredentials(user.getUsername(), cred.getChallengeResponse());
    }

    // 更新凭据
    @Override
    public boolean updateCredential(RealmModel realm, UserModel user, CredentialInput input) {
        if (!supportsCredentialType(input.getType()) || !(input instanceof UserCredentialModel)) {
            return false;
        }
        UserCredentialModel cred = (UserCredentialModel) input;
        return repository.updateCredentials(user.getUsername(), cred.getChallengeResponse());
    }

    @Override
    public Set<String> getDisableableCredentialTypes(RealmModel realm, UserModel user) {
        return Collections.emptySet();
    }

    @Override
    public void disableCredentialType(RealmModel realm, UserModel user, String credentialType) {
    }

    @Override
    public void preRemove(RealmModel realm) {
    }

    @Override
    public void preRemove(RealmModel realm, GroupModel group) {
    }

    @Override
    public void preRemove(RealmModel realm, RoleModel role) {
    }

    @Override
    public void close() {
    }

    @Override
    public UserModel getUserById(String id, RealmModel realm) {
        logger.info("Get user by userId(userId of keycloak): " + id);
        String externalId = StorageId.externalId(id);
        logger.info("Get user by userId(userId of external): " + externalId);
        return new UserAdapter(session, realm, model, repository.findUserById(externalId));
    }

    @Override
    public UserModel getUserByUsername(String username, RealmModel realm) {
        logger.info("Get User By Username: " + username);
        RemoteUser user = repository.findUserByUsernameOrEmail(username);
        if (user == null) {
            logger.info("User not exist: " + username);
            return null;
        }
        logger.info("User exist: " + user.getId() + ", " + user.getUsername());
        return new UserAdapter(session, realm, model, user);
    }

    @Override
    public UserModel getUserByEmail(String email, RealmModel realm) {
        // TODO
        return getUserByUsername(email, realm);
    }

    @Override
    public int getUsersCount(RealmModel realm) {
        return repository.getUsersCount();
    }

    @Override
    public List<UserModel> getUsers(RealmModel realm) {
        return repository.getAllUsers().stream()
                .map(user -> new UserAdapter(session, realm, model, user))
                .collect(Collectors.toList());
    }

    @Override
    public List<UserModel> getUsers(RealmModel realm, int firstResult, int maxResults) {
        // TODO
        return getUsers(realm);
    }

    @Override
    public List<UserModel> searchForUser(String search, RealmModel realm) {
        // TODO
        return repository.findUsers(search).stream()
                .map(user -> new UserAdapter(session, realm, model, user))
                .collect(Collectors.toList());
    }

    @Override
    public List<UserModel> searchForUser(String search, RealmModel realm, int firstResult, int maxResults) {
        // TODO
        return searchForUser(search, realm);
    }

    @Override
    public List<UserModel> searchForUser(Map<String, String> params, RealmModel realm) {
        // TODO
        return getUsers(realm);
    }

    @Override
    public List<UserModel> searchForUser(Map<String, String> params, RealmModel realm, int firstResult, int maxResults) {
        // TODO
        return getUsers(realm);
    }

    @Override
    public List<UserModel> getGroupMembers(RealmModel realm, GroupModel group, int firstResult, int maxResults) {
        // TODO
        return getUsers(realm);
    }

    @Override
    public List<UserModel> getGroupMembers(RealmModel realm, GroupModel group) {
        // TODO
        return getUsers(realm);
    }

    @Override
    public List<UserModel> searchForUserByUserAttribute(String attrName, String attrValue, RealmModel realm) {
        // TODO
        return getUsers(realm);
    }

    @Override
    public UserModel addUser(RealmModel realm, String username) {
        logger.info("Add user: " + username);
        RemoteUser user = new RemoteUser();
        user.setUsername(username);
        user = repository.createUser(user);
        return new UserAdapter(session, realm, model, user);
    }

    @Override
    public boolean removeUser(RealmModel realm, UserModel user) {
        logger.info("Remove user, userId(userId of keycloak): " + user.getId() + ", " + user.getUsername());
        logger.info("Remove user, userId(userId of external): " + StorageId.externalId(user.getId()));
        RemoteUser u = repository.findUserById(StorageId.externalId(user.getId()));
        if(u == null) {
            logger.info("User does not exist, userId(userId of keycloak): " + user.getId());
            return false;
        }
        repository.deleteUser(u.getId());
        return true;
    }
}
