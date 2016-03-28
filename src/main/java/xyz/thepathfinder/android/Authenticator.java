package xyz.thepathfinder.android;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.glassfish.tyrus.core.Base64Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Handles all of the initial authentication sequence. The Authenticator is listenable with
 * {@link AuthenticationListener}s. The methods available in the {@link AuthenticationListener}
 * class allow developers to know whether or not the user was successfully authenticated.
 */
public class Authenticator extends Listenable<AuthenticationListener, JsonObject> {

    /**
     * Logs actions performed by the class.
     */
    private static final Logger logger = LoggerFactory.getLogger(Authenticator.class);

    /**
     * Application Identifier for the application accessing the pathfinder server.
     */
    private final String applicationIdentifier;

    /**
     * String version of a JWT for the user's credentials.
     */
    private final String userCredential;

    /**
     * URL of the server that is responsible for authenticating the users.
     */
    private final String authenticationServerURL;

    /**
     * Access to the web socket connection.
     */
    private final PathfinderServices services;

    /**
     * Creates an authenticator responsible for authenticating the user.
     *
     * @param applicationIdentifier   for the current application.
     * @param userCredential          JWT as a String.
     * @param authenticationServerURL URL of the server that authenticates the user.
     * @param services                a pathfinder services object.
     */
    protected Authenticator(String applicationIdentifier, String userCredential, String authenticationServerURL, PathfinderServices services) {
        this.applicationIdentifier = applicationIdentifier;
        this.userCredential = userCredential;
        this.authenticationServerURL = authenticationServerURL;
        this.services = services;
    }

    /**
     * Sends a post request to the authentication server with the connection id
     * received from the pathfinder server to authenticate the user.
     *
     * @param connectionId received from the pathfinder server.
     */
    protected void sendToAuthenticationServer(final String connectionId) {
        logger.info("Creating authentication post request");
        Runnable task = new Runnable() {
            @Override
            public void run() {
                try {
                    logger.info("Starting authentication post request");
                    String query = String.format("?connection_id=%s&application_id=%s&id_token=%s",
                            URLEncoder.encode(connectionId, StandardCharsets.UTF_8.name()),
                            URLEncoder.encode(applicationIdentifier, StandardCharsets.UTF_8.name()),
                            URLEncoder.encode(userCredential, StandardCharsets.UTF_8.name()));
                    URL url = new URL(authenticationServerURL + query);
                    String protocol = url.getProtocol();
                    if (!(protocol.equals("http") || protocol.equals("https"))) {
                        Authenticator.this.authenticationFailed("Unknown protocol found: " + protocol + ", only http and https are accepted");
                        return;
                    }
                    HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
                    httpConnection.setRequestMethod("POST");
                    httpConnection.setDoOutput(true);
                    httpConnection.setConnectTimeout(10000);
                    logger.info("Ending authentication post request");

                    logger.info("Authentication post request response code: " + httpConnection.getResponseCode());
                    if (httpConnection.getResponseCode() == HttpURLConnection.HTTP_NO_CONTENT) {
                        String[] jwtParts = userCredential.split("\\.");
                        if (jwtParts.length != 3) {
                            Authenticator.this.authenticationFailed("Invalid user credentials: JWT invalid format");
                            return;
                        }

                        byte[] bytesPayload = Base64Utils.decodeFast(jwtParts[1]);
                        String payload = new String(bytesPayload, StandardCharsets.UTF_8);
                        JsonObject jsonPayload = new JsonParser().parse(payload).getAsJsonObject();
                        if (jsonPayload.has("email")) {
                            String email = jsonPayload.get("email").getAsString();

                            JsonObject json = new JsonObject();
                            json.addProperty("message", "Authenticate");
                            json.addProperty("value", email);
                            Authenticator.this.services.getConnection().sendAuthenticationMessage(json.toString());
                        } else {
                            Authenticator.this.authenticationFailed("JWT does not contain email");
                        }
                    } else {
                        Authenticator.this.authenticationFailed("Authentication post request failed: " + httpConnection.getResponseCode());
                    }
                } catch (IOException e) {
                    Authenticator.this.authenticationFailed("IOException occured: " + e.getMessage());
                }
            }
        };

        ExecutorService executors = Executors.newSingleThreadExecutor();
        executors.execute(task);
    }

    /**
     * Tells the listeners that the authentication sequence failed.
     *
     * @param reason of why the authentication sequence failed.
     */
    private void authenticationFailed(String reason) {
        logger.error(reason);
        for (AuthenticationListener listener : Authenticator.this.getListeners()) {
            listener.authenticationFailed(reason);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean notifyUpdate(String reason, JsonObject json) {
        if (reason.equals("ConnectionId")) {
            logger.info("Received connection id.");
            this.sendToAuthenticationServer(json.get("id").getAsString());
            return false;
        }

        if (reason.equals("Authenticated")) {
            logger.info("User authenticated successfully, switching to model message handler.");
            for (AuthenticationListener listener : this.getListeners()) {
                listener.authenticationSuccessful();
            }
            this.services.getConnection().setMessageHandler(new ModelMessageHandler(this.services));

            return false;
        }
        return false;
    }
}
