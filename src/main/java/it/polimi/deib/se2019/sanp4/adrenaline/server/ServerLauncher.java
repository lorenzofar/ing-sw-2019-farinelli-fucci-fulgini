package it.polimi.deib.se2019.sanp4.adrenaline.server;

import it.polimi.deib.se2019.sanp4.adrenaline.common.AdrenalineProperties;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class ServerLauncher {

    private static Logger logger = Logger.getLogger(ServerLauncher.class.getName());

    public static void main(String[] args){
        ServerImpl server = ServerImpl.getInstance();

        AdrenalineProperties properties = AdrenalineProperties.getProperties();

        // Load configuration for the logger
        loadLoggerConfig();

        // Initialize server properties
        properties.loadProperties();

        server.run();
    }

    static void loadLoggerConfig() {
        InputStream loggerConfigStream = null;

        /* First check if the user provided a path for the logging file */
        String loggerConfigFilePath = System.getProperty("adrenaline.loggerconfig");
        if (loggerConfigFilePath != null) {
            /* Try to load the configuration from that file */
            try {
                loggerConfigStream = new FileInputStream(loggerConfigFilePath);
            } catch (FileNotFoundException e) {
                logger.log(Level.WARNING,
                        "Cannot load provided logging configuration file from {0}", loggerConfigFilePath);
            }
        }

        /* If there is no custom configuration file, we use the internal configuration file */
        if (loggerConfigStream == null) {
            loggerConfigStream = ServerLauncher.class.getResourceAsStream("logger.properties");
        }

        /* Try to read the file */
        if (loggerConfigStream != null) {
            try {
                LogManager.getLogManager().readConfiguration(loggerConfigStream);
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Could not load default logging configuration");
            }
        }
    }
}
