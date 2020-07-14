package com.keycloak.userstorage;

import java.util.logging.Logger;

public class Log {
    private Logger logger;
    private Log(String className) {
        logger = Logger.getLogger(className);
    }
    static Log getLogger(String className) {
        return new Log(className);
    }

    void info(String msg) {
        logger.info("******************" + msg + "******************");
    }

    public void warning(String msg) {
        logger.warning("******************" + msg + "******************");
    }
}
