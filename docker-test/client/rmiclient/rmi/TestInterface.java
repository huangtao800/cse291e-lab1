package rmi;

/**
 * Created by tao on 4/23/16.
 */
public interface TestInterface {
    public String sayHello(int x, StringBuffer sb) throws rmi.RMIException;
}
