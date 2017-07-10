package singleton;

import com.google.gson.*;
import logger.Level;
import logger.Logger;
import server.model.Match;
import server.model.board.Player;
import server.model.card.ban.BanCard;
import server.model.card.developement.BuildingDvptCard;
import server.model.card.developement.DvptCard;
import server.model.card.developement.TerritoryDvptCard;
import server.utility.InterfaceAdapter;
import server.utility.Json;
import server.utility.Security;


import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.ArrayList;

public class Database
{

    //The database relative URL
    private static final String DB_URL = "src/main/resources/db.sqlite";

    //The timeout for each query
    private static final int QUERY_TIMEOUT = 10;


    //The database singleton instance
    private static Database instance;

    //The connection object towards the server
    private Connection connection;

    /**
     * The constructor of the instance.
     * Initializes a connection towards the sqlite database
     */
    private Database() {

        //Load up the sqlite JDBC Driver (Must be set either manually or with MAVEN)
        try {

            Class.forName("org.sqlite.JDBC");

            //Connect!
            this.connection = DriverManager.getConnection("jdbc:sqlite:" + DB_URL);

        } catch (ClassNotFoundException e) {

            Logger.log(Level.SEVERE, "Database", "JDBC Driver not found", e);

        } catch (SQLException e) {

            Logger.log(Level.SEVERE, "Database::constructor", "SQL Exception", e);

        }

    }

    /**
     * Singleton method to retrieve the object instance
     * @return The database instance
     */
    public static Database getInstance() {

        if (instance == null) {

            instance = new Database();

        }

        return instance;

    }

    /**
     * This method attempts to perform a login with the provided data
     * @param username the username
     * @param password tha password, in plain, that will be hashed with MD5
     * @return true upon login success, false otherwise
     */
    public boolean login(String username, String password) {

        boolean authenticated = false;

        try {

            //Create a statement
            Statement stmt = this.connection.createStatement();

            //Setup the timeout
            stmt.setQueryTimeout(QUERY_TIMEOUT);

            //Create the query;
            String query = "SELECT * FROM users WHERE username = '" + username + "' AND password = '" + Security.MD5Hash(password) + "'";

            //Execute the query
            ResultSet result = stmt.executeQuery(query);

            //Check results
            while (result.next()) {

                //If there is a match, login succeeded
                authenticated = true;

            }

        } catch (SQLException e) {

            Logger.log(Level.SEVERE, "Database::login", "SQL Exception", e);

        } catch (NoSuchAlgorithmException e) {

            Logger.log(Level.SEVERE, "Database::login", "No such digest algorithm", e);

        }

        return authenticated;

    }

    /**
     * This method attempts to perform a registration with the provided data
     * @param username the username
     * @param password tha password, in plain, that will be hashed with MD5
     * @return true upon login success, false otherwise
     */
    public boolean registration(String username, String password) {

        boolean registrated = false;

        try {

            //Create a statement
            Statement stmt = this.connection.createStatement();

            //Setup the timeout
            stmt.setQueryTimeout(QUERY_TIMEOUT);

            //Create the query;
            String query = "SELECT * FROM users WHERE username = '" + username + "'";

            //Execute the query
            ResultSet result = stmt.executeQuery(query);

            //Check results
            while (result.next()) {

                //If there is a match, registration failed
                return false;

            }

        } catch (SQLException e) {

            Logger.log(Level.SEVERE, "Database::registration", "SQL Exception", e);

        }

        try {

            //Create a statement
            Statement stmt = this.connection.createStatement();

            //Setup the timeout
            stmt.setQueryTimeout(QUERY_TIMEOUT);

            //Create the query;
            String query = "INSERT INTO users VALUES ('"+username+"', '"+Security.MD5Hash(password)+"');";

            //Execute the query
            stmt.executeUpdate(query);

            registrated = true;

        } catch (SQLException e) {

            Logger.log(Level.SEVERE, "Database::registration", "SQL Exception", e);

        } catch (NoSuchAlgorithmException e) {

            Logger.log(Level.SEVERE, "Database::registration", "No such digest algorithm", e);

        }

        return registrated;

    }

    public void endMatch(int matchID) throws SQLException {

        //Create a statement
        Statement stmt = this.connection.createStatement();

        //Setup the timeout
        stmt.setQueryTimeout(QUERY_TIMEOUT);

        //Create the query;
        String query = "UPDATE matches SET 'finished' = 1 WHERE ID = "+matchID+";";

        //Execute the query
        stmt.executeUpdate(query);

    }

    public int saveMatch(Match match) throws SQLException {

        Gson gson = new Gson();

        String jsonInString = gson.toJson(match);

        //Create a statement
        Statement stmt = this.connection.createStatement();

        //Setup the timeout
        stmt.setQueryTimeout(QUERY_TIMEOUT);

        //if finished is 0 the match is not ended
        //we have maximum one not ended match for each player
        //Create the query;
        String query1 = "INSERT INTO matches ('finished') VALUES (0);";

        //Execute the query
        stmt.executeUpdate(query1);

        //Create the query;
        String query = "SELECT max(ID) FROM matches";

        //Execute the query
        ResultSet result = stmt.executeQuery(query);

        int matchID = result.getInt(1);

        //use this int to create dynamic queries
        int i=1;

        for (Player player : match.getPlayers()) {

            //Create the query;
            String queryPlayer = "UPDATE matches SET "+"'player_"+i+"'  =  ('"+player.getUsername()+"') WHERE ID ="+matchID+";";

            //Execute the query
            stmt.executeUpdate(queryPlayer);

            i++;

        }

        //Create the query;
        String query2 = "UPDATE matches  SET 'date' = ( '"+ jsonInString+"') WHERE ID = "+matchID+";";

        //Execute the query
        stmt.executeUpdate(query2);

        return matchID;

    }

    public void save(Match match) throws SQLException {

        int matchID = match.getMatch_id();

        Gson gson = getCorrectGson();

        String jsonInString = gson.toJson(match);

        //Create a statement
        Statement stmt = this.connection.createStatement();

        //Setup the timeout
        stmt.setQueryTimeout(QUERY_TIMEOUT);

        //Create the query;
        String query = "UPDATE matches  SET 'date' = ( '"+ jsonInString+"') WHERE ID = "+matchID+";";

        //Execute the query
        stmt.executeUpdate(query);

    }

    public int isAnUnfinishedMatchPlayer(String username)  {

        //we have maximum one not ended match for each player

        try {

            //Create a statement
            Statement stmt = this.connection.createStatement();

            //Setup the timeout
            stmt.setQueryTimeout(QUERY_TIMEOUT);

            //use this int to create dynamic queries (we can have maximum 5 players)
            for (int i = 1; i <= 5; i++) {

                //Create the query;
                String query = "SELECT * FROM matches WHERE player_" + i + " = '" + username + "' AND finished = 0";

                //Execute the query
                ResultSet result = stmt.executeQuery(query);

                //Check results
                while (result.next()) {

                    //If there is a match, login succeeded
                    return result.getInt("ID");

                }
            }

        }
        catch (SQLException e) {

            Logger.log(Level.WARNING, this.toString(), "SQL Exception", e);


        }

        return -1;

    }

    public ArrayList<String> wasInMatchWithHim(String username) {

        //we have maximum one not ended match for each player

        try {

            ArrayList<String> players = new ArrayList<>();

            //Create a statement
            Statement stmt = this.connection.createStatement();

            //Setup the timeout
            stmt.setQueryTimeout(QUERY_TIMEOUT);

            ResultSet result = null;

            //use this int to create dynamic queries (we can have maximum 5 players)
            for (int i = 1; i <= 5; i++) {

                //Create the query;
                String query = "SELECT count(*) FROM matches WHERE player_" + i + " = '" + username + "' AND finished = 0";

                //Execute the query
                ResultSet result1 = stmt.executeQuery(query);

                if (result1.getInt(1) == 1) {

                    //Create the query;
                    String query1 = "SELECT * FROM matches WHERE player_" + i + " = '" + username + "' AND finished = 0";

                    //Execute the query
                    result = stmt.executeQuery(query1);

                    break;

                }

            }

            //use this int to create dynamic queries (we can have maximum 5 players)
            int i = 1;

            if (result != null) {

                //Check results
                while (i <= 5) {

                    if (result.getString("player_" + i) != null && !(result.getString("player_" + i).equals(username)))
                        players.add(result.getString("player_" + i));

                    i++;
                }

            }

            return players;

        }
        catch (SQLException e) {

            Logger.log(Level.WARNING, this.toString(), "SQL Exception", e);

            return null;

        }

    }

    public Match getMatchFromID(int matchID)  {

        try {

            //Create a statement
            Statement stmt = this.connection.createStatement();

            //Setup the timeout
            stmt.setQueryTimeout(QUERY_TIMEOUT);

            //Create the query;
            String query = "SELECT date FROM matches WHERE ID = " + matchID + ";";

            ResultSet resultSet = stmt.executeQuery(query);

            String matchString = resultSet.getString(1);

            Gson gson = getCorrectGson();

            return gson.fromJson(matchString, Match.class);
        }
        catch (SQLException e) {

            Logger.log(Level.WARNING, this.toString(), "SQL Exception", e);

            return null;

        }

    }


    public Gson getCorrectGson(){

        GsonBuilder gsonBilder = new GsonBuilder();
        gsonBilder.registerTypeAdapter(DvptCard.class, new InterfaceAdapter<DvptCard>());
        gsonBilder.registerTypeAdapter(BanCard.class, new InterfaceAdapter<BanCard>());
        gsonBilder.setPrettyPrinting();

        return gsonBilder.create();

    }

}

