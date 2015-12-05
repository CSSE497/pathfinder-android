package xyz.thepathfinder.android;

import com.google.gson.JsonObject;

public interface PathfinderListener {
    public void update(JsonObject json);
}
