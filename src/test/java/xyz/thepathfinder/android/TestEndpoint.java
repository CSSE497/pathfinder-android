package xyz.thepathfinder.android;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.util.logging.Logger;

@ServerEndpoint(value = "/socket")
public class TestEndpoint {

    Logger logger = Logger.getLogger(TestEndpoint.class.getName());

    public static TestMessager messenger = new TestMessager();
    public static Session session;

    public static TestMessager getMessenger() {
        return TestEndpoint.messenger;
    }

    @OnOpen
    public void onOpen(Session session) {
        TestEndpoint.session = session;
    }

    @OnMessage
    public void onMessage(String message, Session session) {

        logger.info("Server received: " + message);
        TestEndpoint.session = session;
        JsonParser parser = new JsonParser();
        JsonElement messJson = parser.parse(message);

        String receive = TestEndpoint.getMessenger().getReceive();
        JsonElement recJson = parser.parse(receive);
        if(recJson.equals(messJson)) {
            logger.info("Json correct");
            TestEndpoint.getMessenger().setCorrect(true);
        } else {
            logger.info("Json incorrect");
            TestEndpoint.getMessenger().setCorrect(false);
        }
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
    }
}
