package rmi;

import java.io.*;
import java.net.Socket;

/**
 * Created by tao on 4/15/16.
 *
 * Handles calls from client and designate to serverImpl
 */
public class ClientHandler<T> implements Runnable {
    private T serverInterface;
    private Socket clientSocket;

    public ClientHandler(T server, Socket clientSocket){
        this.serverInterface = server;
        this.clientSocket = clientSocket;
    }

    public void run(){
        try {
            ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream());

            ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());
            Object request = null;
            try {
                while((request=ois.readObject())!=null){

                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                // return class not found
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
