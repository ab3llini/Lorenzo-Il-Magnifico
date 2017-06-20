package client.view.cli.cmd;

import exception.NoSuchCommandException;
import logger.AnsiColors;
import server.utility.UnicodeChars;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/*
 * @author  ab3llini
 * @since   09/06/17.
 */
public abstract class Command {

    protected LinkedHashMap<String, String> commandsCache;

    public final String getCommandForValue(String value) throws NoSuchCommandException {

        Iterator it = this.commandsCache.entrySet().iterator();

        while (it.hasNext()) {

            Map.Entry pair = (Map.Entry) it.next();

            if ((pair.getValue()).equals(value))

                return (String)pair.getKey();

        }

        throw new NoSuchCommandException("No command found for value " + value);

    }

    public final String getValueForCommand(String command) throws NoSuchCommandException {

        try {

            return this.commandsCache.get(command);

        } catch (NullPointerException e) {

            throw new NoSuchCommandException("A command named " + command + " does not exists!");

        }

    }

    public final void printChoiches() {

        Iterator it = this.commandsCache.entrySet().iterator();

        while (it.hasNext()) {

            Map.Entry pair = (Map.Entry) it.next();

            String space = (Integer.parseInt((String) pair.getValue()) > 9) ? "  " : "   ";

            System.out.println(AnsiColors.ANSI_GREEN + UnicodeChars.Arrow + "\t(" + pair.getValue() + ")" + space + AnsiColors.ANSI_RESET + pair.getKey());

        }

    }

    public final boolean isValid(String choice) {

        try {

            this.getCommandForValue(choice);


        } catch (NoSuchCommandException e) {

            Cmd.forbidden("'"+choice+"' is not a valid choice, try again.");

            return false;

        }

        return true;

    }

    protected Command() {

        this.commandsCache = new LinkedHashMap<>();

    }

}
