package rmi;

/**
 * Created by tao on 4/23/16.
 */
public class TestImpl implements  TestInterface {
    @Override
    public String sayHello() throws RMIException {
        return "Hello client";
    }
}
