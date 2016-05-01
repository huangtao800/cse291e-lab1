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
    protected boolean started = true;


    public Listening(T server, ServerSocket serverSocket) throws IOException {
        this.server = server;
        this.serverSocket = serverSocket;
    }

    public void run(){
        Socket clientSocket = null;
        try{
            while (started){
                clientSocket = this.serverSocket.accept();
                // to do
                ClientHandler<T> clientHandler = new ClientHandler<>(server, clientSocket);
                Thread t = new Thread(clientHandler);
                t.start();
            }
        }catch (IOException ex){
            System.out.println("In IOException");
//            ex.printStackTrace();
        }finally {
            if(clientSocket!=null) try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            started = false;
        }
    }
}
