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
    private Skeleton<T> skeleton;
    private Class<T> c;

    public ClientHandler(T server, Socket clientSocket, Skeleton<T> skeleton, Class<T> c){
        this.serverInterface = server;
        this.clientSocket = clientSocket;
        this.skeleton = skeleton;
        this.c = c;
    }

    public Method getMethod(String methodName){
        Method[] methods = c.getMethods();
        for(Method m : methods){
            if(m.getName().equals(methodName))  return m;
        }
        return null;
    }

    private void throwException(ObjectOutputStream oos, Throwable e) throws IOException {
        Object[] res = new Object[2];
        res[0] = "Throw error";
        res[1] = e;
        oos.writeObject(res);
    }

    public void run(){
        try {
            ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream());

            ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());
            try {
                Serializable[] request = (Serializable[]) ois.readObject();
                String methodName = (String) request[0];

                Method m = getMethod(methodName);
                if(m == null){
                    throwException(oos, new RMIException("Method not found"));
                }else{
                    int argNum = m.getParameterTypes().length;
                    Object[] params = Arrays.copyOfRange(request, 1, argNum + 1);
                    m.setAccessible(true);
                    Object result = m.invoke(serverInterface, params);

                    Class returnType = m.getReturnType();
                    if(returnType != void.class){
                        oos.writeObject(result);
                    }else{
                        oos.writeObject("Complete");
                    }
                }

            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                throwException(oos, e.getTargetException());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            clientSocket.close();
        } catch (IOException e) {
            System.out.println("Getting Input/Output stream error");
            RMIException exception = new RMIException(e.getMessage());
            skeleton.service_error(exception);
        }

    }

}
