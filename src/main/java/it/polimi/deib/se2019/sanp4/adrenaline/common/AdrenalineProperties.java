package it.polimi.deib.se2019.sanp4.adrenaline.common;

import it.polimi.deib.se2019.sanp4.adrenaline.server.ServerLauncher;

import java.io.*;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/** A class extending the Properties class to load and retrieve properties used by the server. Uses the singleton pattern */
public class AdrenalineProperties extends Properties {

    /** Default file name of the config file */
    private static final String CONFIG_FILENAME = "adrenaline.properties";

    /** Arguments that can be provided by the user */
    private static final String[] ARGUMENTS = {
            "adrenaline.rmiport", "adrenaline.socketport",
            "adrenaline.turntime", "adrenaline.waitingtime",
            "adrenaline.hostname"
    };
    private static final long serialVersionUID = -2776486894760552339L;

    private static AdrenalineProperties instance = new AdrenalineProperties();

    private static final Logger logger = Logger.getLogger(AdrenalineProperties.class.getName());

    /**
     * Returns the instance of the class
     * @return The object representing the instance
     */
    public static AdrenalineProperties getProperties() {
        return instance;
    }

    /**
     * Performs initial loading of server properties
     * It first checks whether the user provided a config file to be used,
     * otherwise checking for its existence in the application folder.
     * After that it checks whether the user passed any of the default arguments defined in {@link #ARGUMENTS},
     * overwriting properties accordingly.
     */
    public void loadProperties(){
        // We first check whether the user has provided a config file
        String configFileName = System.getProperty("adrenaline.config");
        if(configFileName != null){
            // Load properties from file
            try {
                InputStream configFileInputStream = new FileInputStream(configFileName);
                this.load(configFileInputStream);
            } catch (IOException e) {
                logger.log(Level.SEVERE,"Error accessing provided file", e);
            }
        }
        else {
            // Here we search for a fallback file from the current folder
            try {
                FileInputStream defaultConfigStream = new FileInputStream(CONFIG_FILENAME);
                this.load(defaultConfigStream);
            } catch (IOException e) {
                // The file does not exist
                logger.log(Level.FINE, "Default config file does not exist");
            }
        }
        // Then we load command line parameters that override the current ones
        // We iterate over the default arguments and check whether the user set them
        for (String argument : ARGUMENTS) {
            String argumentValue = System.getProperty(argument);
            if(argumentValue == null) continue;
            this.setProperty(argument, argumentValue);
        }
    }

    /**
     * Loads the logging configuration:
     * <ol>
     *     <li>From the file specified in property adrenaline.loggerconfig</li>
     *     <li>If the property is not specified, it uses the resource provided as {@code fallbackConfig}</li>
     * </ol>
     * @param fallbackConfig the path of the resource on which to fallback
     */
    public static void loadLoggerConfig(String fallbackConfig) {
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
            loggerConfigStream = ServerLauncher.class.getResourceAsStream(fallbackConfig);
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
