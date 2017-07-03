package client.view.gui;

import client.controller.network.Client;
import client.view.LocalMatchController;
import client.view.gui.NavigationController;

/*
 * @author  ab3llini
 * @since   02/07/17.
 */
public abstract class PlacementActionController extends DialogController {

    protected Integer additionalServants = 0;
    protected Integer index = 0;

    public final void setIndex(Integer index) {
        this.index = index;
    }

}
