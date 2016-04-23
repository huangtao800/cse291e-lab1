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
            InetAddress clientAddress = clientSocket.getInetAddress();
            String clientHost = (clientAddress != null) ? clientAddress.getHostAddress() : "0.0.0.0";

            ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream());

            ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());
            try {
                Object r = null;
                while((r=ois.readObject()) == null){}   // wait for request
                System.out.println("Request received");
                Serializable[] request = (Serializable[]) r;
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
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }


//            try {
//                String methodName = (String)ois.readObject();
//                Class clazz = serverInterface.getClass();
//
//                //find the method to be executed
//                Method[] methods = clazz.getMethods();
//                Method method = null;
//                for(Method m : methods) {
//                    if(m.getName().equals(methodName)) {
//                        method = m;
//                        break;
//                    }
//                }
//
//                Class[] types = method.getParameterTypes();
//                Object[] params = new Object[types.length];
//
//                for(int i = 0; i < params.length; i++) {
//                    unmashallValue(types[i], ois);
//                }
//
//                //execute the method
//                Object result = null;
//                Class returnType = method.getReturnType();
//                result = method.invoke(serverInterface, params);
//
//                //send the result back
//                if(returnType != void.class) {
//                    marshallValue(returnType, result, oos);
//                }
//
//                /*while((request=ois.readObject())!=null){
//
//                }*/
//            } catch (ClassNotFoundException e) {
//                e.printStackTrace();
//                // return class not found
//            } catch (InvocationTargetException e) {
//                e.printStackTrace();
//            } catch (IllegalAccessException e) {
//                e.printStackTrace();
//            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void marshallValue(Class<?> clazz, Object arg, ObjectOutputStream oos) throws IOException {
        if(clazz.isPrimitive()) {
            if(clazz == Integer.TYPE) {
                oos.writeInt(((Integer)arg).intValue());
            } else if(clazz == Boolean.TYPE) {
                oos.writeBoolean(((Boolean)arg).booleanValue());
            } else if(clazz == Byte.TYPE) {
                oos.writeByte(((Byte)arg).byteValue());
            } else if(clazz == Character.TYPE) {
                oos.writeByte(((Character)arg).charValue());
            } else if(clazz == Short.TYPE) {
                oos.writeShort(((Short)arg).shortValue());
            } else if(clazz == Long.TYPE) {
                oos.writeLong(((Long)arg).longValue());
            } else if(clazz == Float.TYPE) {
                oos.writeFloat(((Float)arg).floatValue());
            } else if(clazz == Double.TYPE) {
                oos.writeDouble(((Double)arg).doubleValue());
            } else {
                throw new Error("Unrecognized primitive type " + clazz);
            }
        } else {
            oos.writeObject(arg);
        }
    }

    private Object unmashallValue(Class<?> clazz, ObjectInputStream ois) throws IOException, ClassNotFoundException {
        if(clazz.isPrimitive()) {
            if(clazz == Integer.TYPE) {
                return Integer.valueOf(ois.readInt());
            } else if(clazz == Boolean.TYPE) {
                return Boolean.valueOf(ois.readBoolean());
            } else if(clazz == Byte.TYPE) {
                return Byte.valueOf(ois.readByte());
            } else if(clazz == Character.TYPE) {
                return Character.valueOf(ois.readChar());
            } else if(clazz == Short.TYPE) {
                return Short.valueOf(ois.readShort());
            } else if(clazz == Long.TYPE) {
                return Long.valueOf(ois.readLong());
            } else if(clazz == Float.TYPE) {
                return Float.valueOf(ois.readFloat());
            } else if(clazz == Double.TYPE) {
                return Double.valueOf(ois.readDouble());
            } else {
                throw new Error("Unrecognized primitive type " + clazz);
            }
        } else {
            return ois.readObject();
        }
    }
}
