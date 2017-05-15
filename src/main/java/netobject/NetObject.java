package netobject;

/*
 * @author  ab3llini
 * @since   15/05/17.
 */

import java.io.Serializable;

/**
 * An object that can be transferred over the network (Socket & RMI)
 */
public abstract class NetObject implements Serializable {

    int id;

}
