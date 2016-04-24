package rmi;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Arrays;

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

    public Method getMethod(String methodName){
        Method[] methods = serverInterface.getClass().getMethods();
        for(Method m : methods){
            if(m.getName().equals(methodName))  return m;
        }
        return null;
    }

    private void methodNotFound(String methodName){
        System.out.println(methodName + " not found!");
    }

    public void run(){
        try {
            ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream());

            ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());
            try {
                System.out.println("Request received");
                Serializable[] request = (Serializable[]) ois.readObject();
                String methodName = (String) request[0];
                Method m = getMethod(methodName);
                if(m == null){
                    methodNotFound(methodName);
                }
                int argNum = m.getParameterTypes().length;
                Object[] params = Arrays.copyOfRange(request, 1, argNum + 1);

                Object result = m.invoke(serverInterface, params);

                Class returnType = m.getReturnType();
                if(returnType != void.class){
                    oos.writeObject(result);
                }else{
                    oos.writeObject("Complete");
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                // reply checked exceptions
                oos.writeObject(e.getTargetException());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            clientSocket.close();
        } catch (IOException e) {
            System.out.println("Getting Input/Output stream error");
        }

    }

}
