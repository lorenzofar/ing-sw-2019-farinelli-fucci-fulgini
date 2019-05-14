package it.polimi.deib.se2019.sanp4.adrenaline.client;

import it.polimi.deib.se2019.sanp4.adrenaline.client.cli.CLIRenderer;
import it.polimi.deib.se2019.sanp4.adrenaline.client.gui.GUIRenderer;

public class ClientLauncher {

    public static void main(String[] args){
        ClientView clientView = new ClientView();

        // We create the default rendering engine (CLI)
        UIRenderer renderer = new CLIRenderer();
        // And we also create the default server connection (SOCKET)
        ServerConnection serverConnection = new SocketServerConnection();

        // We then check if the user chose different modes, by looking at the provided options
        String uiMode = System.getProperty("adraline.uimode");
        String networkMode = System.getProperty("adrenaline.networkmode");
        if(uiMode != null && uiMode.equalsIgnoreCase("gui")) {
            renderer = new GUIRenderer();
        }
        if(networkMode != null && networkMode.equalsIgnoreCase("rmi")){
            serverConnection = new RMIServerConnection(clientView);
        }

        // We then set the renderer and network connection
        clientView.setRenderer(renderer);
        clientView.setServerConnection(serverConnection);

        // We eventually start the client
        clientView.start();
    }
}
