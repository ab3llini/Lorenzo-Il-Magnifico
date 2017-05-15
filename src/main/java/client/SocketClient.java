package client;

import netobject.Message;
import netobject.MessageType;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

/*
 * @author  ab3llini
 * @since   15/05/17.
 */

public class SocketClient {

    //Host properties
    private String host;
    private int port;

    private Socket socket;

    public SocketClient(String host, int port) {

        //Assign host & port
        this.host = host;
        this.port = port;

    }


    public void connect() throws IOException {

        //Create a socket
        Socket socket = new Socket(this.host, this.port);

        this.socket = socket;

        //Debug
        System.out.println("Connection established with remote host (" + this.host + ":" + this.port + ")");

        ObjectOutputStream socketOut = new ObjectOutputStream(this.socket.getOutputStream());

        socketOut.flush();

        socketOut.writeObject(new Message(MessageType.Registration, "Alberto"));


    }


    public static void main(String[] args) throws IOException {

        SocketClient sc = new SocketClient("localhost", 4545);
        sc.connect();

    }

}
