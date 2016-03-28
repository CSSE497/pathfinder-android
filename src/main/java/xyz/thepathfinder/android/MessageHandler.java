package xyz.thepathfinder.android;

/**
 * Routes all web socket messages received to the receiving models.
 *
 * @author David Robinson
 */
interface MessageHandler extends javax.websocket.MessageHandler.Whole<String> {

    /**
     * Returns the number of messages received by the message handler.
     *
     * @return number of messages received by the message handler.
     */
    public int getReceivedMessageCount();
}
