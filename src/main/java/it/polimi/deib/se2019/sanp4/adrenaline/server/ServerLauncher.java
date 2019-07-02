package it.polimi.deib.se2019.sanp4.adrenaline.server;

import it.polimi.deib.se2019.sanp4.adrenaline.common.AdrenalineProperties;
import it.polimi.deib.se2019.sanp4.adrenaline.common.ResourcesLoader;

/**
 * Main class for the server
 */
public class ServerLauncher {

    /**
     * Main method to launch the server.
     * <ul>
     *     <li>Loads the JSON resources and schemas</li>
     *     <li>Loads the configuration and logging parameters for the server</li>
     *     <li>Starts the server, which will be listening for incoming connection</li>
     * </ul>
     * @param args Array of command line arguments, not null
     */
    public static void main(String[] args) {

        ResourcesLoader.loadCreatorResources();

        ServerImpl server = ServerImpl.getInstance();

        AdrenalineProperties properties = AdrenalineProperties.getProperties();

        // Load configuration for the logger
        AdrenalineProperties.loadLoggerConfig("/logger_server.properties");

        // Initialize server properties
        properties.loadProperties();

        server.run();
    }
}
