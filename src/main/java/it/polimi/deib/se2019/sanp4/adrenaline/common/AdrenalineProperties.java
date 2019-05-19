package it.polimi.deib.se2019.sanp4.adrenaline.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/** A class extending the Properties class to load and retrieve properties used by the server. Uses the singleton pattern */
public class AdrenalineProperties extends Properties {

    /** Default file name of the config file */
    private static final String CONFIG_FILENAME = "server.properties";

    /** Arguments that can be provided by the user */
    private static final String[] ARGUMENTS = {
            "adrenaline.skulls",
            "adrenaline.rmiport", "adrenaline.socketpoort",
            "adrenaline.turntime", "adrenaline.waitingtime",
            "adrenaline.maxspawnweapons",
            "adrenaline.initialplayerammo", "adrenaline.maxplayerammocubes", "adrenaline.maxplayerweapons", "adrenaline.maxplayerpowerups",
            "adrenaline.maxplayermarks", "adrenaline.killshotdamage",
            "adrenaline.skullscount"
    };

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
                InputStream configFileInputStream = new FileInputStream(new File(configFileName));
                this.load(configFileInputStream);
            } catch (IOException e) {
                logger.log(Level.SEVERE,"Error accessing provided file", e);
            }
        }
        else {
            // Here we search for a fallback file from the current folder
            try {
                FileInputStream defaultConfigStream = new FileInputStream(new File(CONFIG_FILENAME));
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
}
