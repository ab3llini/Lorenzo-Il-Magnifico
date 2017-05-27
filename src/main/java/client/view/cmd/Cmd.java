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
     * Prints the command choices available
     * @param values the values contained in the command enum
     */
    public static void printChoices(Enum[] values) {

        for (Command c : (Command[])values) {

            System.out.println(AnsiColors.ANSI_GREEN + "[" + c.getValue() + "] " + c.getName());

        }

    }

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

    /**
     * Checks whether or not the selected option is valid
     * In case it it not, a warning message is printed
     * @param values the valid values
     * @param choice the choice made
     * @return true or false
     */
    public static boolean isValid(Enum[] values, String choice) {

        for (Command c : (Command[])values) {

            if (c.getValue().equals(choice)) {

                return true;

            }

        }

        System.out.println(AnsiColors.ANSI_RED + "'" + choice + "' is not a valid choice!");

        return false;

    }

}
