package server.utility;

/*
 * @author  ab3llini
 * @since   18/05/17.
 */

import com.google.gson.*;
import logger.Level;
import logger.Logger;


public class GameConfigParser {

    public static JsonObject config;

    public static JsonObject getConfig() {

        //Do not parse again if already.
        if (config != null) { return config; }


        try {

            //Fetch the config object
            config = Loader.getJsonObjectFromFile("json/game-config.json");

        }
        catch (Exception e) {

            Logger.log(Level.SEVERE, "GameConfigParser", "Unable to parse the config file! Any access to config will fail", e);

        }

        return config;

    }

    /**
     * Parses the rmi port
     * @return the rmi port
     */
    public static int getRmiPort() {

        return GameConfigParser.getConfig().getAsJsonObject("server").get("rmi-port").getAsInt();

    }

    /**
     * Parses the socket port
     * @return the socket port
     */
    public static int getSocketPort() {

        return GameConfigParser.getConfig().getAsJsonObject("server").get("socket-port").getAsInt();

    }

    /**
     * Parses the player timeout port
     * @return the player timeout
     */
    public static int getPlayerTimeout() {

        return GameConfigParser.getConfig().getAsJsonObject("player").get("timeout").getAsInt();

    }

    /**
     * Parses the match timeout port
     * @return the match timeout
     */
    public static int getMatchTimeout() {

        return GameConfigParser.getConfig().getAsJsonObject("match").get("timeout").getAsInt();

    }





}
