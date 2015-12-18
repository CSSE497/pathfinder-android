package xyz.thepathfinder.android.test;

import java.util.logging.Logger;

public class TestMessager {

    private static Logger logger = Logger.getLogger(TestMessager.class.getName());

    public String receive;
    public String send;
    public boolean correct;

    public void setReceive(String message) {
        this.receive = message;
    }

    public void setSend(String message) {
        this.send = message;
    }

    public void setCorrect(boolean correct) {
        this.correct = correct;
    }

    public String getReceive() {
        return this.receive;
    }

    public String getSend() {
        return this.send;
    }

    public boolean getCorrect() {
        return this.correct;
    }

    public void send(String message) {
        this.logger.info("Sever sending: " + message);
        TestEndpoint.session.getAsyncRemote().sendText(message);
    }

}
