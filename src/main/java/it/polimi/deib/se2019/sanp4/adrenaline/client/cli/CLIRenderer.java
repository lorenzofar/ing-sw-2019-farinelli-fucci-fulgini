package it.polimi.deib.se2019.sanp4.adrenaline.client.cli;

import it.polimi.deib.se2019.sanp4.adrenaline.client.ClientView;
import it.polimi.deib.se2019.sanp4.adrenaline.client.UIRenderer;
import it.polimi.deib.se2019.sanp4.adrenaline.common.events.ChoiceResponse;
import it.polimi.deib.se2019.sanp4.adrenaline.common.exceptions.LoginException;
import it.polimi.deib.se2019.sanp4.adrenaline.common.requests.*;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.weapons.WeaponCard;
import it.polimi.deib.se2019.sanp4.adrenaline.view.MessageType;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CLIRenderer implements UIRenderer {

    private static final String LOBBY_MATCH_NOTSTARTING = "The match is about to start, wait for other players to join";
    private static final String LOBBY_MATCH_STARTING = "The match is starting soon with these players";

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
        if (selectedNetworkMode.equals("socket")) {
            clientView.setSocketConnection();
        } else {
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
    private void setUpNetworkConnection() {
        boolean connected = false;
        while (!connected) {
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
    private void performLogin() {
        CLIHelper.printTitle("login");
        String username = CLIHelper.parseString("Enter your username");
        boolean loggedIn = false;
        while (!loggedIn) {
            try {
                clientView.getServerConnection().login(username);
                clientView.setUsername(username);
                loggedIn = true;
            } catch (IOException e) {
                CLIHelper.printError("An error occurred while logging in");
            } catch (LoginException e) {
                CLIHelper.printError("The username is not available");
                loggedIn = false;
                username = CLIHelper.parseString("Enter your username");
            }
        }
    }

    /**
     * Prints the lobby screen, listing the provided connected players
     *
     * @param connectedPlayers The list of connected players
     */
    private void printLobbyScreen(Collection<String> connectedPlayers, boolean matchStarting) {
        CLIHelper.clearScreen();
        CLIHelper.printTitle("waiting room");
        CLIHelper.println(matchStarting ? LOBBY_MATCH_STARTING : LOBBY_MATCH_NOTSTARTING);
        CLIHelper.printlnColored("Connected players:", CLIHelper.ANSI_YELLOW);
        connectedPlayers.forEach(player -> CLIHelper.println("* %s", player));
        CLIHelper.startSpinner();
    }

    @Override
    public void showLobby() {
        printLobbyScreen(Collections.emptyList(), false);
    }

    @Override
    public void updateLobby(Collection<String> connectedPlayers, boolean matchStarting) {
        printLobbyScreen(connectedPlayers, matchStarting);
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

    @Override
    public void handle(ActionRequest request) {
        //TODO: Implement this method
    }

    @Override
    public void handle(BoardRequest request) {
        CLIHelper.printTitle("Board configuration");
        // Ask for user input
        Integer selectedCount = CLIHelper.askOptionFromList(
                "Please select the board that will be used for the match",
                request.getChoices(),
                false);
        // Then create a new response and reply to the server
        ChoiceResponse<Integer> response = new ChoiceResponse<>(clientView.getUsername(), request.getUuid(), selectedCount);
        clientView.notifyObservers(response);
        clientView.onRequestCompleted();
        //TODO: Check how to better render board information
    }

    @Override
    public void handle(PlayerOperationRequest request) {
        //TODO: Implement this method
    }

    @Override
    public void handle(PlayerRequest request) {
        //TODO: Implement this method
    }

    @Override
    public void handle(PowerupCardRequest request) {
        //TODO: Implement this method
    }

    @Override
    public void handle(SkullCountRequest request) {
        CLIHelper.printTitle("Skulls count");
        // Ask for user input
        Integer selectedCount = CLIHelper.askOptionFromList(
                request.getMessage(),
                request.getChoices(),
                false);
        // Then create a new response and reply to the server
        ChoiceResponse<Integer> response = new ChoiceResponse<>(clientView.getUsername(), request.getUuid(), selectedCount);
        clientView.notifyObservers(response);
        clientView.onRequestCompleted();
    }

    @Override
    public void handle(SquareRequest request) {
        //TODO: Implement this method
    }

    @Override
    public void handle(WeaponCardRequest request) {
        // First reneder all the weapon cards we can choose among
        List<List<List<String>>> renderedWeaponCards = request.getChoices().stream().map(CLIHelper::renderWeaponCard).collect(Collectors.toList());
        // Then show them to the user by concatenating them
        CLIHelper.printRenderedGameElement(CLIHelper.concatRenderedElements(renderedWeaponCards, 1));
        // Then print the selection screen
        // If the request is optional, we allow for empty selection
        WeaponCard selectedWeaponCard = CLIHelper.askOptionFromList(
                request.getMessage(),
                request.getChoices(),
                request.isOptional(),
                WeaponCard::getName
        );
        // Then create a response accordingly and reply to server
        ChoiceResponse<WeaponCard> response = new ChoiceResponse<>(clientView.getUsername(), request.getUuid(), selectedWeaponCard);
        clientView.notifyObservers(response);
        clientView.onRequestCompleted();
    }
}