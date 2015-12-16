package xyz.thepathfinder.android.test;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import xyz.thepathfinder.android.Cluster;
import xyz.thepathfinder.android.Pathfinder;

import javax.websocket.DeploymentException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.Assert.assertTrue;

public class PathfinderTest {

    private TestServer server;
    private TestMessager messager;

    @Before
    public void setupSever() {
        Logger l1 = Logger.getLogger("org.glassfish");
        l1.setUseParentHandlers(false);

        this.server = new TestServer();
        this.messager = TestEndpoint.getMessager();
    }

    @After
    public void stopServer() {
        this.server.stopServer();
    }

    public void waitForMessages(Pathfinder pathfinder, int messageCount) throws InterruptedException {
        while(pathfinder.getReceivedMessageCount() != messageCount) {
            Thread.sleep(10);
        }
    }

    @Test(timeout = 10000)
    public void testConnection() throws URISyntaxException, IOException, DeploymentException, InterruptedException {
        URI url = new URI("ws://localhost:8025/socket");
        Pathfinder pathfinder = new Pathfinder("9c4166bb-9535-49e1-8844-1904a0b1f45b", "", url);
        assertTrue(pathfinder.isConnected());
        pathfinder.close();
    }

    @Test(timeout = 10000)
    public void testGetDefaultCluster() throws URISyntaxException, IOException, DeploymentException, InterruptedException {
        URI url = new URI("ws://localhost:8025/socket");
        Pathfinder pathfinder = new Pathfinder("9c4166bb-9535-49e1-8844-1904a0b1f45b", "", url);
        assertTrue(pathfinder.isConnected());
        Cluster cluster = pathfinder.getDefaultCluster();

        JsonObject receive = new JsonObject();
        receive.addProperty("model", "Cluster");
        receive.addProperty("path", "/default");
        receive.addProperty("type", "read");

        this.messager.setReceive(receive.toString());

        JsonObject send = new JsonObject();
        send.addProperty("type", "model");
        send.addProperty("path", "/default");
        send.addProperty("model", "Cluster");

        JsonObject value = new JsonObject();
        value.addProperty("path", "/default");
        value.addProperty("model", "Cluster");

        JsonArray transports = new JsonArray();
        JsonArray commodities = new JsonArray();
        JsonArray subclusters = new JsonArray();

        value.add("transports", transports);
        value.add("commodities", commodities);
        value.add("subClusters", subclusters);

        send.add("value", value);

        this.messager.setSend(send.toString());

        cluster.connect();
        this.waitForMessages(pathfinder, 1);
        Assert.assertTrue(this.messager.getCorrect());
        cluster.subscribe();
        //this.waitForMessages(pathfinder, 3);
        //SubscribableCrudModel transport = cluster.createTransport("hi", 32.32,42, TransportStatus.OFFLINE,null);
        //this.waitForMessages(pathfinder, 5);

        pathfinder.close();
    }
}
