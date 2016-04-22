package rmi;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by tao on 4/8/16.
 */
public class Listening<T> implements Runnable{
    private ServerSocket serverSocket;
    private T server;

    private InetAddress inetAddress;


    public Listening(T server) throws IOException {
        this.server = server;
        this.serverSocket = new ServerSocket(5000);
    }

    public void run(){
        try{
            while (true){
                Socket clientSocket = this.serverSocket.accept();
                // to do
                ClientHandler<T> clientHandler = new ClientHandler<>(server, clientSocket);
                Thread t = new Thread(clientHandler);
                t.start();
            }
        }catch (IOException ex){
            
        }

    }
}
