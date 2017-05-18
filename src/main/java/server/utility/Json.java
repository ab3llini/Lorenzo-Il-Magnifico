package server.utility;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

/*
 * @author  ab3llini
 * @since   18/05/17.
 */
public class Json {

    /**
     * Returns all the keys associated with the json object
     * @param object the json object
     * @return an array list of keys (Strings)
     */
    public static ArrayList<String> getObjectKeys(JsonObject object) {

        ArrayList<String> keys = new ArrayList<String>();

        Set<Map.Entry<String, JsonElement>> entrySet = object.entrySet();

        for (Map.Entry<String, JsonElement> entry : entrySet) {

            //Append key
            keys.add(entry.getKey());

        }
        return keys;

    }

}
