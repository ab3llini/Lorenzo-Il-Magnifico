package client.view.cli.cmd;

import logger.AnsiColors;
import server.utility.UnicodeChars;

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

        System.out.println(AnsiColors.ANSI_BLUE + UnicodeChars.Enter + " " + title + AnsiColors.ANSI_RESET);

    }
    /**
     * Notify the user about something
     * @param title the notification
     */
    public static void notify(String title) {

        System.out.println(AnsiColors.ANSI_PURPLE + UnicodeChars.Info + " " + title + AnsiColors.ANSI_RESET);

    }

    public static void error(String title) {

        System.out.println(AnsiColors.ANSI_RED  + UnicodeChars.Error + " " + title + AnsiColors.ANSI_RESET);

    }

    public static void forbidden(String title) {

        System.out.println(AnsiColors.ANSI_RED + UnicodeChars.Forbidden + " " + title + AnsiColors.ANSI_RESET);

    }

    public static void success(String title) {

        System.out.println(AnsiColors.ANSI_GREEN + UnicodeChars.Success + " " + title + AnsiColors.ANSI_RESET);

    }

}
