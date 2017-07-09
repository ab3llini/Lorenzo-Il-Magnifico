package client;
/*
 * Created by albob on 09/07/2017.
 */


import client.view.cli.CLI;
import client.view.cli.cmd.Cmd;
import client.view.cli.cmd.EnumCommand;
import client.view.gui.GUI;
import javafx.application.Application;

import java.io.BufferedReader;
import java.io.IOException;

public class Launcher {

    private enum UI {

        GUI("Graphical User Interface"),
        CLI("Command Line Interface");

        private String desc;

        UI(String desc) {
            this.desc = desc;
        }

        @Override
        public String toString() {
            return desc;
        }
    }

    public static void main(String[] args) throws InterruptedException {

        Cmd.askFor("Which user interface would you like to use?");

        BufferedReader console = new java.io.BufferedReader(new java.io.InputStreamReader(System.in));
        EnumCommand<UI> cmd = new EnumCommand<>(UI.class);

        String choice = "";


        do {

            cmd.printChoiches();

            try {

                choice = console.readLine();

            } catch (IOException e) {

                e.printStackTrace();

            }


        }
        while (!cmd.isValid(choice));

        UI selected = cmd.getEnumEntryFromChoice(choice);

        switch (selected) {
            case CLI:
                (new CLI()).play();
                break;
            case GUI:
                Application.launch(GUI.class, args);
                break;

        }


    }

}
