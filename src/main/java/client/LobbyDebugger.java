package client;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/*
 * @author  ab3llini
 * @since   19/05/17.
 */
public class LobbyDebugger {

    private static int SPAWNER_MAX_INTERVAL = 3;
    private static int LIFE_MAX_INTERVAL = 7;


    LobbyDebugger() {

        while (true) {

            Thread sc = new Thread(new SocketClient("localhost", 4545, (int)(Math.random() * LIFE_MAX_INTERVAL)));
            sc.start();

            try {
                Thread.sleep((int)(Math.random() * SPAWNER_MAX_INTERVAL) * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

    }

    public static void main(String[] args) {
        new LobbyDebugger();
    }

}
