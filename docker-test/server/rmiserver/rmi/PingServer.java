package rmi;

/**
 * Created by tao on 4/24/16.
 */
public class PingServer implements PingInterface{
    @Override
    public String ping(int id) throws RMIException {
        return "Pong " + id;
    }
}
