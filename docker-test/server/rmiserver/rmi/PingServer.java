package rmi;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

/**
 * Created by tao on 4/24/16.
 */
public class PingServer implements PingInterface{
    @Override
    public String ping(int id) throws RMIException {
        return "Pong " + id;
    }

    public static void main(String[] args) throws UnknownHostException {
        PingInterface pingserver = new PingServer();
        String hostName = InetAddress.getLocalHost().getHostName();
        String ip = InetAddress.getLocalHost().getHostAddress();
        System.out.println("Hostname: " + hostName);
        System.out.println("IP Address: " + ip);

        int port = 8081;
        Skeleton<PingInterface> skeleton = new Skeleton<PingInterface>(PingInterface.class, pingserver,
                new InetSocketAddress(ip, port));
        try {
            skeleton.start();
        } catch (RMIException e) {
            e.printStackTrace();
        }
        System.out.println("Server starts");
    }
}
