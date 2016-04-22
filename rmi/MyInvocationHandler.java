package rmi;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by tao on 4/15/16.
 */
public class MyInvocationHandler implements InvocationHandler {
    private Socket socket;

    public MyInvocationHandler(InetSocketAddress address) throws IOException {
        this.socket = new Socket(address.getAddress(), 5000);
    }

    public MyInvocationHandler(Skeleton skeleton) throws IOException {
        this.socket = new Socket(skeleton.address.getAddress(), 5000);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // I think this proxy object is useless, since it should call RMI    -- Tao
        String methodName = method.getName();

        return null;
    }
}
