package it.polimi.deib.se2019.sanp4.adrenaline.client.cli;

import java.util.List;
import java.util.Scanner;

/**
 * An utility class to interact with the command-line.
 * It provides to read and write from the console
 */
class CLIHelper {
    // Private constructor to hide the public one
    private CLIHelper() {
    }

    private static final Scanner scanner = new Scanner(System.in);

    /**
     * Prints the provided text, formatting it with the provided parameters
     * @param template The template of the text to print
     * @param args Optional arguments to insert inside the template
     */
    static void print(String template, Object... args) {
        System.out.println(String.format(template, args));
    }

    /**
     * Print a section title highlighting its text
     * @param title The title to print
     */
    public static void printTitle(String title){
        StringBuilder topBottomBorder = new StringBuilder();
        for(int i=0; i<title.length() + 16; i++){
            topBottomBorder.append("━");
        }
        print("%c%s%c", '┏', topBottomBorder.toString(), '┓');
        print("┃%8c%s%8c┃", ' ', title.toUpperCase(), ' ');
        print("%c%s%c", '┗', topBottomBorder.toString(), '┛');

    }

    /**
     * Asks the user to choose among a list of options
     * @param message A message to show to the user
     * @param options The list of objects representing the options
     * @return The index of the selected choice in the list
     */
    static int askOptionFromList(String message, List<Object> options) {
        print(message);
        for (int i = 0; i < options.size(); i++) {
            print("%d\t%s", i, options.get(i));
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
     * Retrieves a string entered in the console
     * If the user enters an invalid input, it asks for it again until a valid one is provided
     * @param message An optional message to show to the user
     * @return The string entered by the user
     */
    static String parseString(String message){
        print("> %s:", message);
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
            print("Please insert a valid input");
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
        print("> %s:", message);
        return parseInt();
    }
}
