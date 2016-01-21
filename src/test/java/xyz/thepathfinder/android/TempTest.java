package xyz.thepathfinder.android;


import org.junit.Test;

import javax.websocket.DeploymentException;
import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TempTest {

/*    @Test(timeout = 20000)
    public void test() throws InterruptedException, IOException {

        *//*Logger topLogger = Logger.getLogger("");

        Handler consoleHandler = null;
        for (Handler handler : topLogger.getHandlers()) {
            if (handler instanceof ConsoleHandler) {
                //found the console handler
                consoleHandler = handler;
                break;
            }
        }

        if (consoleHandler == null) {
            // not found, create a new one
            consoleHandler = new ConsoleHandler();
            topLogger.addHandler(consoleHandler);
        }
        //set the console handler level
        consoleHandler.setLevel(Level.INFO);*//*
        Pathfinder pathfinder = new Pathfinder("2e465a65-6c2b-4eaf-ae48-bd5f4ac209b8", "");

        try {
            pathfinder.connect();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (DeploymentException e) {
            e.printStackTrace();
        }

        Cluster cluster = pathfinder.getDefaultCluster();
        cluster.addListener(new MyClusterListener(cluster));
        cluster.connect();

        while(true) {
            Thread.sleep(100);
        }
    }*/
}
