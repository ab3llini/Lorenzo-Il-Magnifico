package server.utility;

import client.view.cmd.CliPrintable;

/**
 * Created by LBARCELLA on 05/06/2017.
 */
public enum UnicodeChars implements CliPrintable {

    Coins(new int[]{0x1F4B0}),
    Servants(new int[]{0x1F473}),
    Wood(new int[]{0x1F332}),
    Stones(new int[]{0x2297}),
    VictoryPoints(new int[]{0x24CB}),
    FaithPoints(new int[]{0x24BB}),
    MilitaryPoints(new int[]{0x24C2});



    private final int[] value;

    UnicodeChars(int[] value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return new String(value, 0, value.length);
    }
}
