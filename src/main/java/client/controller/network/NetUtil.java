package client.controller.network;

import java.util.regex.Pattern;

/*
 * @author  ab3llini
 * @since   12/06/17.
 */


public class NetUtil {

    public static boolean isIPv4(String ipStr) {

        return PATTERN.matcher(ipStr).matches() || ipStr.equals("localhost");

    }

    private static final Pattern PATTERN = Pattern.compile("^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");



}
