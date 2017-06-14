package client.controller.network;

import java.util.regex.Pattern;

/*
 * @author  ab3llini
 * @since   12/06/17.
 */
public class NetUtil {

    public static boolean isIPv4(String ipStr) {
        return Pattern.matches("\\b((25[0–5]|2[0–4]\\d|[01]?\\d\\d?)(\\.)){3}(25[0–5]|2[0–4]\\d|[01]?\\d\\d?)\\b", ipStr);
    }

}
