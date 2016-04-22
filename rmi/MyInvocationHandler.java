package rmi;

import java.io.*;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by tao on 4/15/16.
 */
public class MyInvocationHandler extends Stub implements InvocationHandler {
    private Socket socket;

    public MyInvocationHandler(InetSocketAddress address) throws IOException {
        this.socket = new Socket(address.getAddress(), 5000);
    }

    public MyInvocationHandler(Skeleton skeleton) throws IOException {
        this.socket = new Socket(skeleton.address.getAddress(), 5000);
    }

    public MyInvocationHandler(String hostName) throws IOException {
        this.socket = new Socket(hostName, 5000);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if(method.getDeclaringClass() == Object.class) {
            return invokeObjectMethod(proxy, method, args);
        } else {
            return invokeRemoteMethod(proxy, method, args);
        }

    }

    /**
     * Handles java.lang.Object methods
     * @param proxy
     * @param method
     * @param args
     * @return
     */
    public Object invokeObjectMethod(Object proxy, Method method, Object[] args) {
        String name = method.getName();
        if(name.equals("hashCode")) {
            return hashCode();
        } else if(name.equals("equals")) {
            Object obj = args[0];
            return proxy == obj;
        } else if(name.equals("toString")) {
            return proxyToString(proxy);
        } else {
            throw new IllegalArgumentException(
                    "unexpected Object method: " + method);
        }
    }

    public Object invokeRemoteMethod(Object proxy, Method method, Object[] args) throws Exception {
        // I think this proxy object is useless, since it should call RMI    -- Tao
        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
        String methodName = method.getName();
        /*try {
            Class[] types = method.getParameterTypes();

            for(int i = 0; i < types.length; i++) {
                marshallObject(types[i], args[i], oos);
            }
        } catch (IOException ie) {
            System.err.println("Error in marshalling arguments");
            ie.printStackTrace();
        }*/

        Serializable[] argList = new Serializable[args.length+1];
        argList[0] = methodName;
        try{
            for(int i=0;i<args.length;i++){
                Serializable t = (Serializable) args[i];
                argList[i + 1] = t;
            }
        }catch (ClassCastException e){
            throw new NotSerializableException("Argument(s) Not Serializable");
        }

        oos.writeObject(argList);
        oos.flush();

        //GET THE RESULT OF THE REMOTE METHOD EXECUTION
        Object ret = null;
        try {
            Class returnType = method.getReturnType();
            //the return type of the method is void
            if(returnType == void.class) {
                return null;
            }

            // get result
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            // wait for reply
            while((ret=ois.readObject())==null){

            }
            ret = unmarshallValue(returnType, ois);


        } catch (Exception e) {
            e.printStackTrace();
        }

        return ret;
    }

    private String proxyToString(Object proxy) {
        String address = "Host name: " + socket.getInetAddress().getHostName() + ", port: "
                + socket.getPort();
        Class<?>[] interfaces;
        interfaces = proxy.getClass().getInterfaces();
        if(interfaces.length == 0) {
            return "Proxy[" + this + "]" + ", " + address;
        }
        String iface = interfaces[0].getName();
        if (iface.equals("java.rmi.Remote") && interfaces.length > 1) {
            iface = interfaces[1].getName();
        }
        int dot = iface.lastIndexOf('.');
        if (dot >= 0) {
            iface = iface.substring(dot + 1);
        }
        return "Proxy[" + iface + "," + this + "]" + ", " + address;
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

    private Object unmarshallValue(Class<?> clazz, ObjectInputStream ois) throws IOException, ClassNotFoundException {
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
