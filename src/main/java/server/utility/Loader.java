package server.utility;

/*
 * @author  ab3llini
 * @since   18/05/17.
 */

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Generic file loader.
 */
public abstract class Loader {

    /**
     * Gets a JSON object from a file
     * @param filename the json file
     * @return the json object
     * @throws IOException in case a fault occurs while reading
     * @throws URISyntaxException in case the file url does not exists / malformed
     */
    public static JsonObject getJsonObjectFromFile(String filename) throws IOException, URISyntaxException {

        //The file
        File file = new File(Loader.class.getResource(filename).toURI());

        //The file reader
        BufferedReader reader = new BufferedReader(new FileReader(file));

        //Parse all the file
        JsonParser parser = new JsonParser();

        return parser.parse(reader).getAsJsonObject();
    }

}
