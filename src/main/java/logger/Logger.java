package logger;

/*
 * @author  ab3llini
 * @since   16/05/17.
 */

public class Logger {

    private static boolean silenced = false;
    private static int minLevel = Level.FINE;

    /**
     * Logs the message with the provided level and exception message
     * @param lv The level provided
     * @param ctx The context of the log, usually a description of the class that is logging
     * @param message The message
     * @param e The exception
     */
    public static void log(int lv, String ctx, String message, Exception e) {

        parse(lv, ctx, message, e);

    }

    /**
     * Logs the message with the provided level
     * @param lv The level provided
     * @param ctx The context of the log, usually a description of the class that is logging
     * @param message The message
     */
    public static void log(int lv, String ctx, String message) {

        parse(lv, ctx, message, null);

    }

    /**
     * Parses the level received
     * @param lv The level to be parsed
     * @param message The message
     * @param e The exception (may be null)
     */
    private static void parse(int lv, String ctx, String message, Exception e) {

        if (lv >= minLevel) {

            switch (lv) {

                case Level.FINE:
                    print(AnsiColors.ANSI_GREEN + "[FINE] " + ctx + " : " + message, e);
                    break;

                case Level.INFO:
                    print(AnsiColors.ANSI_BLUE + "[INFO] " + ctx + " : " + message, e);
                    break;

                case Level.WARNING:
                    print(AnsiColors.ANSI_YELLOW + "[WARNING] " + ctx + " : " + message, e);
                    break;

                case Level.SEVERE:
                    print(AnsiColors.ANSI_RED + "[SEVERE] " + ctx + " : " + message, e);
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
    public static void setMinLevel(int minLevel) {
        Logger.minLevel = minLevel;
    }
}
