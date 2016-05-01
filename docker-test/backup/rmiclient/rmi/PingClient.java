package rmi;

/**
 * Created by tao on 4/24/16.
 */
public class PingClient {

    public String pingServer(String ip, int port) throws RMIException {
        PingInterface server = PingServerFactory.makePingServer(ip, port);
        String response = server.ping(100);
        return response;
    }

    public static void main(String[] args){
        String ip = args[0];
        int port = 8081;
        PingClient client = new PingClient();
        int success = 0;
        int fail = 0;
        int testTimes = 4;
        for(int i=0;i<testTimes;i++){
            try {
                String res = client.pingServer(ip, port);
                System.out.println(res);
                success++;
            } catch (RMIException e) {
//                e.printStackTrace();
                fail++;
            }
        }
        System.out.println(success + " Tests Completed, " + fail + " Tests Failed.");
    }
}
