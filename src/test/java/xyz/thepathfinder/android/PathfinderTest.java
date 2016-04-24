package xyz.thepathfinder.android;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.websocket.DeploymentException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

public class PathfinderTest {

/*
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
*/

    public void waitForMessages(Pathfinder pathfinder, int messageCount) throws InterruptedException {
        while(pathfinder.getReceivedMessageCount() != messageCount) {
            Thread.sleep(100);
        }
    }

    public void waitForMessages(TestMessager messager, int messageCount) throws InterruptedException {
        while(messager.messagesReceived != messageCount) {
            Thread.sleep(100);
        }
    }


/*    @Test(timeout = 10000)
    public void testConnection() throws URISyntaxException, IOException, InterruptedException, DeploymentException {
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
    public void testMessageWithNoConnection2() throws URISyntaxException, IOException, InterruptedException, DeploymentException {
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
    public void testGetDefaultCluster() throws URISyntaxException, IOException, InterruptedException, DeploymentException {
        URI url = new URI("ws://localhost:8025/socket");
        Pathfinder pathfinder = new Pathfinder("default", "", url);
        pathfinder.connect();
        Assert.assertTrue(pathfinder.isConnected());
        Cluster cluster = pathfinder.getDefaultCluster();

        JsonObject receive = new JsonObject();
        receive.addProperty("model", "Cluster");
        receive.addProperty("path", "default");
        receive.addProperty("message", "Read");

        this.messager.setReceive(receive.toString());

        JsonObject send = new JsonObject();
        send.addProperty("message", "Model");
        send.addProperty("path", "default");
        send.addProperty("model", "Cluster");

        JsonObject value = new JsonObject();
        value.addProperty("path", "default");
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
        this.waitForMessages(this.messager, 1);
        this.messager.send(send.toString());
        this.waitForMessages(pathfinder, 1);
        System.out.println(this.messager.getCorrect());
        Assert.assertTrue(this.messager.getCorrect());
        //this.waitForMessages(pathfinder, 3);
        //SubscribableCrudModel transport = cluster.createTransport("hi", 32.32,42, TransportStatus.OFFLINE,null);
        //this.waitForMessages(pathfinder, 5);

        pathfinder.close();
    }
*/

/*    @Test(timeout = 10000)
    public void test() throws InterruptedException {
        Pathfinder pf = new Pathfinder("9869bd06-12ec-451f-8207-2c5f217eb4d0", "");
        pf.connect(false);
  //      Cluster defaultCluster = pf.getDefaultCluster();
    //    Cluster texasCluster = defaultCluster.createSubcluster("texas");
      //  texasCluster.create();
        //texasCluster.addListener(new MyClusterListener());
        Cluster seattleCluster = pf.getDefaultCluster();
        //seattleCluster.connect();
        waitForMessages(pf, 4);

        // Use Gson's JSON object to create metadata
        JsonObject metadata = new JsonObject();
        metadata.addProperty("capacity", 10);

// Locally create a transport
        Transport transport = seattleCluster.createTransport(47.56383, -122.31490, TransportStatus.OFFLINE, metadata);
        transport.updateLocation(45.634, -124.3252);
// You can also add listeners to transports, MyTransportListener extends TransportListener
        //.addListener(new MyTransportListener());

// Create the transport on the Pathfinder server
        transport.create();
        Commodity commodity = seattleCluster.getCommodity("p");
        commodity.updatePickedUp(transport);
        commodity.updateDroppedOff();

        commodity.updateStartLocation(45.21312, -125.2131);
        commodity.updateEndLocation(53.132132, -12.432);
        commodity.updateStatus(CommodityStatus.CANCELLED);

        JsonObject commodityMetadata = new JsonObject();
        commodityMetadata.addProperty("bikes", 4);
        commodityMetadata.addProperty("people", 5);

        commodity.updateMetadata(commodityMetadata);
    }*/
}
