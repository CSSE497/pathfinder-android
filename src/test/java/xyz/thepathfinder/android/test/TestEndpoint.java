package xyz.thepathfinder.android.test;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.util.logging.Level;
import java.util.logging.Logger;

@ServerEndpoint(value = "/socket")
public class TestEndpoint {

    Logger logger = Logger.getLogger(TestEndpoint.class.getName());

    public static TestMessager messager = new TestMessager();

    public static TestMessager getMessager() {
        return TestEndpoint.messager;
    }

    @OnOpen
    public void onOpen(Session session) {
    }

    @OnMessage
    public String onMessage(String message, Session session) {

        logger.info("Server received: " + message);

        JsonParser parser = new JsonParser();
        JsonElement messJson = parser.parse(message);

        String receive = TestEndpoint.getMessager().getReceive();
        JsonElement recJson = parser.parse(receive);
        if(recJson.equals(messJson)) {
            logger.info("Json correct");
            TestEndpoint.getMessager().setCorrect(true);
        } else {
            logger.info("Json incorrect");
            TestEndpoint.getMessager().setCorrect(false);
        }

        String send = TestEndpoint.getMessager().getSend();
        logger.info("Server sending: " + send);
        return send;
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
    }
}
