package client.controller;

/*
 * @author  ab3llini
 * @since   02/07/17.
 */
public class Utility {

    public static boolean isInteger(String s) {

        try {

            Integer.parseInt(s);

            return true;

        } catch (Exception e) {
            return false;
        }

    }

}
