package netobject.action;

import client.view.cli.cmd.CliPrintable;

/*
 * @author  ab3llini
 * @since   23/05/17.
 */
public enum SelectionType implements CliPrintable {

    First("First cost"),
    Second("Second cost, if available");

    private final String  name;

    SelectionType(String  name){
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
