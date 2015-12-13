package xyz.thepathfinder;

import org.junit.Test;
import xyz.thepathfinder.android.Cluster;
import xyz.thepathfinder.android.Commodity;
import xyz.thepathfinder.android.Pathfinder;
import xyz.thepathfinder.android.SubscribableModel;
import xyz.thepathfinder.android.Transport;

import javax.websocket.DeploymentException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.Assert.assertTrue;

public class PathfinderTest {

    public void waitForMessages(Pathfinder pathfinder, int messageCount) throws InterruptedException {
        while(pathfinder.getReceivedMessageCount() != messageCount) {
            Thread.sleep(10);
        }
    }

//    @Test(timeout = 10000)
//    public void testConnection() throws URISyntaxException, IOException, DeploymentException, InterruptedException {
//        URI url = new URI("ws://api.thepathfinder.xyz:9000/socket");
//        Pathfinder pathfinder = new Pathfinder("9c4166bb-9535-49e1-8844-1904a0b1f45b", "", url);
//        assertTrue(pathfinder.isConnected());
//        Cluster cluster = pathfinder.cluster();
//        cluster.connect();
//        this.waitForMessages(pathfinder, 1);
//        cluster.subscribe();
//        this.waitForMessages(pathfinder, 3);
//        SubscribableModel transport = cluster.createTransport();
//        this.waitForMessages(pathfinder, 5);
//
//        pathfinder.close();
//    }
}
