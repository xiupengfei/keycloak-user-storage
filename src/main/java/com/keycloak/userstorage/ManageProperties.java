package com.keycloak.userstorage;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Set;

public class ManageProperties {
    private static final Log logger = Log.getLogger(UserRepository.class.getName());
    private static ManageProperties instance = null;
    private Properties properties = new Properties();

    public static ManageProperties getInstance() {
        if(instance == null) {
            instance = new ManageProperties();
        }
        return instance;
    }

    private void printInfo() {
        Set<Object> propKeySet = properties.keySet();
        for(Object propertyKey : propKeySet) {
            String propValue = properties.getProperty(propertyKey.toString());
            if(propValue!=null) {
                System.out.println("--------------------====" + propertyKey.toString() + ":" + propValue);
            }
        }
    }

    private ManageProperties() {
        try {
            InputStream fis = ManageProperties.class.getClassLoader().getResourceAsStream("database.properties");
            properties.load(fis);
            this.printInfo();
        } catch (IOException e) {
            logger.info("Manage properties field: " + e.getMessage());
        }
    }

    public String getStrPropertyValue(String name) {
        String var = System.getenv(name);
        if(var != null) {
            logger.info("System variable, key: " + name + ", value: " + var);
            return var;
        }
        var = properties.getProperty(name);
        logger.info("User variable, key: " + name + ", value: " + var);
        return var;
    }

    public int getIntPropertyValue(String key, int defaultValue) {
        String data = getStrPropertyValue(key);
        try {
            int valor = Integer.parseInt(data);
            return valor;
        } catch (Exception e) {
            logger.info("Get Int Property Value field: " + e.getMessage());
            return defaultValue;
        }
    }

    public static void main(String...argv) {
        ManageProperties.getInstance();
    }
}
