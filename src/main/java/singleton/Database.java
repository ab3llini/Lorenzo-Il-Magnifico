package singleton;

import com.google.gson.Gson;
import logger.Level;
import logger.Logger;
import server.model.Match;
import server.model.board.Player;
import server.utility.Security;

import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;

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

    public int saveMatch(Match match) throws SQLException {

        Gson gson = new Gson();

        String jsonInString = gson.toJson(match);

        //Create a statement
        Statement stmt = this.connection.createStatement();

        //Setup the timeout
        stmt.setQueryTimeout(QUERY_TIMEOUT);

        //Create the query;
        String query1 = "INSERT INTO matches ('finished') VALUES (0);";

        //Execute the query
        stmt.executeUpdate(query1);

        //Create the query;
        String query = "SELECT max(ID) FROM matches";

        //Execute the query
        ResultSet result = stmt.executeQuery(query);

        int matchID = result.getInt(1);

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


}

