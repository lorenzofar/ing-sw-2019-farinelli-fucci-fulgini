package it.polimi.deib.se2019.sanp4.adrenaline.client.cli;

import it.polimi.deib.se2019.sanp4.adrenaline.client.ClientView;
import it.polimi.deib.se2019.sanp4.adrenaline.client.UIRenderer;
import it.polimi.deib.se2019.sanp4.adrenaline.common.exceptions.LoginException;
import it.polimi.deib.se2019.sanp4.adrenaline.view.MessageType;
import it.polimi.deib.se2019.sanp4.adrenaline.view.ViewScene;

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
        clientView = new ClientView();

        // We set the renderer in the client view
        clientView.setRenderer(this);

        CLIHelper.clearScreen();

        // We print the title
        CLIHelper.println(ADRENALINE_TITLE);
        CLIHelper.println("Welcome to adrenaline!");

        // We ask the user to select a network connection mode
        CLIHelper.printTitle("network configuration");
        String selectedNetworkMode = CLIHelper.askOptionFromList("Select the network connection to use", Arrays.asList("socket", "rmi"));
        if(selectedNetworkMode.equals("socket")) {
            clientView.setSocketConnection();
        }
        else {
            clientView.setRMIConnection();
        }

        // First ask the user to set up network connection
        setUpNetworkConnection();
        //  Then make it log in to the server
        performLogin();
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
                clientView.getServerConnection().connect(serverHostname);
                connected = true;
            } catch (IOException e) {
                CLIHelper.printError("Error connecting to the server");
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
                clientView.getServerConnection().login(username);
                loggedIn = true;
            } catch (IOException e) {
                CLIHelper.printError("An error occurred while logging in");
            } catch (LoginException e) {
                CLIHelper.printError("The username is not available");
                loggedIn = false;
                username = CLIHelper.parseString("Enter your username");
            }
        }
        // The user has finally logged in, we update the scene
        //TODO: Check who is responsible of updating the scene
        clientView.selectScene(ViewScene.LOBBY);
    }

    @Override
    public void showLobby() {
        CLIHelper.clearScreen();
        CLIHelper.printTitle("waiting room");
        CLIHelper.println("The game is about to start, wait for other players to join");
        CLIHelper.startSpinner();
    }

    /**
     * Notify the user about the imminent start of the game
     */
    @Override
    public void startWaitingMatch() {
        CLIHelper.println("The match is starting soon");
    }

    /**
     * Cancel the waiting indicators when the game start is cancelled (e.g. due to lack of players)
     */
    @Override
    public void cancelWaitingMatch() {
        CLIHelper.println("There are not enough players, we'll wait a bit more...");
    }

    /**
     * Prepare the client for the game
     * Triggered when the match starts
     */
    @Override
    public void showMatchScreen() {
        CLIHelper.stopSpinner();
        CLIHelper.clearScreen();
        //TODO: Implement this method
    }

    /**
     * Shows a message to the user
     *
     * @param text The text of the message
     * @param type The type of the message
     */
    @Override
    public void showMessage(String text, MessageType type) {
        CLIHelper.printlnColored(text, type.getAnsiCode());
    }
}