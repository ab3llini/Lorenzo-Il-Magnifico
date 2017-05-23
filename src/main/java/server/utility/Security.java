package server.utility;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/*
 * @author  ab3llini
 * @since   23/05/17.
 */


/**
 * Static class with some useful security methods
 */
public class Security {

    /**
     * Converts a string to an MD5 hash
     * @param inputString the string to be converted
     * @return the hashed string to md5
     * @throws NoSuchAlgorithmException will never occur.
     */
    public static String MD5Hash(final String inputString) throws NoSuchAlgorithmException {

        //Creates a message digest
        MessageDigest md = MessageDigest.getInstance("MD5");

        //Update the message digest
        md.update(inputString.getBytes());

        //Get the final md5 digest
        byte[] digest = md.digest();

        return convertByteToHex(digest);
    }

    /**
     * Converts an array of bytes to a hexadecimal string
     * @param byteData the array to be converted
     * @return the converted string
     */
    private static String convertByteToHex(byte[] byteData) {

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < byteData.length; i++) {
            sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
        }

        return sb.toString();
    }

}
