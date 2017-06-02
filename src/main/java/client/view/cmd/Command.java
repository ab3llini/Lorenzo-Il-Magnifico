package client.view.cmd;

/*
 * Created by albob on 23/05/2017.
 */


import exception.NoSuchCommandException;
import logger.AnsiColors;
import logger.Level;
import logger.Logger;
import netobject.action.BoardSectorType;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * This class provides a nice abstraction when creating CLI commands.
 * You just call the constructor with an Enum and it will automatically create a cache with command name-value pairs
 * Many methods are available to interact with the user
 */
public class Command<T extends Enum<T>> {

    protected LinkedHashMap<String, String> commandsCache;

    private Class<T> initEnum = null;

    public Command() {

        this.commandsCache = new LinkedHashMap<String, String>();

    }

    public Command(Class<T> enumeration) {

        this();

        this.initEnum = enumeration;

        int i = 1;

        for (T val : enumeration.getEnumConstants()) {

            String x = val.toString();

            this.commandsCache.put(x, Integer.toString(i));

            i++;

        }

    }

    public String getCommandForValue(String value) throws NoSuchCommandException {

        Iterator it = this.commandsCache.entrySet().iterator();

        while (it.hasNext()) {

            Map.Entry pair = (Map.Entry) it.next();

            if ((pair.getValue()).equals(value))

                return (String)pair.getKey();

        }

        throw new NoSuchCommandException("No command found for value " + value);

    }

    public String getValueForCommand(String command) throws NoSuchCommandException {

        try {

            return this.commandsCache.get(command);

        } catch (NullPointerException e) {

            throw new NoSuchCommandException("A command named " + command + " does not exists!");

        }

    }

    public void printChoiches() {

        Iterator it = this.commandsCache.entrySet().iterator();

        while (it.hasNext()) {

            Map.Entry pair = (Map.Entry) it.next();

            System.out.println(AnsiColors.ANSI_GREEN + "[" + pair.getKey() + "] " + pair.getValue());

        }

    }

    public boolean isValid(String choice) {

        try {

            this.getCommandForValue(choice);


        } catch (NoSuchCommandException e) {

            System.out.println(AnsiColors.ANSI_RED + "Invalid choice '"+choice+"'" + AnsiColors.ANSI_RESET);

            return false;

        }

        return true;

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

        if (this.initEnum == null) {

            Logger.log(Level.SEVERE, "Command", "This command was not constructed with an enum!");

            return null;

        }

        try {

            String cmd = this.getCommandForValue(choice);

            for (T val : this.initEnum.getEnumConstants()) {

                if (val.toString().equals(cmd))

                    return val;

            }

        } catch (NoSuchCommandException e) {
            e.printStackTrace();
        }

        return null;

    }



}
