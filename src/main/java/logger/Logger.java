package logger;

/*
 * @author  ab3llini
 * @since   16/05/17.
 */

public class Logger {

    private static boolean silenced = false;
    private static Level minLevel = Level.FINE;

    /**
     * Logs the message with the provided level and exception message
     * @param lv The level provided
     * @param ctx The context of the log, usually a description of the class that is logging
     * @param message The message
     * @param e The exception
     */
    public static void log(Level lv, String ctx, String message, Exception e) {

        parse(lv, ctx, message, e);

    }

    /**
     * Logs the message with the provided level
     * @param lv The level provided
     * @param ctx The context of the log, usually a description of the class that is logging
     * @param message The message
     */
    public static void log(Level lv, String ctx, String message) {

        parse(lv, ctx, message, null);

    }

    /**
     * Parses the level received
     * @param lv The level to be parsed
     * @param message The message
     * @param e The exception (may be null)
     */
    private static void parse(Level lv, String ctx, String message, Exception e) {

        if (lv.compareTo(minLevel) >= 0) {

            switch (lv) {

                case FINE:
                    print("[FINE] " + ctx + " : " + message, e);
                    break;

                case INFO:
                    print("[INFO] " + ctx + " : " + message, e);
                    break;

                case WARNING:
                    print("[WARNING] " + ctx + " : " + message, e);
                    break;

                case SEVERE:
                    print("[SEVERE] " + ctx + " : " + message, e);
                    break;

            }
        }

    }

    /**
     * Prints the log on the console
     * @param message The formatted message
     * @param e The exception to be appended
     */
    private static void print(String message, Exception e) {

        if (silenced) {

           return;

        }

        if (e != null) {

            System.out.println(message + " | Exception : " + e.getMessage());

        }

        else {

            System.out.println(message);

        }

    }

    /**
     * Enables/Disables the logger
     * @param silenced true or false
     */
    public static void setSilenced(boolean silenced) {
        Logger.silenced = silenced;
    }

    /**
     * Sets the minimum logging level. The higher, the less loggings
     * @param minLevel The level
     */
    public static void setMinLevel(Level minLevel) {
        Logger.minLevel = minLevel;
    }
}
