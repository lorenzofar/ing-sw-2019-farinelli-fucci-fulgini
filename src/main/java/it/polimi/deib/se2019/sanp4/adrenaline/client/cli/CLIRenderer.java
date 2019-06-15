package it.polimi.deib.se2019.sanp4.adrenaline.client.cli;

import it.polimi.deib.se2019.sanp4.adrenaline.client.ClientView;
import it.polimi.deib.se2019.sanp4.adrenaline.client.ModelManager;
import it.polimi.deib.se2019.sanp4.adrenaline.client.UIRenderer;
import it.polimi.deib.se2019.sanp4.adrenaline.common.events.ChoiceResponse;
import it.polimi.deib.se2019.sanp4.adrenaline.common.exceptions.LoginException;
import it.polimi.deib.se2019.sanp4.adrenaline.common.modelviews.PlayerView;
import it.polimi.deib.se2019.sanp4.adrenaline.common.requests.*;
import it.polimi.deib.se2019.sanp4.adrenaline.model.board.BoardCreator;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.powerup.PowerupCard;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.weapons.WeaponCard;
import it.polimi.deib.se2019.sanp4.adrenaline.view.MessageType;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CLIRenderer implements UIRenderer {

    private static final String LOBBY_MATCH_NOTSTARTING = "The match is about to start, wait for other players to join";
    private static final String LOBBY_MATCH_STARTING = "The match is starting soon with these players";

    private ClientView clientView;

    private CommandsParser commandsParser;
    private ExecutorService commandsParserExecutor;

    /* ASCII-art version of the game title */
    private static final String ADRENALINE_TITLE =
            "    ___    ____  ____  _______   _____    __    _____   ________\n" +
                    "   /   |  / __ \\/ __ \\/ ____/ | / /   |  / /   /  _/ | / / ____/\n" +
                    "  / /| | / / / / /_/ / __/ /  |/ / /| | / /    / //  |/ / __/\n" +
                    " / ___ |/ /_/ / _, _/ /___/ /|  / ___ |/ /____/ // /|  / /_\n" +
                    "/_/  |_/_____/_/ |_/_____/_/ |_/_/  |_/_____/___/_/ |_/_____/ \n";


    @Override
    public void initialize() {

        // Create the executor for the commands parser
        commandsParserExecutor = Executors.newSingleThreadExecutor();
        commandsParser = new CommandsParser(this);

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
     * Start the command parser and submit to executor if it's not alive
     */
    private void startCommandsParser() {
        if (!commandsParser.isAlive()) {
            commandsParser.setAlive(true);
            commandsParserExecutor.submit(commandsParser);
        }
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
        // If the commands parser does not exist, we initialize it
        startCommandsParser();
        CLIHelper.stopSpinner();
        CLIHelper.clearScreen();

        /*  The match screen is divided into two main panes,
            that correspond to two different contexts
            - LEFT PANE : game information
            - RIGHT PANE : players information

            In the left pane there will be:
            - TOP ROW showing killshots track and spawn squares
            - MIDDLE ROW showing the game board
            - BOTTOM ROW showing the player board of the user

            In the right pane there will be:
            - turn information
            - players table
            - stacked players board
        */

        ModelManager modelManager = clientView.getModelManager();
        // We first render the killshots track
        List<List<String>> renderedKillshotsTrack = CLIHelper.renderKillshotsTrack(
                modelManager.getMatch() != null ? modelManager.getMatch().getKillshotsCount() : 0,
                modelManager.getMatch() != null ? modelManager.getMatch().getTotalSkulls() : 0);
        // Then the table showing information about spawn points
        List<List<String>> renderedSpawnTable = CLIHelper.renderSpawnWeaponsTable(modelManager.getBoard());
        // Then we build the first row
        List<List<String>> leftTopRow = CLIHelper.concatRenderedElements(
                Arrays.asList(renderedKillshotsTrack, renderedSpawnTable),
                4);
        // We then render the board
        List<List<String>> renderedBoard = CLIHelper.renderBoard(
                modelManager.getBoard(),
                modelManager.getPlayersColors());
        // Then the player board of the user
        List<List<String>> renderedUserPlayerBoard = CLIHelper.renderPlayerBoard(
                modelManager.getPlayerBoards().get(clientView.getUsername()),
                clientView.getUsername(),
                modelManager.getPlayersColors()
        );
        // And eventually build the left pane
        List<List<String>> leftPane = CLIHelper.stackRenderedElements(
                Arrays.asList(leftTopRow, renderedBoard, renderedUserPlayerBoard),
                2
        );

        // Then we consider the right pane
        // We first render the players overview
        List<List<String>> renderedPlayersList = CLIHelper.renderPlayersOverview(
                modelManager.getPlayers().entrySet().stream().collect(Collectors.toMap(
                        Map.Entry::getKey, e -> e.getValue().getColor()
                ))
        );
        // Then we generate all the player boards, removing the one of the current player
        List<List<List<String>>> renderedPlayerBoards = modelManager.getPlayerBoards().entrySet().stream()
                .filter(e -> !e.getKey().equals(clientView.getUsername()))
                .map(e -> CLIHelper.renderPlayerBoard(e.getValue(), e.getKey(), modelManager.getPlayersColors()))
                .collect(Collectors.toList());

        //TODO: Generate turn rendering and show information about frenzy mode
        //TODO: Show score of the user
        //TODO: Show ammo of the user

        // Then we stack each board on top of the other
        List<List<String>> stackedPlayerBoards = CLIHelper.stackRenderedElements(renderedPlayerBoards, 1);
        List<List<String>> rightPane = CLIHelper.stackRenderedElements(
                Arrays.asList(renderedPlayersList, stackedPlayerBoards),
                2
        );

        // Then we place the two panes one after the other
        List<List<String>> matchScreen = CLIHelper.concatRenderedElements(Arrays.asList(leftPane, rightPane), 2);

        // And finally print the match screen
        CLIHelper.printRenderedGameElement(matchScreen);

    }

    /**
     * Shows the weapons held by the user
     */
    public void showUserWeapons() {
        PlayerView user = clientView.getModelManager().getPlayers().getOrDefault(clientView.getUsername(), null);
        if (user == null) {
            // This should never happen, since the user is a player
            return;
        }
        List<List<List<String>>> userWeapons = user.getWeapons().stream().map(CLIHelper::renderWeaponCard).collect(Collectors.toList());
        CLIHelper.printFullScreenRenderedGameElement(CLIHelper.concatRenderedElements(userWeapons, 2), "Weapons");
    }

    public void showUserPowerups(){
        PlayerView user = clientView.getModelManager().getPlayers().getOrDefault(clientView.getUsername(), null);
        if (user == null) {
            // This should never happen, since the user is a player
            return;
        }
        List<List<List<String>>> userPowerups = user.getPowerups().stream().map(CLIHelper::renderPowerupCard).collect(Collectors.toList());
        CLIHelper.printFullScreenRenderedGameElement(CLIHelper.concatRenderedElements(userPowerups, 2), "Powerups");
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
    public void cancelSelection() {
        CLIHelper.cancelInput();
    }

    @Override
    public void handle(ActionRequest request) {
        requestRoutine("Action selection", request, actionEnum -> String.format("%s : %s", actionEnum.name(), actionEnum.toString()));
    }

    @Override
    public void handle(BoardRequest request) {
        requestRoutine("Board configuration", request, BoardCreator::getBoardDescription);
    }

    @Override
    public void handle(PlayerOperationRequest request) {
        requestRoutine("Operation selection", request); // Operations enum already returns the message when converted to string
    }

    @Override
    public void handle(PlayerRequest request) {
        requestRoutine("Player selection", request); // Players are already string, we do not need additional formatting
    }

    @Override
    public void handle(PowerupCardRequest request) {
        // First render all the powerup cards we can choose among
        List<List<List<String>>> renderedPowerupCards = request.getChoices().stream().map(CLIHelper::renderPowerupCard).collect(Collectors.toList());
        // Then show them to the user by concatenating them
        CLIHelper.printRenderedGameElement(CLIHelper.concatRenderedElements(renderedPowerupCards, 1));
        requestRoutine("Powerup selection", request, PowerupCard::getName);
    }

    @Override
    public void handle(SkullCountRequest request) {
        requestRoutine("Skulls configuration", request);
    }

    @Override
    public void handle(SquareRequest request) {
        // First make sure the board is shown to the user, hence refresh the match screen
        showMatchScreen();
        requestRoutine("Square selection", request, coordPair -> String.format("(%d:%d)", coordPair.getX(), coordPair.getY()));
    }

    @Override
    public void handle(WeaponCardRequest request) {
        // First render all the weapon cards we can choose among
        List<List<List<String>>> renderedWeaponCards = request.getChoices().stream().map(CLIHelper::renderWeaponCard).collect(Collectors.toList());
        // Then show them to the user by concatenating them
        CLIHelper.printRenderedGameElement(CLIHelper.concatRenderedElements(renderedWeaponCards, 1));
        requestRoutine("Weapon card selection", request, WeaponCard::getName);
    }

    /**
     * Performs the provided request on the user, replying to the server with the selected object
     *
     * @param request         The oject representing the request
     * @param stringConverter The conversion function to print the available choices
     * @param <T>             The type of the choices
     */
    private <T extends Serializable> void requestRoutine(String title, ChoiceRequest<T> request, Function<T, String> stringConverter) {
        CLIHelper.cancelInput();
        CLIHelper.printTitle(title);
        commandsParser.setAlive(false);
        // Ask the user to select a choice
        T selectedObject = CLIHelper.askOptionFromList(
                request.getMessage(),
                request.getChoices(),
                request.isOptional(),
                stringConverter
        );
        if ((selectedObject == null && !request.isOptional()) || clientView.getCurrentRequest() == null) {
            // The request has been cancelled, hence we stop here
            // We also check whether the current request has been deleted, in order to avoid issues when the
            // request allows an optional choice
            startCommandsParser();
            return;
        }
        // Then create a response accordingly and reply to server
        ChoiceResponse<T> response = new ChoiceResponse<>(clientView.getUsername(), request.getUuid(), selectedObject);
        startCommandsParser();
        clientView.notifyObservers(response);
        clientView.onRequestCompleted();
    }

    /**
     * Performs the provided request on the user, replying to the server with the selected object
     *
     * @param request The oject representing the request
     * @param <T>     The type of the choices
     */
    private <T extends Serializable> void requestRoutine(String title, ChoiceRequest<T> request) {
        requestRoutine(title, request, Object::toString);
    }
}