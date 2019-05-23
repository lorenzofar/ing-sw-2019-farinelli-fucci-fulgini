package it.polimi.deib.se2019.sanp4.adrenaline.server;

import it.polimi.deib.se2019.sanp4.adrenaline.common.AdrenalineProperties;

public class ServerLauncher {

    public static void main(String[] args){
        ServerImpl server = ServerImpl.getInstance();

        AdrenalineProperties properties = AdrenalineProperties.getProperties();

        // Load configuration for the logger
        AdrenalineProperties.loadLoggerConfig("/logger_server.properties");

        // Initialize server properties
        properties.loadProperties();

        server.run();
    }
}
