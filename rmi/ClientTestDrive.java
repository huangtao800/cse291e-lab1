package rmi;

import java.net.InetSocketAddress;

/**
 * Created by tao on 4/23/16.
 */
public class ClientTestDrive {

    public static void main(String[] args) throws RMIException {
        InetSocketAddress address = new InetSocketAddress("127.0.0.1", 8081);
        TestInterface testInterface = Stub.create(TestInterface.class, address);

        String ret = testInterface.sayHello(123, new StringBuffer("12345"));
        System.out.println(ret);
    }
}
