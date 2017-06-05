package server.utility;

import client.view.cmd.CliPrintable;

/**
 * Created by LBARCELLA on 05/06/2017.
 */
public enum UnicodeChars implements CliPrintable {

    Coins(new int[]{0x1F4B0});


    private final int[] value;

    UnicodeChars(int[] value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return new String(value, 0, value.length);
    }
}
