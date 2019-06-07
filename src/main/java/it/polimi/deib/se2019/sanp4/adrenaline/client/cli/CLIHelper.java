package it.polimi.deib.se2019.sanp4.adrenaline.client.cli;

import it.polimi.deib.se2019.sanp4.adrenaline.common.ColoredObject;
import it.polimi.deib.se2019.sanp4.adrenaline.common.modelviews.BoardView;
import it.polimi.deib.se2019.sanp4.adrenaline.common.modelviews.PlayerBoardView;
import it.polimi.deib.se2019.sanp4.adrenaline.common.modelviews.SpawnSquareView;
import it.polimi.deib.se2019.sanp4.adrenaline.common.modelviews.SquareView;
import it.polimi.deib.se2019.sanp4.adrenaline.model.board.CardinalDirection;
import it.polimi.deib.se2019.sanp4.adrenaline.model.board.CoordPair;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.ammo.AmmoCubeCost;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.powerup.PowerupCard;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.weapons.WeaponCard;
import it.polimi.deib.se2019.sanp4.adrenaline.model.player.PlayerColor;

import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.CancellationException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * An utility class to interact with the command-line.
 * It provides to read and write from the console
 */
class CLIHelper {

    /**
     * Private constructor to hide the public one
     */
    private CLIHelper() {
    }

    /**
     * Timer to print animations and schedule periodic printing
     */
    private static Timer timer;
    /**
     * Scanner to read user input
     */
    private static final CancellableInput input = new CancellableInput(new InputStreamReader(System.in));
    /**
     * Stack of characters used for the spinner animation
     */
    private static final Queue<Character> spinnerStack = new LinkedList<>(Arrays.asList('⠋', '⠙', '⠹', '⠸', '⠼', '⠴', '⠦', '⠧', '⠇', '⠏'));

    /* ===== ANSI COLORS ===== */
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";
    public static final String ANSI_BLACK_BACKGROUND = "\u001B[40m";
    public static final String ANSI_RED_BACKGROUND = "\u001B[41m";
    public static final String ANSI_GREEN_BACKGROUND = "\u001B[42m";
    public static final String ANSI_YELLOW_BACKGROUND = "\u001B[43m";
    public static final String ANSI_BLUE_BACKGROUND = "\u001B[44m";
    public static final String ANSI_PURPLE_BACKGROUND = "\u001B[45m";
    public static final String ANSI_CYAN_BACKGROUND = "\u001B[46m";
    public static final String ANSI_WHITE_BACKGROUND = "\u001B[47m";

    /* ===== ANSI FORMATTERS ===== */
    private static final String ANSI_BOLD = "\033[0;1m";
    private static final String ANSI_ITALIC = "\u001B[3m";

    /* ===== BORDER AND CORNERS ===== */
    private static final String LEFT_TOP_CORNER = "╔";
    private static final String RIGHT_TOP_CORNER = "╗";
    private static final String LEFT_BOTTOM_CORNER = "╚";
    private static final String RIGHT_BOTTOM_CORNER = "╝";
    private static final String LIGHT_LEFT_TOP_CORNER = "┏";
    private static final String LIGHT_RIGHT_TOP_CORNER = "┓";
    private static final String LIGHT_LEFT_BOTTOM_CORNER = "┗";
    private static final String LIGHT_RIGHT_BOTTOM_CORNER = "┛";
    private static final String VERTICAL_BORDER = "║";
    private static final String HORIZONTAL_BORDER = "═";
    private static final String LEFT_VERTICAL_SEPARATOR = "╠";
    private static final String RIGHT_VERTICAL_SEPARATOR = "╣";
    private static final String LIGHT_LEFT_VERTICAL_SEPARATOR = "╟";
    private static final String LIGHT_RIGHT_VERTICAL_SEPARATOR = "╢";
    private static final String LIGHT_HORIZONTAL_BORDER = "─";
    private static final String VERTICAL_WALL = "┃";
    private static final String HORIZONTAL_WALL = "━";
    private static final String BLANK = " ";

    /* ===== SYMBOLS ===== */
    private static final String ANSI_DOT = "●";
    private static final String ANSI_SKULL = "☠";
    private static final String ANSI_MARK = "⊚";

    /* ===== DIMENSIONS ===== */
    private static final int SQUARE_DIM = 15;
    private static final int CARD_WIDTH = 20;
    private static final int FULLSCREEN_WIDTH = 119;
    private static final int FULLSCREEN_TITLE_WIDTH = 40;
    private static final int SPAWN_WEAPONS_CELL_DIM = 40;
    private static final int PLAYERS_OVERVIEW_DIM = 25;

    /* ===== TEMPLATES ====== */
    private static final String TRISTRING_TEMPLATE = "%s%s%s";
    private static final String QUADSTRING_TEMPLATE = "%s%s%s%s";
    private static final String PROMPT_TEMPLATE = ">> ";

    /**
     * Prints the provided text, formatting it with the provided parameters
     *
     * @param template The template of the text to print
     * @param args     Optional arguments to insert inside the template
     */
    static void print(String template, Object... args) {
        System.out.printf(template, args);
    }

    /**
     * Prints the provided text with the provided color, formatting it with the provided parameters
     *
     * @param template The template of the text to print
     * @param color    The ANSI code of the color
     * @param args     Optional arguments to insert inside the template
     */
    static void printColored(String template, String color, Object... args) {
        print(color + template + ANSI_RESET, args);
    }

    /**
     * Prints the provided text, formatting it with the provided parameters
     * and adding a new line after it
     *
     * @param template The template of the text to print
     * @param args     Optional arguments to insert inside the template
     */
    static void println(String template, Object... args) {
        print(template, args);
        print("\n");
    }

    /**
     * Prints the provided text with the provided color, formatting it with the provided parameters
     * and adding a new line after it
     *
     * @param template The template of the text to print
     * @param color    The ANSI code of the color
     * @param args     Optional arguments to insert inside the template
     */
    static void printlnColored(String template, String color, Object... args) {
        print(color + template + ANSI_RESET, args);
        print("\n");
    }

    /**
     * Print a section title highlighting its text
     *
     * @param title The title to print
     */
    static void printTitle(String title) {
        StringBuilder topBottomBorder = new StringBuilder();
        for (int i = 0; i < title.length() + 16; i++) {
            topBottomBorder.append(HORIZONTAL_BORDER);
        }
        print(ANSI_GREEN);
        println(TRISTRING_TEMPLATE, LEFT_TOP_CORNER, topBottomBorder.toString(), RIGHT_TOP_CORNER);
        println("%s%8c%s%8c%s", VERTICAL_BORDER, ' ', title.toUpperCase(), ' ', VERTICAL_BORDER);
        println(TRISTRING_TEMPLATE, LEFT_BOTTOM_CORNER, topBottomBorder.toString(), RIGHT_BOTTOM_CORNER);
        resetColor();
    }

    /**
     * Prints the provided game element to the command line
     * If a null object is provided, nothing is printed
     *
     * @param gameElement The textual representation of the element
     */
    public static void printRenderedGameElement(List<List<String>> gameElement) {
        if (gameElement != null) {
            gameElement.forEach(line -> println(String.join("", line)));
        }
    }

    /**
     * Prints the provided game element to the command line in a full-screen mode
     * If a null object is provided, nothing is printed
     *
     * @param gameElement The textual representation of the element
     * @param title       The title of the screen
     * @param description The description of the screen
     */
    private static void printFullScreenRenderedGameElement(List<List<String>> gameElement, String title, String description) {
        if (gameElement == null || gameElement.isEmpty()) {
            return;
        }

        List<List<String>> output = new ArrayList<>(gameElement);
        int contentLength = output.get(0).size();
        AtomicInteger currentLine = new AtomicInteger(0);

        if (!title.equals("")) {
            List<String> titleChunks = splitString(title, FULLSCREEN_TITLE_WIDTH);
            titleChunks.forEach(titleChunk -> {
                output.add(currentLine.get(), generateLine(BLANK, FULLSCREEN_WIDTH, BLANK, BLANK));
                int i = 0;
                while (i < (FULLSCREEN_WIDTH - FULLSCREEN_TITLE_WIDTH - 2) / 2) {
                    output.get(currentLine.get()).set(i, HORIZONTAL_BORDER);
                    output.get(currentLine.get()).set(FULLSCREEN_WIDTH - 1 - i, HORIZONTAL_BORDER);
                    i++;
                }
                // Make the title bold
                output.get(currentLine.get()).add(0, ANSI_BOLD);
                // i marks the start of the empty space for the title
                // compute the horizontal center
                int center = i + (FULLSCREEN_TITLE_WIDTH / 2);
                int textStart = center - titleChunk.length() / 2;
                for (int l = 0; l < titleChunk.length(); l++) {
                    output.get(currentLine.get()).set(textStart + l, titleChunk.substring(l, l + 1));
                }
                currentLine.getAndIncrement();
            });
            output.add(currentLine.get(), generateLine(BLANK, FULLSCREEN_WIDTH, BLANK, BLANK));
            currentLine.getAndIncrement();
        }

        // Insert description
        if (!description.equals("")) {
            List<String> descriptionChunks = splitString(description, FULLSCREEN_WIDTH - 2);
            descriptionChunks.forEach(descriptionChunk -> {
                output.add(currentLine.get(), generateLine(BLANK, FULLSCREEN_WIDTH, BLANK, BLANK));
                fillLineWithText(output.get(currentLine.get()), descriptionChunk, 1);
                currentLine.getAndIncrement();
            });
            if (!descriptionChunks.isEmpty()) {
                output.add(currentLine.get(), generateLine(BLANK, FULLSCREEN_WIDTH, BLANK, BLANK));
                currentLine.getAndIncrement();
            }
        }

        // Add left and right padding
        for (int i = 0; i < (FULLSCREEN_WIDTH - contentLength) / 2; i++) {
            output.stream().skip(currentLine.get()).forEach(line -> {
                line.add(BLANK);
                line.add(0, BLANK);
            });
        }
        // Add bottom border, then clear the screen and print everything
        expandStringRendering(output, generateLine(BLANK, FULLSCREEN_WIDTH, BLANK, BLANK));
        clearScreen();
        printRenderedGameElement(output);
    }

    /**
     * Prints the provided game element to the command line in a full-screen mode
     * If a null object is provided, nothing is printed
     *
     * @param gameElement The textual representation of the element
     * @param title       The title of the screen
     */
    private static void printFullScreenRenderedGameElement(List<List<String>> gameElement, String title) {
        printFullScreenRenderedGameElement(gameElement, title, "");
    }

    /**
     * Prints the provided game element to the command line in a full-screen mode
     * If a null object is provided, nothing is printed
     *
     * @param gameElement The textual representation of the element
     */
    private static void printFullScreenRenderedGameElement(List<List<String>> gameElement) {
        printFullScreenRenderedGameElement(gameElement, "", "");
    }

    /**
     * Asks the user to choose among a list of options
     *
     * @param message         A message to show to the user
     * @param options         The list of objects representing the options
     * @param allowNull       Allows a null option to be chosen
     * @param stringConverter The function describing how to print each element
     * @return The object representing the selected option, could be {@code null} if allowNull is {@code true}
     */
    static <T> T askOptionFromList(String message, List<T> options, boolean allowNull, Function<T, String> stringConverter) {
        Integer n = null;

        print(ANSI_CYAN);
        println("%s", message);
        resetColor();

        options.forEach(option -> println("%d.\t%s", options.indexOf(option), stringConverter.apply(option)));
        if (allowNull) {
            print("%d. None", options.size());
        }

        int maxN = options.size() - (allowNull ? 0 : 1);

        do {
            if (n != null) print("This choice does not exist!");
            n = parseInt();
            if (n == null) {
                return null;
            }
        } while (n > maxN || n < 0);

        return n == options.size() ? null : options.get(n);
    }

    /**
     * Asks the user to choose among a list of options
     *
     * @param message         A message to show to the user
     * @param options         The list of objects representing the options
     * @param stringConverter The function describing how to print each element
     * @return The object representing the selected option
     */
    static <T> T askOptionFromList(String message, List<T> options, Function<T, String> stringConverter) {
        return askOptionFromList(message, options, false, stringConverter);
    }

    /**
     * Asks the user to choose among a list of options
     *
     * @param message   A message to show to the user
     * @param options   The list of objects representing the options
     * @param allowNull Allows a null option to be chosen
     * @return The object representing the selected option
     */
    static <T> T askOptionFromList(String message, List<T> options, boolean allowNull) {
        return askOptionFromList(message, options, allowNull, Object::toString);
    }

    /**
     * Asks the user to choose among a list of options
     *
     * @param message A message to show to the user
     * @param options The list of objects representing the options
     * @return The object representing the selected option
     */
    static <T> T askOptionFromList(String message, List<T> options) {
        return askOptionFromList(message, options, false, Object::toString);
    }

    /**
     * Clears the console screen by using ANSI escape codes
     */
    static void clearScreen() {
        stopSpinner();
        print("\033[H\033[2J");
        resetColor();
    }

    private static void resetColor() {
        print(ANSI_RESET);
    }

    /**
     * Cancels the current input request
     */
    public static void cancelInput() {
        input.cancel();
    }

    /**
     * Retrieves a string entered in the console
     * If the user enters an invalid input, it asks for it again until a valid one is provided
     *
     * @param message An optional message to show to the user
     * @return The string entered by the user
     */
    static String parseString(String message) {
        print(ANSI_CYAN);
        println(message);
        resetColor();
        print(PROMPT_TEMPLATE);
        return input.readLine();

    }

    /**
     * Retrieves an integer entered in the console
     * If the user enters an invalid input, it asks for it again until a valid one is provided
     *
     * @return The integer entered by the user
     */
    private static Integer parseInt() {
        print(PROMPT_TEMPLATE);
        try {
            return Integer.parseInt(input.readLine());
        } catch (CancellationException e) {
            return null;
        } catch (Exception e) {
            println("Please insert a valid input");
            return parseInt();
        }
    }

    /**
     * Retrieves an integer entered in the console
     * If the user enters an invalid input, it asks for it again until a valid one is provided
     *
     * @param message An message to show to the user
     * @return The integer entered by the user
     */
    private static Integer parseInt(String message) {
        print(ANSI_CYAN);
        println(message);
        resetColor();
        return parseInt();
    }

    /* ===== SPINNERS AND LOADING ===== */

    /**
     * Prints the current frame of the spinner
     * By using backspace character it updates the previous character to make it seem an animation
     */
    private static void printSpinner() {
        Character currentSpinnerSymbol = spinnerStack.remove();
        print("\b");
        print(currentSpinnerSymbol.toString());
        spinnerStack.add(currentSpinnerSymbol);
    }

    /**
     * Starts showing the spinner
     * The animation is updated every 100ms
     */
    static void startSpinner() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                CLIHelper.printSpinner();
            }
        }, 0, 100);
    }

    /**
     * Stops showing the spinner and adds a new line below it
     */
    static void stopSpinner() {
        if (timer != null) {
            timer.cancel();
            timer = null;
            println("");
        }
    }

    /* ================================ */

    /* ===== ERRORS ===== */
    static void printError(String errorMessage) {
        print(ANSI_RED);
        println(errorMessage);
        resetColor();
    }
    /* ================== */

    /* ===== STRINGS AND LISTS MANIPULATION ===== */

    /**
     * Generate a new line according to the provided template, with the provided left and right borders
     *
     * @param template    The string the line is composed of
     * @param length      The length of the line
     * @param leftBorder  The string representing the left border
     * @param rightBorder The string representing the right border
     */
    private static List<String> generateLine(String template, int length, String leftBorder, String rightBorder) {
        if (length == 0) {
            return Collections.emptyList();
        }
        List<String> line = new ArrayList<>(Collections.nCopies(length, template));
        line.set(0, leftBorder);
        line.set(line.size() - 1, rightBorder);
        return line;
    }

    /**
     * Generate a new line according to the provided template
     *
     * @param template The string the line is composed of
     * @param length   The length of the line
     */
    private static List<String> generateLine(String template, int length) {
        return generateLine(template, length, template, template);
    }

    /**
     * Expand the provided rendered list of strings with the provided new line
     *
     * @param renderedString The string rendering
     * @param newLine        The new line to be copied into the rendering
     */
    private static void expandStringRendering(List<List<String>> renderedString, List<String> newLine) {
        renderedString.add(new ArrayList<>());
        renderedString.get(renderedString.size() - 1).addAll(newLine);
    }

    /**
     * Insert the provided text in the provided line, by leaving an horizontal padding
     *
     * @param line      The line
     * @param text      The text to insert
     * @param hPadding  The padding amount
     * @param formatter The ANSI formatter to use
     * @param color     The ANSI color code to use
     */
    private static void fillLineWithText(List<String> line, String text, int hPadding, String formatter, String color) {
        for (int i = 0; i < text.length(); i++) {
            line.set(i + hPadding, String.format(QUADSTRING_TEMPLATE, formatter, color, text.substring(i, i + 1), ANSI_RESET));
        }
    }

    /**
     * Insert the provided text in the provided line, by leaving an horizontal padding
     *
     * @param line      The line
     * @param text      The text to insert
     * @param hPadding  The padding amount
     * @param formatter The ANSI formatter to use
     */
    private static void fillLineWithText(List<String> line, String text, int hPadding, String formatter) {
        fillLineWithText(line, text, hPadding, formatter, "");
    }

    /**
     * Insert the provided text in the provided line, by leaving an horizontal padding
     *
     * @param line     The line
     * @param text     The text to insert
     * @param hPadding The padding amount
     */
    private static void fillLineWithText(List<String> line, String text, int hPadding) {
        fillLineWithText(line, text, hPadding, "");
    }

    /**
     * Insert the provided objects in the provided line, by leaving an horizontal padding
     * and transforming the objects according to the provided functions
     *
     * @param line            The line
     * @param objects         The objects to put inside
     * @param colorConverter  The function mapping an object to a color
     * @param symbolConverter The function mapping an object to a string
     * @param hPadding        The padding amount
     * @param spacing         The spacing between two consecutive objects
     * @param <T>             the tipo of objects to insert
     */
    private static <T> void fillLineWithObjects(List<String> line, List<T> objects, Function<T, String> colorConverter, Function<T, String> symbolConverter, int hPadding, Integer spacing) {
        if (spacing == null) {
            spacing = 1;
        }
        int i = 0;
        for (T object : objects) {
            line.set(i + hPadding, String.format(TRISTRING_TEMPLATE, colorConverter.apply(object), symbolConverter.apply(object), ANSI_RESET));
            i += spacing;
        }
    }

    /**
     * Split the provided string into chunks to be contained within the specified width
     *
     * @param text     The string to split
     * @param maxWidth The maximum width
     * @return The list of chunks the string has been split into
     */
    private static List<String> splitString(String text, int maxWidth) {
        List<String> chunks = new ArrayList<>();
        int startIndex = 0;
        int endIndex;
        while (startIndex < text.length()) {
            endIndex = startIndex + maxWidth;
            endIndex = endIndex > text.length() ? text.length() : endIndex;
            chunks.add(text.substring(startIndex, endIndex));
            startIndex = endIndex;
        }
        return chunks;
    }

    /**
     * Split the provided list into chunks to be contained within the specified width
     *
     * @param list     The list to split
     * @param maxWidth The maximum width
     * @return The list of chunks the string has been split into
     */
    private static <T> List<List<T>> splitList(List<T> list, int maxWidth) {
        List<List<T>> chunks = new ArrayList<>();
        int startIndex = 0;
        int endIndex;
        while (startIndex < list.size()) {
            endIndex = startIndex + maxWidth;
            endIndex = endIndex > list.size() ? list.size() : endIndex;
            chunks.add(list.subList(startIndex, endIndex));
            startIndex = endIndex;
        }
        return chunks;
    }


    /**
     * Truncate the provided string to fit the provided width
     *
     * @param text     The string to truncate
     * @param maxWidth The maximum width to fits
     * @return The truncated string
     */
    private static String truncateString(String text, int maxWidth) {
        if (text.length() <= maxWidth) {
            return text;
        }
        return text.substring(0, maxWidth);
    }
    /* =================== */

    /* ===== SQUARES ===== */

    /**
     * Render the provided square to be printable in a CLI environment
     *
     * @param square The object representing the square
     * @return The list of all the rows the rendered square is composed of.
     * Each row is a list of string containing all the cells of the rendered square.
     */
    static List<List<String>> renderSquare(SquareView square, Map<String, ColoredObject> playersColors) {
        List<List<String>> renderedSquare = new ArrayList<>();
        if (square == null) {
            for (int i = 0; i < SQUARE_DIM / 2; i++) {
                renderedSquare.add(new ArrayList<>(Collections.nCopies(SQUARE_DIM, BLANK)));
            }
            return renderedSquare;
        }

        // We first initialize the main list
        expandStringRendering(renderedSquare, generateLine(
                square.getAdjacentMap().get(CardinalDirection.N) != null ? square.getAdjacentMap().get(CardinalDirection.N).getHorizontalCharacterRepresentation() : HORIZONTAL_WALL,
                SQUARE_DIM,
                LIGHT_LEFT_TOP_CORNER,
                LIGHT_RIGHT_TOP_CORNER
        ));

        for (int y = 0; y < SQUARE_DIM / 2 - 2; y++) {
            expandStringRendering(renderedSquare, generateLine(BLANK, SQUARE_DIM,
                    square.getAdjacentMap().get(CardinalDirection.W) != null ? square.getAdjacentMap().get(CardinalDirection.W).getVerticalCharacterRepresentation() : VERTICAL_WALL,
                    square.getAdjacentMap().get(CardinalDirection.E) != null ? square.getAdjacentMap().get(CardinalDirection.E).getVerticalCharacterRepresentation() : VERTICAL_WALL
            ));
        }

        expandStringRendering(renderedSquare, generateLine(
                square.getAdjacentMap().get(CardinalDirection.S) != null ? square.getAdjacentMap().get(CardinalDirection.S).getHorizontalCharacterRepresentation() : HORIZONTAL_WALL,
                SQUARE_DIM,
                LIGHT_LEFT_BOTTOM_CORNER,
                LIGHT_RIGHT_BOTTOM_CORNER
        ));

        // We add the marker indicating the square type
        renderedSquare.get(1).set(1, square.printTypeMarker());

        /* ===== PLAYERS RENDERING ===== */
        // We first calculate the center of the square
        int centerX = SQUARE_DIM / 2;
        int centerY = SQUARE_DIM / 4;
        // We retrieve the number of players inside the square
        int playersCount = square.getPlayers().size();
        // We compute the starting point of the line
        int playerX = centerX - playersCount / 2;
        for (String player : square.getPlayers()) {
            renderedSquare.get(centerY).set(playerX, String.format(TRISTRING_TEMPLATE, playersColors.get(player).getAnsiCode(), ANSI_DOT, square.getRoomColor().getAnsiCode()));
            playerX++;
        }
        // Then eventually put the correct ANSI code for the square color
        renderedSquare.forEach(row -> row.set(0, String.format("%s%s", square.getRoomColor().getAnsiCode(), row.get(0))));

        return renderedSquare;
    }

    /**
     * Print the provided board to the terminal
     *
     * @param board The object representing the board
     */
    public static List<List<String>> renderBoard(BoardView board, Map<String, ColoredObject> playersColors) {
        int width = board.getSquares().length;

        // We define an initial list of lists to hold the rendered board
        List<List<List<String>>> renderedRows = new ArrayList<>();

        // We then render each row and append it to the list
        for (int i = 0; i < board.getSquares()[0].length; i++) {
            List<List<List<String>>> renderedSquares = new ArrayList<>();
            for (int j = 0; j < width; j++) {
                renderedSquares.add(renderSquare(board.getSquares()[j][i], playersColors));
            }
            renderedRows.add(concatRenderedElements(renderedSquares, 0));
        }
        List<List<String>> renderedBoard = stackRenderedElements(renderedRows, 0);

        // Add coordinates indicators
        int row = 0;
        for (int i = 0; i < renderedBoard.size(); i++) {
            // Consider the index of the current row
            // Check whether is the center of a square
            if (i == row * (SQUARE_DIM / 2) + (SQUARE_DIM / 4)) {
                renderedBoard.get(i).add(0, String.format("%s%2d ", ANSI_RESET, row));
                row++;
            } else {
                renderedBoard.get(i).add(0, "   ");
            }
            renderedBoard.get(i).set(renderedBoard.get(i).size() - 1, String.format("%s%s", renderedBoard.get(i).get(renderedBoard.get(i).size() - 1), ANSI_RESET));
        }
        // Add a new line on top of the board
        renderedBoard.add(0, generateLine(BLANK, renderedBoard.get(0).size(), BLANK, BLANK));
        // Set the coordinates on the vertical centers of the square
        for (int col = 0; col < width; col++) {
            renderedBoard.get(0).set(3 + col * SQUARE_DIM + SQUARE_DIM / 2, String.format("%d", col));
        }
        return renderedBoard;
    }

    /* ===== WEAPON CARDS ===== */

    /**
     * Render the provided weapon card to be printable in a CLI environment
     *
     * @param weaponCard The object representing the weapon card
     * @return A list of all the lines composing the rendered card
     */
    public static List<List<String>> renderWeaponCard(WeaponCard weaponCard) {
        if (weaponCard == null) {
            //TODO: Check this scenario and what to return
            return Collections.emptyList();
        }

        List<List<String>> renderedWeapon = new ArrayList<>();

        // Set the top border
        expandStringRendering(renderedWeapon, generateLine(HORIZONTAL_BORDER, CARD_WIDTH, LEFT_TOP_CORNER, RIGHT_TOP_CORNER));

        // Split the name of the weapon (considering padding)
        List<String> weaponNameChunks = splitString(weaponCard.getName(), CARD_WIDTH - 4);
        // And add it to the rendered card
        weaponNameChunks.forEach(chunk -> {
            expandStringRendering(renderedWeapon, generateLine(BLANK, CARD_WIDTH, VERTICAL_BORDER, VERTICAL_BORDER));
            fillLineWithText(renderedWeapon.get(renderedWeapon.size() - 1), chunk, 2, ANSI_BOLD);
        });
        expandStringRendering(renderedWeapon, generateLine(HORIZONTAL_BORDER, CARD_WIDTH, LEFT_VERTICAL_SEPARATOR, RIGHT_VERTICAL_SEPARATOR));

        // Add indicators for ammo cubes
        // Each cube is represented by a colored dot, the first one is the one that comes pre-loaded with the weapon
        // First split the list of ammo cubes into chunks fitting the width, by considering a gao between each pair
        List<List<AmmoCubeCost>> ammoCubesChunks = splitList(weaponCard.getCost(), (CARD_WIDTH - 4) / 2);
        // And add them to the rendered card
        ammoCubesChunks.forEach(chunk -> {
            expandStringRendering(renderedWeapon, generateLine(BLANK, CARD_WIDTH, VERTICAL_BORDER, VERTICAL_BORDER));
            fillLineWithObjects(
                    renderedWeapon.get(renderedWeapon.size() - 1),
                    chunk,
                    AmmoCubeCost::getAnsiCode,
                    cube -> ANSI_DOT,
                    2,
                    2
            );
        });
        expandStringRendering(renderedWeapon, generateLine(HORIZONTAL_BORDER, CARD_WIDTH, LEFT_VERTICAL_SEPARATOR, RIGHT_VERTICAL_SEPARATOR));

        // Add description of effects
        weaponCard.getEffects().forEach(effect -> {
            // We add the name of the effect
            List<String> effectNameChunks = splitString(effect.getName(), CARD_WIDTH - 4);
            List<String> effectDescriptionChunks = splitString(effect.getDescription(), CARD_WIDTH - 4);
            List<List<AmmoCubeCost>> effectCostChunks = splitList(effect.getCost(), CARD_WIDTH - 4);

            effectNameChunks.forEach(effectNameChunk -> {
                expandStringRendering(renderedWeapon, generateLine(BLANK, CARD_WIDTH, VERTICAL_BORDER, VERTICAL_BORDER));
                fillLineWithText(renderedWeapon.get(renderedWeapon.size() - 1), effectNameChunk, 2);
            });
            expandStringRendering(renderedWeapon, generateLine(LIGHT_HORIZONTAL_BORDER, CARD_WIDTH, LIGHT_LEFT_VERTICAL_SEPARATOR, LIGHT_RIGHT_VERTICAL_SEPARATOR));
            effectDescriptionChunks.forEach(effectDescriptionChunk -> {
                expandStringRendering(renderedWeapon, generateLine(BLANK, CARD_WIDTH, VERTICAL_BORDER, VERTICAL_BORDER));
                fillLineWithText(renderedWeapon.get(renderedWeapon.size() - 1), effectDescriptionChunk, 2, ANSI_ITALIC);
            });
            effectCostChunks.forEach(effectCostChunk -> {
                expandStringRendering(renderedWeapon, generateLine(BLANK, CARD_WIDTH, VERTICAL_BORDER, VERTICAL_BORDER));
                fillLineWithObjects(
                        renderedWeapon.get(renderedWeapon.size() - 1),
                        effectCostChunk,
                        AmmoCubeCost::getAnsiCode,
                        cube -> ANSI_DOT,
                        2,
                        2
                );
            });
            expandStringRendering(renderedWeapon, generateLine(HORIZONTAL_BORDER, CARD_WIDTH, LEFT_VERTICAL_SEPARATOR, RIGHT_VERTICAL_SEPARATOR));
        });

        // Then remove the last inserted line, since is a middle separator
        renderedWeapon.remove(renderedWeapon.size() - 1);

        // Add bottom border
        expandStringRendering(renderedWeapon, generateLine(HORIZONTAL_BORDER, CARD_WIDTH, LEFT_BOTTOM_CORNER, RIGHT_BOTTOM_CORNER));

        return renderedWeapon;
    }

    /* ===== POWERUP CARDS ===== */

    /**
     * Render the provided powerup card to be printable in a CLI environment
     *
     * @param powerupCard The object representing the powerup card
     * @return A list of all the lines composing the rendered card
     */
    public static List<List<String>> renderPowerupCard(PowerupCard powerupCard) {
        if (powerupCard == null) {
            // TODO: Check this scenario
            return Collections.emptyList();
        }
        List<List<String>> renderedPowerup = new ArrayList<>();
        List<String> powerupNameChunks = splitString(powerupCard.getName(), CARD_WIDTH - 4);
        List<String> powerupDescriptionChunks = splitString(powerupCard.getDescription(), CARD_WIDTH - 4);
        expandStringRendering(renderedPowerup, generateLine(HORIZONTAL_BORDER, CARD_WIDTH, LEFT_TOP_CORNER, RIGHT_TOP_CORNER));
        powerupNameChunks.forEach(chunk -> {
            expandStringRendering(renderedPowerup, generateLine(BLANK, CARD_WIDTH, VERTICAL_BORDER, VERTICAL_BORDER));
            fillLineWithText(renderedPowerup.get(renderedPowerup.size() - 1), chunk, 2, ANSI_BOLD, powerupCard.getCubeColor().getAnsiCode());
        });
        expandStringRendering(renderedPowerup, generateLine(HORIZONTAL_BORDER, CARD_WIDTH, LEFT_VERTICAL_SEPARATOR, RIGHT_VERTICAL_SEPARATOR));
        powerupDescriptionChunks.forEach(chunk -> {
            expandStringRendering(renderedPowerup, generateLine(BLANK, CARD_WIDTH, VERTICAL_BORDER, VERTICAL_BORDER));
            fillLineWithText(renderedPowerup.get(renderedPowerup.size() - 1), chunk, 2, ANSI_ITALIC);
        });
        expandStringRendering(renderedPowerup, generateLine(HORIZONTAL_BORDER, CARD_WIDTH, LEFT_BOTTOM_CORNER, RIGHT_BOTTOM_CORNER));
        return renderedPowerup;
    }

    /* ===== PLAYER BOARD ===== */
    public static List<List<String>> renderPlayerBoard(PlayerBoardView playerBoard, String player, Map<String, ColoredObject> playersColor) {
        List<List<String>> renderedPlayerBoard = new ArrayList<>();
        if (playerBoard == null) {
            return renderedPlayerBoard;
        }
        // Determine the width of the board according to the number of damages
        // Setting a minimum value of 20
        final int boardWidth = playerBoard.getDamages().size() * 2 + 4 < 20 ? 20 : playerBoard.getDamages().size() * 2 + 4;
        expandStringRendering(renderedPlayerBoard, generateLine(HORIZONTAL_BORDER, boardWidth, LEFT_TOP_CORNER, RIGHT_TOP_CORNER));
        // Add the colored name of the player
        List<String> playerNameChunks = splitString(player, boardWidth - 12);
        playerNameChunks.forEach(nameChunk -> {
            expandStringRendering(renderedPlayerBoard, generateLine(BLANK, boardWidth, VERTICAL_BORDER, VERTICAL_BORDER));
            fillLineWithText(renderedPlayerBoard.get(renderedPlayerBoard.size() - 1), nameChunk, 2, ANSI_BOLD, playersColor.get(player).getAnsiCode());
        });
        // Then add indicators for deaths
        List<String> playerInfoLine = renderedPlayerBoard.get(1);
        playerInfoLine.set(playerInfoLine.size() - 4, String.format("%2d", playerBoard.getDeaths()));
        playerInfoLine.remove(playerInfoLine.size() - 2);
        playerInfoLine.set(playerInfoLine.size() - 6, ANSI_SKULL);
        playerInfoLine.set(playerInfoLine.size() - 8, "|");
        // Add indicator for marks
        // Check whether there is already a free row, if not create it
        if (playerNameChunks.size() == 1) {
            expandStringRendering(renderedPlayerBoard, generateLine(BLANK, boardWidth, VERTICAL_BORDER, VERTICAL_BORDER));
        }
        playerInfoLine = renderedPlayerBoard.get(2);
        playerInfoLine.set(playerInfoLine.size() - 4, String.format("%2d", playerBoard.getMarks()));
        playerInfoLine.remove(playerInfoLine.size() - 2);
        playerInfoLine.set(playerInfoLine.size() - 6, ANSI_MARK);
        playerInfoLine.set(playerInfoLine.size() - 8, "|");
        expandStringRendering(renderedPlayerBoard, generateLine(LIGHT_HORIZONTAL_BORDER, boardWidth, LIGHT_LEFT_VERTICAL_SEPARATOR, LIGHT_RIGHT_VERTICAL_SEPARATOR));
        // Render damages
        expandStringRendering(renderedPlayerBoard, generateLine(BLANK, boardWidth, VERTICAL_BORDER, VERTICAL_BORDER));
        fillLineWithObjects(
                renderedPlayerBoard.get(renderedPlayerBoard.size() - 1),
                playerBoard.getDamages(),
                p -> playersColor.get(p).getAnsiCode(),
                p -> ANSI_DOT,
                2,
                2
        );
        // Add bottom border
        expandStringRendering(renderedPlayerBoard, generateLine(HORIZONTAL_BORDER, boardWidth, LEFT_BOTTOM_CORNER, RIGHT_BOTTOM_CORNER));
        return renderedPlayerBoard;
    }

    /* ===== KILLSHOTS TRACK ===== */

    /**
     * Render a killshots track according to the provided number of total and available skulls
     *
     * @param takenSkulls The number of taken skulls
     * @param totalSkulls THe number of total skulls
     * @return A list of all the lines composing the rendered track
     */
    public static List<List<String>> renderKillshotsTrack(int takenSkulls, int totalSkulls) {
        List<List<String>> renderedTrack = new ArrayList<>();
        int trackWidth = totalSkulls * 2 + 4; // add gap between two consecutive skulls and external padding
        // Generate a list of integers to represent skulls and determine their color later
        List<Integer> skulls = new ArrayList<>();
        for (int i = 0; i < totalSkulls; i++) {
            skulls.add(i);
        }
        expandStringRendering(renderedTrack, generateLine(HORIZONTAL_BORDER, trackWidth, LEFT_TOP_CORNER, RIGHT_TOP_CORNER));
        expandStringRendering(renderedTrack, generateLine(BLANK, trackWidth, VERTICAL_BORDER, VERTICAL_BORDER));
        fillLineWithText(renderedTrack.get(renderedTrack.size() - 1), "Killshots", 2, ANSI_BOLD);
        expandStringRendering(renderedTrack, generateLine(LIGHT_HORIZONTAL_BORDER, trackWidth, LIGHT_LEFT_VERTICAL_SEPARATOR, LIGHT_RIGHT_VERTICAL_SEPARATOR));
        expandStringRendering(renderedTrack, generateLine(BLANK, trackWidth, VERTICAL_BORDER, VERTICAL_BORDER));
        fillLineWithObjects(
                renderedTrack.get(renderedTrack.size() - 1),
                skulls,
                skullIndex -> skullIndex < takenSkulls ? ANSI_RED : ANSI_RESET,
                skullIndex -> ANSI_SKULL,
                2,
                2
        );
        expandStringRendering(renderedTrack, generateLine(HORIZONTAL_BORDER, trackWidth, LEFT_BOTTOM_CORNER, RIGHT_BOTTOM_CORNER));
        return renderedTrack;
    }

    /* ===== SPAWN SQUARES WEAPONS ===== */

    /**
     * Renders a cell showing information about the weapons contained in a spawn square
     *
     * @param color    The object representing the color of the spawn square
     * @param location The object representing the location of the square (in coordinates)
     * @param board    The object representing the board view
     * @return The textual representation of the cell
     */
    private static List<List<String>> renderSpawnWeaponsCell(ColoredObject color, CoordPair location, BoardView board) {
        //TODO: Use weapons factory to retrieve weapon names
        List<List<String>> renderedCell = new ArrayList<>();
        List<String> weapons = ((SpawnSquareView) board.getSquare(location)).getWeapons();
        expandStringRendering(renderedCell, generateLine(HORIZONTAL_BORDER, SPAWN_WEAPONS_CELL_DIM, LEFT_TOP_CORNER, RIGHT_TOP_CORNER));
        expandStringRendering(renderedCell, generateLine(BLANK, SPAWN_WEAPONS_CELL_DIM, VERTICAL_BORDER, VERTICAL_BORDER));
        fillLineWithText(renderedCell.get(renderedCell.size() - 1), String.format("%d,%d", location.getX(), location.getY()), 2, ANSI_BOLD, color.getAnsiCode());
        expandStringRendering(renderedCell, generateLine(LIGHT_HORIZONTAL_BORDER, SPAWN_WEAPONS_CELL_DIM, LIGHT_LEFT_VERTICAL_SEPARATOR, LIGHT_RIGHT_VERTICAL_SEPARATOR));
        weapons.stream().filter(Objects::nonNull).forEach(weapon -> {
            expandStringRendering(renderedCell, generateLine(BLANK, SPAWN_WEAPONS_CELL_DIM, VERTICAL_BORDER, VERTICAL_BORDER));
            fillLineWithText(renderedCell.get(renderedCell.size() - 1), weapon, 2);
        });
        expandStringRendering(renderedCell, generateLine(HORIZONTAL_BORDER, SPAWN_WEAPONS_CELL_DIM, LEFT_BOTTOM_CORNER, RIGHT_BOTTOM_CORNER));
        return renderedCell;
    }

    /**
     * Renders a table showing information about the weapons contained in all the provided spawn squares
     *
     * @param board The object representing the board view
     * @return The textual representation of the table
     */
    public static List<List<String>> renderSpawnWeaponsTable(BoardView board) {
        List<List<List<String>>> renderedCells = new ArrayList<>();
        board.getSpawnPoints().forEach((color, location) -> renderedCells.add(renderSpawnWeaponsCell(color, location, board)));
        return concatRenderedElements(renderedCells, 1);
    }

    /* ===== PLAYERS OVERVIEW ===== */

    /**
     * Renders a table showing the list of players along with their color id
     *
     * @param players The map storing the color for each player
     * @return The textual representation of the table
     */
    public static List<List<String>> renderPlayersOverview(Map<String, PlayerColor> players) {
        List<List<String>> renderedOverview = new ArrayList<>();
        expandStringRendering(renderedOverview, generateLine(HORIZONTAL_BORDER, PLAYERS_OVERVIEW_DIM, LEFT_TOP_CORNER, RIGHT_TOP_CORNER));
        expandStringRendering(renderedOverview, generateLine(BLANK, PLAYERS_OVERVIEW_DIM, VERTICAL_BORDER, VERTICAL_BORDER));
        fillLineWithText(renderedOverview.get(renderedOverview.size() - 1), "Players", 2, ANSI_BOLD);
        expandStringRendering(renderedOverview, generateLine(LIGHT_HORIZONTAL_BORDER, PLAYERS_OVERVIEW_DIM, LIGHT_LEFT_VERTICAL_SEPARATOR, LIGHT_RIGHT_VERTICAL_SEPARATOR));
        players.forEach((player, color) -> {
            expandStringRendering(renderedOverview, generateLine(BLANK, PLAYERS_OVERVIEW_DIM, VERTICAL_BORDER, VERTICAL_BORDER));
            fillLineWithText(
                    renderedOverview.get(renderedOverview.size() - 1),
                    String.format("%s - %s", color.name().substring(0, 1), truncateString(player, PLAYERS_OVERVIEW_DIM - 8)),
                    2,
                    color.getAnsiCode());
            // TODO: Find a way to distinguish between green and gray
            expandStringRendering(renderedOverview, generateLine(BLANK, PLAYERS_OVERVIEW_DIM, VERTICAL_BORDER, VERTICAL_BORDER));
        });
        renderedOverview.remove(renderedOverview.size() - 1);
        expandStringRendering(renderedOverview, generateLine(HORIZONTAL_BORDER, PLAYERS_OVERVIEW_DIM, LEFT_BOTTOM_CORNER, RIGHT_BOTTOM_CORNER));
        return renderedOverview;
    }

    /* ===== RENDERINGS MANIPULATION ===== */

    /**
     * Compute the left displacement to be put before an element when concatenating,
     * according to the elements on its left that are shorter than the current height
     *
     * @param currentHeight The current height
     * @param elements      The list of all the elements
     * @param elementIndex  The index of the currently evaluated element
     * @param spacing       The spacing between two consecutive elements
     * @return The displacement to be put at the left of the element
     */
    private static int computeLeftDisplacement(int currentHeight, List<List<List<String>>> elements, int elementIndex, int spacing) {
        // Check how many elements on its left do not have content
        List<List<List<String>>> leftBlankElements = elements.subList(0, elementIndex).stream().filter(leftElement -> leftElement.size() <= currentHeight).collect(Collectors.toList());
        // Check if there is an element taller than the current height on its left and, if present, remove all the elements at his left
        Optional<List<List<String>>> rightBoundingElement = elements.subList(0, elementIndex).stream().filter(el -> el.size() > currentHeight).findFirst();
        rightBoundingElement.ifPresent(lists -> leftBlankElements.removeAll(elements.subList(0, elements.indexOf(lists))));
        return leftBlankElements.stream().map(blankElement -> {
            if (blankElement == null || blankElement.isEmpty()) {
                return 0;
            } else {
                return blankElement.get(0).size();
            }
        }).reduce(0, Integer::sum) + spacing * leftBlankElements.size();
    }

    /**
     * Generates a rendering of the provided elements so that they are placed one after the other
     *
     * @param elements The elements to concat
     * @param spacing  The spacing between two consecutive elements
     * @return A list of all the lines composing the concatenated elements
     */
    public static List<List<String>> concatRenderedElements(List<List<List<String>>> elements, int spacing) {
        // First retrieve the tallest element
        Optional<List<List<String>>> tallestElement = elements.stream().max(Comparator.comparingInt(List::size));
        if (!(tallestElement.isPresent())) {
            // This happens when the provided list is empty
            return Collections.emptyList();
        }

        // And create a container fitting that max height
        List<List<String>> baseReference = new ArrayList<>();
        for (int i = 0; i < tallestElement.get().size(); i++) {
            baseReference.add(new ArrayList<>());
        }

        // Then add all the other elements in order
        elements.forEach(element -> {
            // We iterate over the whole set of lines
            for (int i = 0; i < tallestElement.get().size(); i++) {
                // But consider just those index that are within the bounds of the currently evaluated element
                if (i < element.size()) {
                    final int leftDisplacement = computeLeftDisplacement(i, elements, elements.indexOf(element), spacing);
                    if (leftDisplacement != 0) {
                        baseReference.get(i).addAll(generateLine(BLANK, leftDisplacement));
                    }
                    if (spacing > 0) {
                        // If a non-negative spacing is provided add blank space
                        baseReference.get(i).addAll(generateLine(BLANK, spacing));
                    }
                    // And eventually append the element's chunk
                    baseReference.get(i).addAll(element.get(i));
                }
            }
        });
        return baseReference;
    }

    /**
     * Generates a rendering of the provided elements so that they are stacked one on top of the other
     *
     * @param elements The elements to stack
     * @param spacing  The spacing between two consecutive elements
     * @return A list of all the lines composing the rendered stacked elements
     */
    public static List<List<String>> stackRenderedElements(List<List<List<String>>> elements, int spacing) {
        if (elements == null) {
            return Collections.emptyList();
        }
        // Also consider when each element is of different width
        // We first get the maximum width
        int maxWidth = elements.stream().filter(element -> !element.isEmpty()).map(element -> element.get(0).size()).max(Integer::compareTo).orElse(0);
        // Then retrieve those elements shorter than it
        elements.stream().filter(element -> !element.isEmpty()).filter(element -> element.get(0).size() < maxWidth).forEach(element ->
                element.forEach(elementLine -> elementLine.addAll(generateLine(BLANK, maxWidth - elementLine.size())))
        );

        List<List<String>> baseReference = new ArrayList<>();
        elements.forEach(element -> {
            baseReference.addAll(element);
            if (spacing > 0) {
                baseReference.addAll(Collections.nCopies(spacing, generateLine(BLANK, maxWidth)));
            }
        });
        return baseReference;
    }
}