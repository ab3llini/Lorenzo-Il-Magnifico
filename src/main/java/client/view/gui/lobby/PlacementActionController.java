package client.view.gui.lobby;

import client.controller.network.Client;
import client.view.LocalMatchController;
import client.view.gui.NavigationController;

/*
 * @author  ab3llini
 * @since   02/07/17.
 */
public abstract class PlacementActionController extends NavigationController {

    protected Integer additionalServants = 0;
    protected Integer index = 0;

    protected Client client;
    protected LocalMatchController localMatchController;


    public final void setLocalMatchController(LocalMatchController localMatchController) {

        this.localMatchController = localMatchController;
    }

    public final void setClient(Client client) {
        this.client = client;
    }

    public final void setIndex(Integer index) {
        this.index = index;
    }

}
