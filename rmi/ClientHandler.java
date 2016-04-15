package rmi;

/**
 * Created by tao on 4/15/16.
 */
public class ClientHandler<T> implements Runnable {
    private T server;

    public ClientHandler(T server){
        this.server = server;
    }

    public void run(){

    }
}
