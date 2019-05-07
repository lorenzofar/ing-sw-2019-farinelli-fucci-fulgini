package it.polimi.deib.se2019.sanp4.adrenaline.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.LogManager;

public class ServerLauncher {

    public static void main(String[] args){
        ServerImpl server = new ServerImpl();

        ServerProperties properties = ServerProperties.getProperties();

        // Load configuration for the logger
        // First we check whether the user provided a config file
        String loggerConfigFilePath = System.getProperty("adrenaline.loggerconfig");
        if(loggerConfigFilePath == null) {
            // If not, we use default config file
            loggerConfigFilePath = Thread.currentThread().getContextClassLoader().getResource("").getPath() + "logger.properties";
        }
        try {
            InputStream loggerConfigStream = new FileInputStream(new File(loggerConfigFilePath));
            LogManager.getLogManager().readConfiguration(loggerConfigStream);
        } catch (IOException e) {
            // Error accessing the file
        }

        // Initialize server properties
        properties.loadProperties();

        server.run();
    }
}
