package xyz.thepathfinder.android.test;

import org.glassfish.tyrus.server.Server;

public class TestServer {

    private Server server;

    public TestServer() {
        this.server = this.runServer();
    }

    public Server runServer() {
        Server server = new Server("localhost", 8025, "", null, TestEndpoint.class);

        try {
            server.start();
        } catch(Exception e) {
            throw new RuntimeException(e);
        }

        return server;
    }

    public void stopServer() {
        this.server.stop();
    }
}
