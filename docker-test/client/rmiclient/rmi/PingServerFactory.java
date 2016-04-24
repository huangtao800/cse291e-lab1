package rmi;

import java.net.InetSocketAddress;

/**
 * Created by tao on 4/24/16.
 */
public class PingServerFactory {
    public static PingInterface makePingServer(String ip, int port){
        return Stub.create(PingInterface.class, new InetSocketAddress(ip, port));
    }
}
