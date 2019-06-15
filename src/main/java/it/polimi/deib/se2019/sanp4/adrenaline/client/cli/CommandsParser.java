package it.polimi.deib.se2019.sanp4.adrenaline.client.cli;

import it.polimi.deib.se2019.sanp4.adrenaline.view.MessageType;

import java.io.InputStreamReader;

/**
 * A class describing an objects that takes input from the user and interprets it to render the game accordingly
 */
public class CommandsParser implements Runnable {
    private CLIRenderer renderer;
    private Boolean alive;
    private CancellableInput input;

    CommandsParser(CLIRenderer renderer) {
        this.renderer = renderer;
        this.alive = false;
        input = new CancellableInput(new InputStreamReader(System.in));
    }

    @Override
    public synchronized void run() {
        while (alive) {
            String command = input.readLine();
            if (command != null) {
                switch (command.toLowerCase()) {
                    case ":showmatch":
                    case ":showboard":
                    case ":showgame":
                        // Show the match screen and the game board
                        renderer.showMatchScreen();
                        break;
                    case ":showweapons":
                        // Show the weapons the user is holding
                        //TODO: Implement this method
                        break;
                    case ":showpowerups":
                        // Show the powerups the user is holding
                        //TODO: Implement this method
                        break;
                    default:
                        renderer.showMessage("Command not found", MessageType.ERROR);
                        break;
                }
            }
        }
    }

    /**
     * Sets the alive status of the parser
     *
     * @param alive {@code true} if the parser is alive, {@code otherwise}
     */
    void setAlive(boolean alive) {
        this.alive = alive;
    }

    /**
     * Gets the alive status of the parser
     *
     * @return {@code true} if the parser is alive, {@code otherwise}
     */
    boolean isAlive() {
        return alive;
    }
}
