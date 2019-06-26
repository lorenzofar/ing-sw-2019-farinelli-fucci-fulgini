package it.polimi.deib.se2019.sanp4.adrenaline.client;

import it.polimi.deib.se2019.sanp4.adrenaline.client.cli.CLIRenderer;
import it.polimi.deib.se2019.sanp4.adrenaline.client.gui.GUIRenderer;
import it.polimi.deib.se2019.sanp4.adrenaline.common.ResourcesLoader;

/**
 * A class providing an entry point to the client
 */
public class ClientLauncher {

    public static void main(String[] args) {

        // We initialize resources of the creators
        ResourcesLoader.loadCreatorResources();

        // We create the default rendering engine (GUI)
        UIRenderer renderer = new GUIRenderer();

        // We then check if the user chose different modes, by looking at the provided options
        String uiMode = System.getProperty("adrenaline.uimode");
        if (uiMode != null && uiMode.equalsIgnoreCase("cli")) {
            renderer = new CLIRenderer();
        }
        // Initialize the renderer
        renderer.initialize();
    }
}
