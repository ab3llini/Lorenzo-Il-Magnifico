package client.view.gui;/*
 * Created by albob on 03/07/2017.
 */

import client.controller.network.Client;
import client.view.LocalMatchController;

public class DialogController extends NavigationController {

    protected Client client;
    protected LocalMatchController localMatchController;

    public void setLocalMatchController(LocalMatchController localMatchController) {

        this.localMatchController = localMatchController;
    }

    public final void setClient(Client client) {
        this.client = client;
    }

}
