package xyz.thepathfinder.android;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Routes all web socket messages received to the receiving models.
 *
 * @author David Robinson
 */
class MessageHandler implements javax.websocket.MessageHandler.Whole<String> {

    private static final Logger logger = LoggerFactory.getLogger(Action.class);

    /**
     * Holds access to all the models.
     */
    private PathfinderServices services;

    /**
     * Number of messages received, helps with testing.
     */
    private int receivedMessageCount;

    /**
     * Constructs the message handler.
     *
     * @param services to find models.
     */
    protected MessageHandler(PathfinderServices services) {
        this.services = services;
        this.receivedMessageCount = 0;
    }

    protected void setServices(PathfinderServices services) {
        this.services = services;
    }

    /**
     * Invoked when the web socket receives a message.
     *
     * @param message the message received as a String.
     */
    @Override
    public void onMessage(String message) {
        try {
            logger.info("Received json: " + message);
            this.receivedMessageCount++;
            JsonObject json = new JsonParser().parse(message).getAsJsonObject();

            if (!json.has("message") || !json.has("model")) {
                logger.warn("Ignoring invalid message: " + json.toString());
            } else {
                String type = json.get("message").getAsString();

                JsonObject value = json.getAsJsonObject("value");
                String tempPath = "";
                if (json.has("id")) {
                    tempPath += json.get("id").getAsString();
                } else if (json.has("clusterId")) {
                    tempPath += json.get("clusterId").getAsString();
                } else if (!json.get("model").getAsString().equals(ModelType.CLUSTER.toString())) {
                    tempPath += value.get("clusterId").getAsString();
                    tempPath += "/" + value.get("id").getAsString();
                } else {
                    tempPath += value.get("id").getAsString();
                }

                ModelType modelType = ModelType.getModelType(json.get("model").getAsString());
                logger.info("Model Type : " + modelType);

                Path path = new Path(tempPath, modelType);

                Model model = null;
                if(type.equals("Created") && !ModelType.CLUSTER.equals(modelType)) {
                    model = this.services.getRegistry().pollCreateBacklog();
                    model.setPathName(path.getPathName());
                } else {
                    model = this.services.getRegistry().getModel(path);
                }

                if(model != null) {
                    logger.info("Notifying " + model.getPathName() + " Type: " + model.getModelType() + " of message");

                    model.notifyUpdate(type, json);
                } else {
                    Path parentPath = path.getParentPath();

                    if(parentPath != null && this.services.getRegistry().isModelRegistered(parentPath)) {
                        if(modelType == ModelType.CLUSTER) {
                            Cluster.getInstance(value, this.services);
                            return;
                        } else if(modelType == ModelType.COMMODITY) {
                            Commodity.getInstance(value, this.services);
                            return;
                        } else if(modelType == ModelType.TRANSPORT) {
                            Transport.getInstance(value, this.services);
                            return;
                        }
                    }

                    logger.warn("Received message that couldn't be routed to a model: " + message);
                }
            }
        } catch (Exception e) { // catch any exception that occured while serving a message
            logger.error(e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Returns the number of message received by the web socket.
     *
     * @return number of messages received by the web socket.
     */
    public int getReceivedMessageCount() {
        return this.receivedMessageCount;
    }
}
