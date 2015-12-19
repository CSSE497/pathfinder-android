package xyz.thepathfinder.android;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Logger;

public class PathfinderTest {

    private TestServer server;
    private TestMessager messager;

    @Before
    public void setupSever() {
        Logger l1 = Logger.getLogger("org.glassfish");
        l1.setUseParentHandlers(false);

        this.server = new TestServer();
        this.messager = TestEndpoint.getMessenger();
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
    public void testConnection() throws URISyntaxException, IOException, InterruptedException {
        URI url = new URI("ws://localhost:8025/socket");
        Pathfinder pathfinder = new Pathfinder("9c4166bb-9535-49e1-8844-1904a0b1f45b", "", url);
        Assert.assertFalse(pathfinder.isConnected());
        pathfinder.connect();
        Assert.assertTrue(pathfinder.isConnected());
        pathfinder.close();
        Assert.assertFalse(pathfinder.isConnected());
        pathfinder.connect();
        Assert.assertTrue(pathfinder.isConnected());
    }

    @Test(expected = IllegalStateException.class, timeout = 10000)
    public void testMessageWithNoConnection() throws URISyntaxException, IOException, InterruptedException {
        URI url = new URI("ws://localhost:8025/socket");
        Pathfinder pathfinder = new Pathfinder("9c4166bb-9535-49e1-8844-1904a0b1f45b", "", url);
        Assert.assertFalse(pathfinder.isConnected());
        Cluster cluster = pathfinder.getDefaultCluster();
        cluster.connect();
    }

    @Test(expected = IllegalStateException.class, timeout = 10000)
    public void testMessageWithNoConnection2() throws URISyntaxException, IOException, InterruptedException {
        URI url = new URI("ws://localhost:8025/socket");
        Pathfinder pathfinder = new Pathfinder("9c4166bb-9535-49e1-8844-1904a0b1f45b", "", url);
        Assert.assertFalse(pathfinder.isConnected());
        pathfinder.connect();
        Assert.assertTrue(pathfinder.isConnected());
        pathfinder.close();
        Assert.assertFalse(pathfinder.isConnected());
        Cluster cluster = pathfinder.getDefaultCluster();
        cluster.connect();
    }

    @Test(timeout = 10000)
    public void testGetDefaultCluster() throws URISyntaxException, IOException, InterruptedException {
        URI url = new URI("ws://localhost:8025/socket");
        Pathfinder pathfinder = new Pathfinder("", "", url);
        pathfinder.connect();
        Assert.assertTrue(pathfinder.isConnected());
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

        cluster.connect();
        Assert.assertEquals(1, pathfinder.getSentMessageCount());
        Assert.assertEquals(0, pathfinder.getReceivedMessageCount());
        this.messager.send(send.toString());
        this.waitForMessages(pathfinder, 1);
        Assert.assertTrue(this.messager.getCorrect());
        //this.waitForMessages(pathfinder, 3);
        //SubscribableCrudModel transport = cluster.createTransport("hi", 32.32,42, TransportStatus.OFFLINE,null);
        //this.waitForMessages(pathfinder, 5);

        pathfinder.close();
    }
}
