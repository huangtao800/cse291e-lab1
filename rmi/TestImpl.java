package rmi;

/**
 * Created by tao on 4/23/16.
 */
public class TestImpl implements  TestInterface {
    @Override
    public String sayHello(int x, StringBuffer sb) throws RMIException {
        return x + sb.toString();
    }
}
