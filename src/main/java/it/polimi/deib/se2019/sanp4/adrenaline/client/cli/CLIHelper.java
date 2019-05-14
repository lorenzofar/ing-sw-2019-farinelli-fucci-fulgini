package it.polimi.deib.se2019.sanp4.adrenaline.client.cli;

import java.util.Scanner;

/**
 * An utility class to interact with the command-line.
 * It provides to read and write from the console
 */
class CLIHelper {
    // Private constructor to hide the public one
    private CLIHelper(){}

    private static final Scanner in = new Scanner(System.in);

    static void printf(String template, Object... args){
        System.out.printf(template, args);
    }
}
