package it.polimi.deib.se2019.sanp4.adrenaline.client.cli;

import it.polimi.deib.se2019.sanp4.adrenaline.client.*;
import it.polimi.deib.se2019.sanp4.adrenaline.common.exceptions.LoginException;

import java.io.IOException;
import java.util.Arrays;

public class CLIRenderer implements UIRenderer {

    private ClientView clientView;

    /* ASCII-art version of the game title */
    private static final String ADRENALINE_TITLE =
            "    ___    ____  ____  _______   _____    __    _____   ________\n" +
            "   /   |  / __ \\/ __ \\/ ____/ | / /   |  / /   /  _/ | / / ____/\n" +
            "  / /| | / / / / /_/ / __/ /  |/ / /| | / /    / //  |/ / __/\n" +
            " / ___ |/ /_/ / _, _/ /___/ /|  / ___ |/ /____/ // /|  / /_\n" +
            "/_/  |_/_____/_/ |_/_____/_/ |_/_/  |_/_____/___/_/ |_/_____/ \n";


    @Override
    public void initialize() {
        // Create a new client view
        this.clientView = new ClientView();

        // We create the default server connection (SOCKET)
        this.clientView.setServerConnection(new SocketServerConnection());

        // We print the title
        CLIHelper.print(ADRENALINE_TITLE);
        CLIHelper.print("Welcome to adrenaline!");

        // We ask the user to select a network connection mode
        CLIHelper.printTitle("network configuration");
        int selectedNetworkMode = CLIHelper.askOptionFromList(
                "Select the network connection to use",
                Arrays.asList("socket", "rmi"));
        if(selectedNetworkMode == 1) {
            this.clientView.setServerConnection(new RMIServerConnection(clientView));
        }

        // First ask the user to set up network connection
        setUpNetworkConnection();
        //  Then make it log in to the server
        performLogin();

        // Then we set the client view in the renderer
        clientView.setRenderer(this);
    }

    /**
     * Prompts the user to enter the hostname of the server
     * and tries to connect to it using the selected network connection
     */
    private void setUpNetworkConnection(){
        boolean connected = false;
        while(!connected){
            try {
                String serverHostname = CLIHelper.parseString("Enter the hostname of the server");
                this.clientView.getServerConnection().connect(serverHostname);
                connected = true;
            } catch (IOException e) {
                connected = false;
            }
        }
    }

    /**
     * Prompts the user to enter his username and tries to log in to the server
     */
    private void performLogin(){
        CLIHelper.printTitle("login");
        String username = CLIHelper.parseString("Enter your username");
        boolean loggedIn = false;
        while(!loggedIn) {
            try {
                this.clientView.getServerConnection().login(username);
                loggedIn = true;
            } catch (IOException e) {
                CLIHelper.print("An error occurred while logging in");
                //Retry
            } catch (LoginException e) {
                CLIHelper.print("The username is not available");
                loggedIn = false;
                username = CLIHelper.parseString("Enter your username");
            }
        }
    }
}