package xyz.thepathfinder.android;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.glassfish.tyrus.core.Base64Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//TODO comment
public class Authenticator extends Listenable<AuthenticationListener, JsonObject>{

    /**
     * Logs actions performed by the class.
     */
    private static final Logger logger = LoggerFactory.getLogger(Authenticator.class);

    private final String applicationIdentifier;
    private final String userCredential;
    private final String authenticationServerURL;
    private final PathfinderServices services;

    protected Authenticator(String applicationIdentifier, String userCredential, String authenticationServerURL, PathfinderServices services) {
        this.applicationIdentifier = applicationIdentifier;
        this.userCredential = userCredential;
        this.authenticationServerURL = authenticationServerURL;
        this.services = services;
    }

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
                    if(!(protocol.equals("http") || protocol.equals("https"))) {
                        logger.error("Unknown protocol found: " + protocol + ", only http and https are accepted");
                        Authenticator.this.authenticationFailed();
                        return;
                    }
                    HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
                    httpConnection.setRequestMethod("POST");
                    httpConnection.setDoOutput(true);
                    httpConnection.setConnectTimeout(10000);
                    logger.info("Ending authentication post request");

                    logger.info("Authentication post request response code: " + httpConnection.getResponseCode());
                    if(httpConnection.getResponseCode() == HttpURLConnection.HTTP_NO_CONTENT) {
                        String[] jwtParts = userCredential.split("\\.");
                        if(jwtParts.length != 3) {
                            logger.error("Invalid user credentials: JWT invalid format");
                            Authenticator.this.authenticationFailed();
                            return;
                        }

                        byte[] bytesPayload = Base64Utils.decodeFast(jwtParts[1]);
                        String payload = new String(bytesPayload, StandardCharsets.UTF_8);
                        JsonObject jsonPayload =  new JsonParser().parse(payload).getAsJsonObject();
                        if(jsonPayload.has("email")) {
                            String email = jsonPayload.get("email").getAsString();

                            JsonObject json = new JsonObject();
                            json.addProperty("message", "Authenticate");
                            json.addProperty("value", email);
                            Authenticator.this.services.getConnection().sendAuthenticationMessage(json.toString());
                        } else {
                            logger.error("JWT does not contain email");
                            Authenticator.this.authenticationFailed();
                        }
                    } else {
                        logger.error("Authentication post request failed: " + httpConnection.getResponseCode());
                        Authenticator.this.authenticationFailed();
                    }
                } catch (IOException e) {
                    logger.error(e.getMessage());
                    Authenticator.this.authenticationFailed();
                }
            }
        };

        ExecutorService executors = Executors.newSingleThreadExecutor();
        executors.execute(task);
    }

    private void authenticationFailed() {
        for(AuthenticationListener listener : Authenticator.this.getListeners()) {
            listener.authenticationFailed();
        }
    }

    @Override
    protected boolean notifyUpdate(String reason, JsonObject json) {
        if(reason.equals("ConnectionId")) {
            logger.info("Received connection id.");
            this.sendToAuthenticationServer(json.get("id").getAsString());
            return false;
        }

        if(reason.equals("Authenticated")) {
            logger.info("User authenticated successfully, switching to model message handler.");
            for(AuthenticationListener listener: this.getListeners()) {
                listener.authenticationSuccessful();
            }
            this.services.getConnection().setMessageHandler(new ModelMessageHandler(this.services));

            return false;
        }
        return false;
    }
}
