package server.utility;

import client.view.cli.cmd.CliPrintable;

/**
 * Created by LBARCELLA on 05/06/2017.
 */
public enum UnicodeChars implements CliPrintable {

    Coins(new int[]{0x1F4B0}),
    Servants(new int[]{0x1F473}),
    Wood(new int[]{0x1F384}),
    Stones(new int[]{0x2297}),
    VictoryPoints(new int[]{0x24CB}),
    FaithPoints(new int[]{0x271D}),
    MilitaryPoints(new int[]{0x2694}),
    Council(new int[]{0x1F4DC}),
    Immediate(new int[]{0x26A1}),
    Harvest(new int[]{0x1F528}),
    Production(new int[]{0x2699}),
    CheckMark(new int[]{0x2714}),
    Ballot(new int[]{0x2718}),
    Man(new int[]{0x1F468}),
    Lock(new int[]{0x1F512}),
    OpenLock(new int[]{0x1F513}),
    KeyLock(new int[]{0x1F510}),
    FamilyMember(new int[]{0x25AE}),
    Card(new int[]{0x25AF}),
    DieFaceOne(new int[]{0x2680}),
    DieFaceTwo(new int[]{0x2681}),
    DieFaceThree(new int[]{0x2682}),
    DieFaceFour(new int[]{0x2683}),
    DieFaceFive(new int[]{0x2684}),
    DieFaceSix(new int[]{0x2685}),
    Arrow(new int[]{0x2192}),
    Start(new int[]{0x1F3C1}),
    Forbidden(new int[]{0x1F6AB}),
    Error(new int[]{0x274C}),
    Info(new int[]{0x2139}),
    Thumb(new int[]{0x1F44D}),
    Ok(new int[]{0x1F197}),
    Success(new int[]{0x2705}),
    Warning(new int[]{0x26A0}),
    Dots(new int[]{0x20DB}),
    Enter(new int[]{0x2386}),
    Force(new int[]{0x1F4AA}),
    Permanent(new int[]{0x221E});

    private final int[] value;

    UnicodeChars(int[] value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return new String(value, 0, value.length);
    }
}
