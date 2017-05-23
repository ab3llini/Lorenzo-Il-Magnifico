package client;

import netobject.LoginAuthentication;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

/*
 * @author  ab3llini
 * @since   15/05/17.
 */

public class SocketClient implements Runnable {

    //Host properties
    private String host;
    private int port;

    private int life;

    private Socket socket;

    public SocketClient(String host, int port, int life) {

        //Assign host & port
        this.host = host;
        this.port = port;
        this.life = life;

    }




    public void connect() throws IOException {

        //Create a socket
        Socket socket = new Socket(this.host, this.port);

        this.socket = socket;

        //Debug
        System.out.println("Connection established with remote host (" + this.host + ":" + this.port + ")");

        ObjectOutputStream socketOut = new ObjectOutputStream(this.socket.getOutputStream());

        socketOut.flush();

        socketOut.writeObject(new LoginAuthentication("Alberto", null));


    }


    public static void main(String[] args) throws IOException {

        Thread sc = new Thread(new SocketClient("localhost", 4545, 3600));
        sc.start();


    }

    public void run() {

        try {
            this.connect();
        } catch (IOException e) {
            e.printStackTrace();
        }

        while (life > 0) {

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            life--;

        }

        System.out.println("Disconnecting..");

        try {
            this.socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
