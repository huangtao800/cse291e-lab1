package rmi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
            InputStreamReader isReader = new InputStreamReader(clientSocket.getInputStream());
            BufferedReader reader = new BufferedReader(isReader);
            while(reader.readLine()!=null){

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
