package rmi;

import java.io.*;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by tao on 4/15/16.
 */
public class MyInvocationHandler extends Stub implements InvocationHandler {
    private Socket socket;
    private Skeleton skeleton = null;
    private String hostname;
    private int port;

    public MyInvocationHandler(InetSocketAddress address) throws IOException {
        this.hostname = address.getHostName();
        this.port = address.getPort();
    }

    public MyInvocationHandler(Skeleton skeleton) throws IOException {
//        this.socket = new Socket(skeleton.iAddress.getAddress(), skeleton.iAddress.getPort());
        this.skeleton = skeleton;
    }

    public MyInvocationHandler(String hostName, int port) throws IOException {
        this.hostname = hostName;
        this.port = port;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String name = method.getName();
        if(name.equals("equals") || name.equals("toString") || name.equals("hashCode")) {
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
            if(proxy == null)   return proxy.hashCode();
            MyInvocationHandler handler = (MyInvocationHandler) Proxy.getInvocationHandler(proxy);
            if(handler.skeleton==null)  return proxy.getClass().hashCode();
            return proxy.getClass().hashCode() + handler.skeleton.hashCode();
        } else if(name.equals("equals")) {
            Object obj = args[0];
            if(proxy==null && obj==null)    return true;
            if(obj==null)   return false;
            MyInvocationHandler proxyHandler = (MyInvocationHandler) Proxy.getInvocationHandler(proxy);
            MyInvocationHandler objHandler = (MyInvocationHandler) Proxy.getInvocationHandler(obj);
            return proxy.getClass().equals(obj.getClass())
                    && proxyHandler.skeleton.equals(objHandler.skeleton);
        } else if(name.equals("toString")) {
            return proxyToString(proxy);
        } else {
            throw new IllegalArgumentException(
                    "unexpected Object method: " + method);
        }
    }

    public Serializable[] marshall(Method method, Object[] args) throws NotSerializableException {
        int argNum = 0;
        Class[] argTypes = method.getParameterTypes();
        argNum = argTypes.length;

        Serializable[] request = new Serializable[argNum + 1];
        int pos = 0;
        String methodName = method.getName();
        request[pos++] = methodName;

        try{
            for(int i=0;i<argNum;i++){
                Serializable t = (Serializable) args[i];
                request[pos++] = t;
            }
        }catch (ClassCastException e){
            throw new NotSerializableException("Argument(s) Not Serializable");
        }
        return request;
    }

    public Object invokeRemoteMethod(Object proxy, Method method, Object[] args) throws Exception {
        // Global variables needs synchronization.
        synchronized (this) {
            if (this.skeleton != null) {
                this.socket = new Socket(skeleton.iAddress.getAddress(), skeleton.iAddress.getPort());
            } else {
                this.socket = new Socket(hostname, port);
            }
        }

        Object ret = null;
        ObjectOutputStream oos;
        ObjectInputStream ois;
        try {
            oos = new ObjectOutputStream(socket.getOutputStream());
            Serializable[] request = marshall(method, args);

            oos.writeObject(request);
            oos.flush();
//            System.out.println("Request sent");
        }catch (Exception e){
//            e.printStackTrace();
            throw new RMIException("Remote call fails. Throws " + e.getClass().getName());
        }

        Class returnType = method.getReturnType();
        try{
            // get result
            ois = new ObjectInputStream(socket.getInputStream());
            ret = ois.readObject();
        }catch (Exception e){
//            e.printStackTrace();
//            System.out.println(method.getName());
//            e.printStackTrace();
            throw new RMIException("Remote call fails");
        }

        if(ret instanceof String && returnType==void.class){
            String retString = (String) ret;
            if(retString.equals("Complete"))    return null;
        }
        if(ret instanceof Exception){
            throw (Exception) ret;
        }

        if(oos!=null)   oos.close();
        if(ois!=null)   ois.close();
        this.socket.close();
        return ret;
    }

    private String proxyToString(Object proxy) {
        String address = "Host name: " + this.hostname + ", port: "
                + this.port;
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
}
