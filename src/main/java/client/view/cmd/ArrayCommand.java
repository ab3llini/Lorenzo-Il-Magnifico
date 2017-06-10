package client.view.cmd;

import java.util.ArrayList;

/*
 * @author  ab3llini
 * @since   09/06/17.
 */
public class ArrayCommand<T> extends Command {


    public ArrayCommand(ArrayList<T> list) {

        super();

        int i = 1;

        for (T val : list) {

            String x = val.toString();

            this.commandsCache.put(x, Integer.toString(i));

            i++;

        }

    }

}
