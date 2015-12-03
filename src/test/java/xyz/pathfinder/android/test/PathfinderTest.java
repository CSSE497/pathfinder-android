package xyz.pathfinder.android.test;

import org.junit.Test;
import xyz.pathfinder.android.Pathfinder;

import javax.websocket.DeploymentException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.Assert.assertTrue;

public class PathfinderTest {

    @Test
    public void testConnection() throws URISyntaxException, IOException, DeploymentException, InterruptedException {
        URI url = new URI("ws://api.thepathfinder.xyz:9000/socket");
        Pathfinder pathfinder = new Pathfinder("", "", url);
        Thread.sleep(1000);
        assertTrue(pathfinder.isConnected());
    }
}
