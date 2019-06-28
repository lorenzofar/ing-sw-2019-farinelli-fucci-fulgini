package it.polimi.deib.se2019.sanp4.adrenaline.client;

import it.polimi.deib.se2019.sanp4.adrenaline.client.cli.CLIRenderer;
import it.polimi.deib.se2019.sanp4.adrenaline.client.gui.GUIRenderer;
import it.polimi.deib.se2019.sanp4.adrenaline.common.AdrenalineProperties;
import it.polimi.deib.se2019.sanp4.adrenaline.common.ResourcesLoader;

/**
 * A class providing an entry point to the client
 */
public class ClientLauncher {

    public static void main(String[] args) {

        // We initialize resources of the creators
        ResourcesLoader.loadCreatorResources();

        // Load properties
        AdrenalineProperties.getProperties().loadProperties();

        // Load configuration for the logger
        AdrenalineProperties.loadLoggerConfig("/logger_client.properties");

        // We then check if the user chose different modes, by looking at the provided options
        String uiMode = args.length > 0 ? args[0] : "";

        // We create the rendering engine
        UIRenderer renderer;
        if (uiMode.equals("cli")) {
            renderer = new CLIRenderer();
        } else {
            renderer = new GUIRenderer();
        }
        // Initialize the renderer
        renderer.initialize();
    }
}
