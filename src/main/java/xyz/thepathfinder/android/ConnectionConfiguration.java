package xyz.thepathfinder.android;

import javax.websocket.ClientEndpointConfig;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ConnectionConfiguration extends ClientEndpointConfig.Configurator {

    private final String applicationIdentifier;

    public ConnectionConfiguration(String applicationIdentifier) {
        this.applicationIdentifier = applicationIdentifier;
    }

    @Override
    public void beforeRequest(Map<String, List<String>> header) {
        header.put("Authorization", Arrays.asList(this.applicationIdentifier));
    }
}
