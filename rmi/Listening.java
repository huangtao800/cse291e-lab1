package rmi;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by tao on 4/8/16.
 */
public class Listening<T> implements Runnable{
    private ServerSocket serverSocket;
    private T server;


    public Listening(T server) throws IOException {
        this.server = server;
        this.serverSocket = new ServerSocket(5000);
    }

    public void run(){
        try{
            while (true){
                Socket clientSocket = this.serverSocket.accept();
                // to do

            }
        }catch (Exception ex){
            
        }

    }
}