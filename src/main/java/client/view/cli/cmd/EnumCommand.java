package client.view.cli.cmd;

/*
 * Created by albob on 23/05/2017.
 */


import exception.NoSuchCommandException;
import logger.Level;
import logger.Logger;

/**
 * This class provides a nice abstraction when creating CLI commands.
 * You just call the constructor with an Enum and it will automatically create a cache with command name-value pairs
 * Many methods are available to interact with the user
 */
public class EnumCommand<T extends Enum<T>> extends Command {

    private Class<T> reference = null;

    public EnumCommand(Class<T> enumeration) {

        super();

        this.reference = enumeration;

        int i = 1;

        for (T val : enumeration.getEnumConstants()) {

            String x = val.toString();

            this.commandsCache.put(x, Integer.toString(i));

            i++;

        }

    }

    public boolean choiceMatch(String choice, T enumEntry) {

        if (this.isValid(choice)) {

            try {
                return this.getCommandForValue(choice).equals(enumEntry.toString());
            } catch (NoSuchCommandException e) {
                e.printStackTrace();
            }

        }

        return false;


    }

    public T getEnumEntryFromChoice(String choice) {

        if (this.reference == null) {

            Logger.log(Level.SEVERE, "EnumCommand", "This command was not constructed with an enum!");

            return null;

        }

        try {

            String cmd = this.getCommandForValue(choice);

            for (T val : this.reference.getEnumConstants()) {

                if (val.toString().equals(cmd))

                    return val;

            }

        } catch (NoSuchCommandException e) {
            e.printStackTrace();
        }

        return null;

    }



}
