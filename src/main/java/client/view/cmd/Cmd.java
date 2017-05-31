package client.view.cmd;

import logger.AnsiColors;

/*
 * @author  ab3llini
 * @since   25/05/17.
 */


/**
 * A static class that provides methods to interact with the user via command line interface
 */
public class Cmd {
    /**
     * Ask the user to enter something
     * @param title the question
     */
    public static void askFor(String title) {

        System.out.println(AnsiColors.ANSI_BLUE + "> " + title);

    }

    /**
     * Notify the user about something
     * @param title the notification
     */
    public static void notify(String title) {

        System.out.println(AnsiColors.ANSI_PURPLE + "* " + title);

    }

}
