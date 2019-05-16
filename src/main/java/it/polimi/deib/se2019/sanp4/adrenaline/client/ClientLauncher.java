package it.polimi.deib.se2019.sanp4.adrenaline.client;

import it.polimi.deib.se2019.sanp4.adrenaline.client.gui.GUIRenderer;

/** A class providing an entry point to the client */
public class ClientLauncher {

    public static void main(String[] args){
        // We create the default rendering engine (CLI)
        UIRenderer renderer = new GUIRenderer();

        // We then check if the user chose different modes, by looking at the provided options
        String uiMode = System.getProperty("adrenaline.uimode");
        if(uiMode != null && uiMode.equalsIgnoreCase("gui")) {
            renderer = new GUIRenderer();
        }
        // Initialize the renderer
        renderer.initialize();
    }
}
