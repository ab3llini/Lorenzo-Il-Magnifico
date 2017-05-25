package client.view.cmd;

import client.view.cmd.Command;
import logger.AnsiColors;

/*
 * @author  ab3llini
 * @since   25/05/17.
 */
public class Cmd {

    public static void printChoices(Enum[] values) {

        for (Command c : (Command[])values) {

            System.out.println(AnsiColors.ANSI_GREEN + "[" + c.getValue() + "] " + c.getName());

        }

    }

    public static void askFor(String title) {

        System.out.println(AnsiColors.ANSI_BLUE + "> " + title);

    }

    public static void notify(String title) {

        System.out.println(AnsiColors.ANSI_PURPLE + "* " + title);

    }

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
