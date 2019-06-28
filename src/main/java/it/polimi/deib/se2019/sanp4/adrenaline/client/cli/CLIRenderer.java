package it.polimi.deib.se2019.sanp4.adrenaline.client.cli;

import it.polimi.deib.se2019.sanp4.adrenaline.client.ClientView;
import it.polimi.deib.se2019.sanp4.adrenaline.client.ModelManager;
import it.polimi.deib.se2019.sanp4.adrenaline.client.UIRenderer;
import it.polimi.deib.se2019.sanp4.adrenaline.common.events.ChoiceResponse;
import it.polimi.deib.se2019.sanp4.adrenaline.common.exceptions.LoginException;
import it.polimi.deib.se2019.sanp4.adrenaline.common.requests.*;
import it.polimi.deib.se2019.sanp4.adrenaline.model.board.BoardCreator;
import it.polimi.deib.se2019.sanp4.adrenaline.model.board.CoordPair;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.powerup.PowerupCard;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.weapons.EffectDescription;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.weapons.WeaponCard;
import it.polimi.deib.se2019.sanp4.adrenaline.model.match.Leaderboard;
import it.polimi.deib.se2019.sanp4.adrenaline.view.MessageType;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;
import java.util.function.Function;
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
        // If a request is currently being handled, do not print the match screen
        if (clientView.getCurrentRequest() != null) {
            return;
        }
        CLIHelper.stopSpinner();
        CLIHelper.clearScreen();
        printMatchScreen();
    }

    @Override
    public void showDrawnWeapon(WeaponCard weapon) {
        // We refresh the match screen since the list of owned weapon cards is shown there
        printMatchScreen();
    }

    @Override
    public void showDrawnPowerup(PowerupCard powerup) {
        // We refresh the match screen since the list of owned powerup cards is shown there
        printMatchScreen();
    }

    /**
     * Generates and prints a textual representation of the game dashboard
     */
    private void printMatchScreen() {

        // If there is a request pending, we do not print the match screen
        if (clientView.getCurrentRequest() != null) {
            return;
        }

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
                modelManager.getMatch() != null ?
                        modelManager.getMatch()
                                .getKillshotsTrack()
                                .stream()
                                .map(shooter -> clientView.getModelManager().getPlayersColors().get(shooter))
                                .collect(Collectors.toList()) : Collections.emptyList(),
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
        // We then render the match overview
        List<List<String>> renderedPlayersList = CLIHelper.renderMatchOverview(
                modelManager.getPlayers().entrySet().stream().collect(Collectors.toMap(
                        Map.Entry::getKey, e -> e.getValue().getColor()
                )),
                modelManager.getMatch().isFrenzy(),
                modelManager.getCurrentTurn(),
                modelManager.getPlayers().get(clientView.getUsername()).getScore()
        );
        // Then we generate all the player boards, removing the one of the current player
        List<List<List<String>>> renderedPlayerBoards = modelManager.getPlayerBoards().entrySet().stream()
                .filter(e -> !e.getKey().equals(clientView.getUsername()))
                .map(e -> CLIHelper.renderPlayerBoard(e.getValue(), e.getKey(), modelManager.getPlayersColors()))
                .collect(Collectors.toList());
        // Then we stack each board on top of the other
        List<List<String>> stackedPlayerBoards = CLIHelper.stackRenderedElements(renderedPlayerBoards, 1);
        // Then the table showing the owned weapons
        List<List<String>> renderedWeaponsTable = CLIHelper.renderWeaponsTable(
                modelManager.getPlayers().entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().getWeapons())));
        // And concatenate elements in the middle row
        List<List<String>> leftMiddleRow = CLIHelper.concatRenderedElements(
                Arrays.asList(renderedBoard, renderedPlayersList, stackedPlayerBoards, renderedWeaponsTable),
                4
        );

        // Then the player board of the user
        List<List<String>> renderedUserPlayerBoard = CLIHelper.renderPlayerBoard(
                modelManager.getPlayerBoards().get(clientView.getUsername()),
                clientView.getUsername(),
                modelManager.getPlayersColors()
        );
        // Then the table showing the owned ammo
        List<List<String>> renderedAmmoTable = CLIHelper.renderAmmoOverview(modelManager.getPlayers().get(clientView.getUsername()).getAmmo());

        // Then the table showing the owned powerups
        List<List<String>> renderedPowerupsTable = CLIHelper.renderPowerupsTable(modelManager.getPlayers().get(clientView.getUsername()).getPowerups());
        List<List<String>> leftBottomRow = CLIHelper.concatRenderedElements(
                Arrays.asList(renderedUserPlayerBoard, renderedAmmoTable, renderedPowerupsTable),
                2
        );
        // And eventually build the left pane
        List<List<String>> leftPane = CLIHelper.stackRenderedElements(
                Arrays.asList(leftTopRow, leftMiddleRow, leftBottomRow),
                2
        );

        // And finally print the match screen
        CLIHelper.printRenderedGameElement(leftPane);
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
    public void showPreemptionScreen(String title, String message) {
        CLIHelper.clearScreen();
        CLIHelper.printTitle(title);
        CLIHelper.printlnColored(message, CLIHelper.ANSI_RED);
        CLIHelper.println("Press enter key to close the game...");
        CLIHelper.parseString();
        System.exit(0);
    }

    @Override
    public void showRejoinScreen() {
        CLIHelper.printTitle("Rejoining the match");
        CLIHelper.println("You have been reconnected to your previous match");
        CLIHelper.println("You will rejoin the match at the end of the current turn...");
    }

    @Override
    public void showLeaderBoard() {
        Leaderboard leaderboard = clientView.getModelManager().getLeaderboard();
        if (leaderboard != null) {
            CLIHelper.clearScreen();
            CLIHelper.printFullScreenRenderedGameElement(
                    CLIHelper.renderLeaderBoard(leaderboard),
                    "Final scores");
        }
    }

    @Override
    public void updateLeaderBoard(Leaderboard leaderboard) {
        CLIHelper.clearScreen();
        CLIHelper.printFullScreenRenderedGameElement(CLIHelper.renderLeaderBoard(leaderboard), "Final scores");
    }

    @Override
    public void setIdleScreen() {
        // We cancel pending input requests
        CLIHelper.cancelInput();
    }

    /**
     * Updates the screen when the player is playing the current turn
     */
    @Override
    public void setActiveScreen() {
        // We cancel pending input requests
        CLIHelper.cancelInput();
    }

    /**
     * Refreshes the rendered killshots track
     */
    @Override
    public void refreshKillshotsTrack() {
        printMatchScreen();
    }

    /**
     * Refreshes a rendered player board
     *
     * @param boardOwner The player owning the board to refresh
     */
    @Override
    public void refreshPlayerBoard(String boardOwner) {
        printMatchScreen();
    }

    /**
     * Refreshes the whole rendered game board
     */
    @Override
    public void refreshGameBoard() {
        printMatchScreen();
    }

    /**
     * Refreshes the rendered game board by updating the provided squares
     *
     * @param squares The collection of squares to refresh
     */
    @Override
    public void refreshGameBoard(CoordPair... squares) {
        printMatchScreen();
    }

    @Override
    public void refreshAmmoInfo() {
        printMatchScreen();
    }

    /**
     * Refreshes the rendered information about the match
     */
    @Override
    public void refreshMatchInfo() {
        printMatchScreen();
    }

    @Override
    public void refreshSpawnWeapons() {
        printMatchScreen();
    }

    @Override
    public void refreshOwnedWeapons() {
        printMatchScreen();
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
        requestRoutine("Square selection", request,
                coordPair -> {
                    // We show information about:
                    // * how many players are there
                    // * the type of square
                    // * what is contained in the square
                    // First get the current count of players inside the square
                    int playersCount = clientView.getModelManager().getBoard().getSquare(coordPair).getPlayers().size();
                    String squareContent = clientView.getModelManager().getBoard().getSquare(coordPair).printSquareContent();
                    String template = "(%d:%d) - %s - players: %d - content: %s";
                    return String.format(template,
                            coordPair.getX(),
                            coordPair.getY(),
                            clientView.getModelManager().getBoard().getSquare(coordPair).printTypeMarker(),
                            playersCount,
                            squareContent);
                }
        );
    }

    @Override
    public void handle(WeaponCardRequest request) {
        // First render all the weapon cards we can choose among
        List<List<List<String>>> renderedWeaponCards = request.getChoices().stream().map(CLIHelper::renderWeaponCard).collect(Collectors.toList());
        // Then show them to the user by concatenating them
        CLIHelper.printRenderedGameElement(CLIHelper.concatRenderedElements(renderedWeaponCards, 1));
        requestRoutine("Weapon card selection", request, WeaponCard::getName);
    }

    @Override
    public void handle(EffectRequest request) {
        List<List<List<String>>> renderedEffects = request.getChoices().stream().map(effect -> CLIHelper.renderEffectDescription(effect, true)).collect(Collectors.toList());
        CLIHelper.printRenderedGameElement(CLIHelper.concatRenderedElements(renderedEffects, 1));
        requestRoutine("Effect selection", request, EffectDescription::getName);
    }

    /**
     * Performs the provided request on the user, replying to the server with the selected object
     *
     * @param title           The title of the screen shown to the user
     * @param request         The object representing the request
     * @param stringConverter The conversion function to print the available choices
     * @param <T>             The type of the choices
     */
    private <T extends Serializable> void requestRoutine(String title, ChoiceRequest<T> request, Function<T, String> stringConverter) {
        CLIHelper.cancelInput();
        CLIHelper.stopSpinner();
        CLIHelper.printTitle(title);
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
            return;
        }
        // Then create a response accordingly and reply to server
        ChoiceResponse<T> response = new ChoiceResponse<>(clientView.getUsername(), request.getUuid(), selectedObject);
        clientView.notifyObservers(response);
        clientView.onRequestCompleted();
        showMatchScreen();
    }

    /**
     * Performs the provided request on the user, replying to the server with the selected object
     *
     * @param request The object representing the request
     * @param <T>     The type of the choices
     */
    private <T extends Serializable> void requestRoutine(String title, ChoiceRequest<T> request) {
        requestRoutine(title, request, Object::toString);
    }
}