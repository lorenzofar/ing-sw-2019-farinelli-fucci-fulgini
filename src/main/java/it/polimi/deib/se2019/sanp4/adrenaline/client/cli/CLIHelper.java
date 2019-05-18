package it.polimi.deib.se2019.sanp4.adrenaline.client.cli;

import java.util.*;

/**
 * An utility class to interact with the command-line.
 * It provides to read and write from the console
 */
class CLIHelper {

    /** Private constructor to hide the public one */
    private CLIHelper() {
    }

    /** Timer to print animations and schedule periodic printing */
    private static Timer timer;
    /** Scanner to read user input */
    private static final Scanner scanner = new Scanner(System.in);
    /** Stack of characters used for the spinner animation */
    private static final Queue<Character> spinnerStack = new LinkedList<>(Arrays.asList('⠋', '⠙', '⠹', '⠸', '⠼', '⠴', '⠦', '⠧', '⠇', '⠏'));

    /**
     * Prints the provided text, formatting it with the provided parameters
     * @param template The template of the text to print
     * @param args Optional arguments to insert inside the template
     */
    static void print(String template, Object... args) {
        System.out.print(String.format(template, args));
    }

    /**
     * Prints the provided text, formatting it with the provided parameters
     * and adding a new line after it
     * @param template The template of the text to print
     * @param args Optional arguments to insert inside the template
     */
    static void println(String template, Object... args) {
        print(template, args);
        print("\n");
    }

    /**
     * Print a section title highlighting its text
     * @param title The title to print
     */
    static void printTitle(String title){
        StringBuilder topBottomBorder = new StringBuilder();
        for(int i=0; i<title.length() + 16; i++){
            topBottomBorder.append("━");
        }
        println("%c%s%c", '┏', topBottomBorder.toString(), '┓');
        println("┃%8c%s%8c┃", ' ', title.toUpperCase(), ' ');
        println("%c%s%c", '┗', topBottomBorder.toString(), '┛');

    }

    /**
     * Asks the user to choose among a list of options
     * @param message A message to show to the user
     * @param options The list of objects representing the options
     * @return The index of the selected choice in the list
     */
    static int askOptionFromList(String message, List<Object> options) {
        println(message);
        for (int i = 0; i < options.size(); i++) {
            println("%d\t%s", i, options.get(i));
        }
        // Then wait for the input
        int selectedOption = parseInt();
        // Until we do not get a value within the bounds, we keep asking
        while(selectedOption < 0 || selectedOption >= options.size()){
            selectedOption = parseInt();
        }
        return selectedOption;
    }

    /**
     * Waits for the user to press the enter key
     */
    static void waitEnterKey(){
        String s = scanner.next();
        while(!s.equalsIgnoreCase("\r")){
            s = scanner.next();
        }
    }

    /**
     * Clears the console screen by using ANSI escape codes
     */
    static void clearScreen(){
        CLIHelper.print("\033[H\033[2J");
    }

    /**
     * Retrieves a string entered in the console
     * If the user enters an invalid input, it asks for it again until a valid one is provided
     * @param message An optional message to show to the user
     * @return The string entered by the user
     */
    static String parseString(String message){
        println("> %s:", message);
        return scanner.next();

    }

    /**
     * Retrieves an integer entered in the console
     * If the user enters an invalid input, it asks for it again until a valid one is provided
     * @return The integer entered by the user
     */
    private static int parseInt() {
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
     * @param message An message to show to the user
     * @return The integer entered by the user
     */
    private static int parseInt(String message){
        println("> %s:", message);
        return parseInt();
    }

    /* ===== SPINNERS AND LOADING ===== */

    /**
     * Prints the current frame of the spinner
     * By using backspace character it updates the previous character to make it seem an animation
     */
    static void printSpinner(){
        Character currentSpinnerSymbol = spinnerStack.remove();
        print("\b");
        print(currentSpinnerSymbol.toString());
        spinnerStack.add(currentSpinnerSymbol);
    }

    /**
     * Starts showing the spinner
     * The animation is updated every 100ms
     */
    static void startSpinner(){
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
    static void stopSpinner(){
        if(timer != null) {
            timer.cancel();
            timer = null;
            println("");
        }
    }

    /* ================================ */
}
