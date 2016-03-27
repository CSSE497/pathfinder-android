package xyz.thepathfinder.android;

/**
 * Routes all web socket messages received to the receiving models.
 *
 * @author David Robinson
 */
interface MessageHandler extends javax.websocket.MessageHandler.Whole<String> {
    public int getReceivedMessageCount();
}
