package com.keycloak.userstorage;

import org.keycloak.common.util.MultivaluedHashMap;
import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.provider.ProviderConfigurationBuilder;
import org.keycloak.storage.UserStorageProviderFactory;

import java.beans.PropertyVetoException;
import java.util.List;

public class CustomUserStorageProviderFactory implements UserStorageProviderFactory<CustomUserStorageProvider> {
    private final Log logger = Log.getLogger(CustomUserStorageProviderFactory.class.getName());
    private final ManageProperties mp = ManageProperties.getInstance();

    @Override
    public CustomUserStorageProvider create(KeycloakSession session, ComponentModel model) {
//        MultivaluedHashMap<String, String> config = model.getConfig();
        // 在这设置用户存储提供程序，启动一些连接。
        DatabaseConnector dbc = DatabaseConnector.getInstance();
//        String username = config.getFirst(Constants.DB_USERNAME);
//        String password = config.getFirst(Constants.DB_PASSWORD);
//        String url = config.getFirst(Constants.DB_URL);
//        String driver = config.getFirst(Constants.DB_DRIVER_CLASS);
//        try {
//            DatabaseConnector.refresh(username, password, url, driver);
//        } catch (PropertyVetoException e) {
//            logger.warning("Refresh Connection field: " + e.toString());
//        }

        UserRepository repository = new UserRepository(dbc);
        return new CustomUserStorageProvider(session, model, repository);
    }

    @Override
    public String getId() {
        String id = mp.getStrPropertyValue(Constants.PROVIDER_NAME);
        logger.info("CustomUserStorageProviderFactory::getId: "+ id);
        return id;
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return ProviderConfigurationBuilder.create()
                .property()
                .name(Constants.DB_URL)
                .type(ProviderConfigProperty.STRING_TYPE)
                .label("Jdbc URL")
                .defaultValue(mp.getStrPropertyValue(Constants.DB_URL))
                .helpText("Jdbc Url ?")
                .add()

                .property()
                .name(Constants.DB_DRIVER_CLASS)
                .type(ProviderConfigProperty.STRING_TYPE)
                .label("DB Connection Driver")
                .defaultValue(mp.getStrPropertyValue(Constants.DB_DRIVER_CLASS))
                .add()

                .property()
                .name(Constants.DB_USERNAME)
                .type(ProviderConfigProperty.STRING_TYPE)
                .label("DB UserName")
                .defaultValue(mp.getStrPropertyValue(Constants.DB_USERNAME))
                .add()

                .property()
                .name(Constants.DB_PASSWORD)
                .type(ProviderConfigProperty.STRING_TYPE)
                .label("DB Password")
                .defaultValue(mp.getStrPropertyValue(Constants.DB_PASSWORD))
                .add()

                .build();
    }
}
