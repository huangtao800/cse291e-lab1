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
        String methodName = method.getName();

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

        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
        oos.writeObject(argList);
        oos.flush();

        // get result
        ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
        Object ret = null;

        // wait for reply
        while((ret=ois.readObject())==null){}


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

    private void marshallObject(Object[] args) {

    }
}
