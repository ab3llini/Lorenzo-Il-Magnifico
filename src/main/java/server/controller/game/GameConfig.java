package server.controller.game;

/*
 * @author  ab3llini
 * @since   18/05/17.
 */

import logger.Level;
import logger.Logger;
import server.utility.GameConfigParser;

/**
 * This is a singleton.
 * It will hold a reference to the game configuration
 */
public class GameConfig {

    /**
     * The reference to the instance of the singleton
     */
    private static GameConfig instance = null;

    /**
     * The Socket server port
     */
    private int socketPort;

    /**
     * The RMI server port
     */
    private int rmiPort;

    /**
     * The match timeout after which it should start
     */
    private int matchTimeout;

    /**
     * The player timeout to make a move
     */
    private int playerTimeout;

    /**
     * The constructor is responsible for parsing & loading the data
     */
    private GameConfig() {

        //Parse the object first into the parser
        GameConfigParser.parse();

        //Inject the values
        this.socketPort     = GameConfigParser.getSocketPort();
        this.rmiPort        = GameConfigParser.getRmiPort();
        this.matchTimeout   = GameConfigParser.getMatchTimeout();
        this.playerTimeout  = GameConfigParser.getPlayerTimeout();

        Logger.log(Level.INFO, "GameConfig", "Game configuration loaded");


    }

    /**
     * Get a reference to the singleton
     * @return the singleton instance
     */
    public static GameConfig getInstance() {

        if (instance == null) {

            instance = new GameConfig();

        }

        return instance;

    }


    public int getMatchTimeout() {
        return matchTimeout;
    }

    public int getPlayerTimeout() {
        return playerTimeout;
    }

    public int getRmiPort() {
        return rmiPort;
    }

    public int getSocketPort() {
        return socketPort;
    }



}
