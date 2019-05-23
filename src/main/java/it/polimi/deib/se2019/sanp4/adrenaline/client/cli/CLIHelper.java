package it.polimi.deib.se2019.sanp4.adrenaline.client.cli;

import it.polimi.deib.se2019.sanp4.adrenaline.common.modelviews.BoardView;
import it.polimi.deib.se2019.sanp4.adrenaline.common.modelviews.PlayerView;
import it.polimi.deib.se2019.sanp4.adrenaline.common.modelviews.SquareView;
import it.polimi.deib.se2019.sanp4.adrenaline.model.board.CardinalDirection;
import it.polimi.deib.se2019.sanp4.adrenaline.model.board.CoordPair;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.ammo.AmmoCubeCost;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.powerup.PowerupCard;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.weapons.WeaponCard;

import java.util.*;
import java.util.function.Function;

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
    private static final Scanner scanner = new Scanner(System.in);
    /**
     * Stack of characters used for the spinner animation
     */
    private static final Queue<Character> spinnerStack = new LinkedList<>(Arrays.asList('⠋', '⠙', '⠹', '⠸', '⠼', '⠴', '⠦', '⠧', '⠇', '⠏'));

    /* ===== ANSI COLORS ===== */
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_BLACK = "\u001B[30m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_BLUE = "\u001B[34m";
    private static final String ANSI_PURPLE = "\u001B[35m";
    private static final String ANSI_CYAN = "\u001B[36m";
    private static final String ANSI_WHITE = "\u001B[37m";
    private static final String ANSI_BLACK_BACKGROUND = "\u001B[40m";
    private static final String ANSI_RED_BACKGROUND = "\u001B[41m";
    private static final String ANSI_GREEN_BACKGROUND = "\u001B[42m";
    private static final String ANSI_YELLOW_BACKGROUND = "\u001B[43m";
    private static final String ANSI_BLUE_BACKGROUND = "\u001B[44m";
    private static final String ANSI_PURPLE_BACKGROUND = "\u001B[45m";
    private static final String ANSI_CYAN_BACKGROUND = "\u001B[46m";
    private static final String ANSI_WHITE_BACKGROUND = "\u001B[47m";

    /* ===== ANSI FORMATTERS ===== */
    private static final String ANSI_BOLD = "\033[0;1m";
    private static final String ANSI_ITALIC = "\u001B[3m";

    /* ===== BORDER AND CORNERS ===== */
    private static final String TOP_SX_CORNER = "╔";
    private static final String TOP_DX_CORNER = "╗";
    private static final String BOTTOM_SX_CORNER = "╚";
    private static final String BOTTOM_DX_CORNER = "╝";
    private static final String VERTICAL_BORDER = "║";
    private static final String HORIZONTAL_BORDER = "═";
    private static final String LEFT_VERTICAL_SEPARATOR = "╠";
    private static final String RIGHT_VERTICAL_SEPARATOR = "╣";
    private static final String LIGHT_LEFT_VERTICAL_SEPARATOR = "╟";
    private static final String LIGHT_RIGHT_VERTICAL_SEPARATOR = "╢";
    private static final String LIGHT_HORIZONTAL_BORDER = "─";

    private static final String ANSI_DOT = "●";

    /* ===== DIMENSIONS ===== */
    private static final int SQUARE_DIM = 19;
    private static final int CARD_WIDTH = 20;


    /* ===== SEPARATORS ===== */
    private static final List<String> CARD_SEPARATOR = new ArrayList<>(Collections.nCopies(CARD_WIDTH, HORIZONTAL_BORDER));
    private static final List<String> CARD_BLANK_LINE = new ArrayList<>(Collections.nCopies(CARD_WIDTH, " "));
    private static final List<String> CARD_LIGHT_SEPARATOR = new ArrayList<>(Collections.nCopies(CARD_WIDTH, LIGHT_HORIZONTAL_BORDER));

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
     * Print a section title highlighting its text
     *
     * @param title The title to print
     */
    static void printTitle(String title) {
        StringBuilder topBottomBorder = new StringBuilder();
        for (int i = 0; i < title.length() + 16; i++) {
            topBottomBorder.append("━");
        }
        print(ANSI_GREEN);
        println(TRISTRING_TEMPLATE, TOP_SX_CORNER, topBottomBorder.toString(), TOP_DX_CORNER);
        println("%s%8c%s%8c%s", VERTICAL_BORDER, ' ', title.toUpperCase(), ' ', VERTICAL_BORDER);
        println(TRISTRING_TEMPLATE, BOTTOM_SX_CORNER, topBottomBorder.toString(), BOTTOM_DX_CORNER);
        resetColor();
    }

    /**
     * Asks the user to choose among a list of options
     *
     * @param message   A message to show to the user
     * @param options   The list of objects representing the options
     * @param allowNull Allows a null option to be chosen
     * @return The object representing the selected option, could be {@code null} if allowNull is {@code true}
     */
    static <T> T askOptionFromList(String message, List<T> options, boolean allowNull) {
        Integer n = null;

        print(ANSI_CYAN);
        println("%s", message);
        resetColor();

        options.forEach(option -> println("%d.\t%s", options.indexOf(option), option));
        if (allowNull) {
            print("%d. None", options.size());
        }

        int maxN = options.size() - (allowNull ? 0 : 1);

        do {
            if (n != null) print("This choice does not exist!");
            print(PROMPT_TEMPLATE, "");
            n = scanner.nextInt();
            scanner.nextLine(); /* Catch newline */
        } while (n > maxN || n < 0);

        return n == options.size() ? null : options.get(n);
    }

    /**
     * Asks the user to choose among a list of options
     *
     * @param message A message to show to the user
     * @param options The list of objects representing the options
     * @return The object representing the selected option
     */
    static <T> T askOptionFromList(String message, List<T> options) {
        return askOptionFromList(message, options, false);
    }


    /**
     * Waits for the user to press the enter key
     */
    static void waitEnterKey() {
        String s = scanner.next();
        while (!s.equalsIgnoreCase("\r")) {
            s = scanner.next();
        }
    }

    /**
     * Clears the console screen by using ANSI escape codes
     */
    static void clearScreen() {
        print("\033[H\033[2J");
        resetColor();
    }

    private static void resetColor() {
        print(ANSI_RESET);
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
        return scanner.next();

    }

    /**
     * Retrieves an integer entered in the console
     * If the user enters an invalid input, it asks for it again until a valid one is provided
     *
     * @return The integer entered by the user
     */
    private static int parseInt() {
        print(PROMPT_TEMPLATE);
        String input = scanner.next();
        try {
            return Integer.parseInt(input);
        } catch (Exception ex) {
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
    private static int parseInt(String message) {
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
     * Generate a new line according to  the provided template, with the provided left and right borders
     *
     * @param template    The list of strings representing the line template
     * @param leftBorder  The string representing the left border
     * @param rightBorder The string representing the right border
     */
    private static List<String> generateLine(List<String> template, String leftBorder, String rightBorder) {
        List<String> line = new ArrayList<>(template);
        line.set(0, leftBorder);
        line.set(line.size() - 1, rightBorder);
        return line;
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
    /* =================== */

    /* ===== SQUARES ===== */

    /**
     * Render the provided square to be printable in a CLI environment
     *
     * @param square The object representing the square
     * @return The list of all the rows the rendered square is composed of.
     * Each row is a list of string containing all the cells of the rendered square.
     */
    static List<List<String>> renderSquare(SquareView square) {
        // We first initialize the main list
        List<List<String>> squareRows = new ArrayList<>();

        // We initialize all the rows by putting blank cells
        for (int i = 0; i < SQUARE_DIM / 2; i++) {
            squareRows.add(new ArrayList<>(Collections.nCopies(SQUARE_DIM, " ")));
        }

        // If the provided square is null, we just return an empty list
        if (square == null) {
            return squareRows;
        }

        // We then build left and right edges, by setting the first and last character of each row according to the connection type
        squareRows.forEach(row -> {
            row.set(0, square.getAdjacentMap().get(CardinalDirection.W).getCharacterRepresentation());
            row.set(row.size() - 1, square.getAdjacentMap().get(CardinalDirection.E).getCharacterRepresentation());
        });

        // Then we take top and bottom border and we update their characters according to the connection type
        List<String> topBorder = squareRows.get(0);
        List<String> bottomBorder = squareRows.get(squareRows.size() - 1);
        topBorder.replaceAll(s -> square.getAdjacentMap().get(CardinalDirection.N).getCharacterRepresentation());
        bottomBorder.replaceAll(s -> square.getAdjacentMap().get(CardinalDirection.S).getCharacterRepresentation());

        // We add corners around the square
        topBorder.set(0, TOP_SX_CORNER);
        topBorder.set(topBorder.size() - 1, TOP_DX_CORNER);
        bottomBorder.set(0, BOTTOM_SX_CORNER);
        bottomBorder.set(bottomBorder.size() - 1, BOTTOM_DX_CORNER);

        // We add the marker indicating the square type
        squareRows.get(1).set(1, square.getTypeMarker());

        /* ===== PLAYERS RENDERING ===== */
        // We first calculate the center of the square
        int centerX = SQUARE_DIM / 2;
        int centerY = SQUARE_DIM / 4;
        // We retrieve the number of players inside the square
        int playersCount = square.getPlayers().size();
        // We compute the starting point of the line
        int playerX = centerX - playersCount / 2;
        // We print the players in a single row
        for (PlayerView player : square.getPlayers()) {
            squareRows.get(centerY).set(playerX, String.format(TRISTRING_TEMPLATE, player.getColor().getANSICode(), ANSI_DOT, square.getRoomColor().getANSICode()));
            playerX++;
        }
        // Then eventually put the correct ANSI code for the square color
        squareRows.forEach(row -> row.add(0, square.getRoomColor().getANSICode()));

        return squareRows;
    }

    /**
     * Print the provided board to the terminal
     *
     * @param board The object representing the board
     */
    public static void printBoard(BoardView board) {
        int width = board.getColumnsCount();
        int height = board.getRowsCount();
        // We define an initial list of lists to hold the rendered board
        List<List<String>> boardRows = new ArrayList<>();

        // We then proceed by considering each row
        for (int r = 0; r < height; r++) {
            // Then for each row we have to take all the squares that are inside it and append their rendered value
            // We add the first square of the row
            List<List<String>> rowSquares = renderSquare(board.getSquare(new CoordPair(0, r)));
            for (int c = 1; c < width; c++) {
                // We concatenate all the squares in the row
                // by adding all the lists to the previous ones
                List<List<String>> renderedSquare = renderSquare(board.getSquare(new CoordPair(c, r)));
                for (int i = 0; i < rowSquares.size(); i++) {
                    rowSquares.get(i).addAll(renderedSquare.get(i));
                }
            }
            // We then concatenate the row with the previous one
            boardRows.addAll(rowSquares);
        }

        // Eventually we print the result
        boardRows.forEach(row -> println(String.join("", row)));
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
        expandStringRendering(renderedWeapon, generateLine(CARD_SEPARATOR, TOP_SX_CORNER, TOP_DX_CORNER));

        // Split the name of the weapon (considering padding)
        List<String> weaponNameChunks = splitString(weaponCard.getName(), CARD_WIDTH - 4);
        // And add it to the rendered card
        weaponNameChunks.forEach(chunk -> {
            expandStringRendering(renderedWeapon, generateLine(CARD_BLANK_LINE, VERTICAL_BORDER, VERTICAL_BORDER));
            fillLineWithText(renderedWeapon.get(renderedWeapon.size() - 1), chunk, 2, ANSI_BOLD);
        });
        expandStringRendering(renderedWeapon, generateLine(CARD_SEPARATOR, LEFT_VERTICAL_SEPARATOR, RIGHT_VERTICAL_SEPARATOR));

        // Add indicators for ammo cubes
        // Each cube is represented by a colored dot, the first one is the one that comes pre-loaded with the weapon
        // First split the list of ammo cubes into chunks fitting the width, by considering a gao between each pair
        List<List<AmmoCubeCost>> ammoCubesChunks = splitList(weaponCard.getCost(), (CARD_WIDTH - 4) / 2);
        // And add them to the rendered card
        ammoCubesChunks.forEach(chunk -> {
            expandStringRendering(renderedWeapon, generateLine(CARD_BLANK_LINE, VERTICAL_BORDER, VERTICAL_BORDER));
            fillLineWithObjects(
                    renderedWeapon.get(renderedWeapon.size() - 1),
                    chunk,
                    cube -> cube.getCorrespondingCube().getANSICode(),
                    cube -> ANSI_DOT,
                    2,
                    2
            );
        });
        expandStringRendering(renderedWeapon, generateLine(CARD_SEPARATOR, LEFT_VERTICAL_SEPARATOR, RIGHT_VERTICAL_SEPARATOR));

        // Add description of effects
        weaponCard.getEffects().forEach(effect -> {
            // We add the name of the effect
            List<String> effectNameChunks = splitString(effect.getName(), CARD_WIDTH - 4);
            List<String> effectDescriptionChunks = splitString(effect.getDescription(), CARD_WIDTH - 4);
            List<List<AmmoCubeCost>> effectCostChunks = splitList(effect.getCost(), CARD_WIDTH - 4);

            effectNameChunks.forEach(effectNameChunk -> {
                expandStringRendering(renderedWeapon, generateLine(CARD_BLANK_LINE, VERTICAL_BORDER, VERTICAL_BORDER));
                fillLineWithText(renderedWeapon.get(renderedWeapon.size() - 1), effectNameChunk, 2);
            });
            expandStringRendering(renderedWeapon, generateLine(CARD_LIGHT_SEPARATOR, LIGHT_LEFT_VERTICAL_SEPARATOR, LIGHT_RIGHT_VERTICAL_SEPARATOR));
            effectDescriptionChunks.forEach(effectDescriptionChunk -> {
                expandStringRendering(renderedWeapon, generateLine(CARD_BLANK_LINE, VERTICAL_BORDER, VERTICAL_BORDER));
                fillLineWithText(renderedWeapon.get(renderedWeapon.size() - 1), effectDescriptionChunk, 2, ANSI_ITALIC);
            });
            effectCostChunks.forEach(effectCostChunk -> {
                expandStringRendering(renderedWeapon, generateLine(CARD_BLANK_LINE, VERTICAL_BORDER, VERTICAL_BORDER));
                fillLineWithObjects(
                        renderedWeapon.get(renderedWeapon.size() - 1),
                        effectCostChunk,
                        cube -> cube.getCorrespondingCube().getANSICode(),
                        cube -> ANSI_DOT,
                        2,
                        2
                );
            });
            expandStringRendering(renderedWeapon, generateLine(CARD_SEPARATOR, LEFT_VERTICAL_SEPARATOR, RIGHT_VERTICAL_SEPARATOR));
        });

        // Then remove the last inserted line, since is a middle separator
        renderedWeapon.remove(renderedWeapon.size() - 1);

        // Add bottom border
        expandStringRendering(renderedWeapon, generateLine(CARD_SEPARATOR, BOTTOM_SX_CORNER, BOTTOM_DX_CORNER));

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
        List<String> powerupNameChunks = splitString(powerupCard.getName(), CARD_WIDTH-4);
        List<String> powerupDescriptionChunks = splitString(powerupCard.getDescription(), CARD_WIDTH - 4);
        expandStringRendering(renderedPowerup, generateLine(CARD_SEPARATOR, TOP_SX_CORNER, TOP_DX_CORNER));
        powerupNameChunks.forEach(chunk -> {
            expandStringRendering(renderedPowerup, generateLine(CARD_BLANK_LINE, VERTICAL_BORDER, VERTICAL_BORDER));
            fillLineWithText(renderedPowerup.get(renderedPowerup.size() - 1), chunk, 2, ANSI_BOLD, powerupCard.getCubeColor().getANSICode());
        });
        expandStringRendering(renderedPowerup, generateLine(CARD_SEPARATOR, LEFT_VERTICAL_SEPARATOR, RIGHT_VERTICAL_SEPARATOR));
        powerupDescriptionChunks.forEach(chunk -> {
            expandStringRendering(renderedPowerup, generateLine(CARD_BLANK_LINE, VERTICAL_BORDER, VERTICAL_BORDER));
            fillLineWithText(renderedPowerup.get(renderedPowerup.size() - 1), chunk, 2, ANSI_ITALIC);
        });
        expandStringRendering(renderedPowerup, generateLine(CARD_SEPARATOR, BOTTOM_SX_CORNER, BOTTOM_DX_CORNER));
        return renderedPowerup;
    }
}
