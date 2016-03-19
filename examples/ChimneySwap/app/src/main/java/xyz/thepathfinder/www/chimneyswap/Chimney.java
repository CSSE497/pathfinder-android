package xyz.thepathfinder.www.chimneyswap;

import android.graphics.Bitmap;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.JsonObject;

public class Chimney {

    private int id;
    private String name;
    private String imageUrl;
    private Bitmap image;
    private LatLng position;

    public Chimney() {
        this.id = 0;
        this.name = null;
        this.imageUrl = null;
        this.image = null;
        this.position = null;
    }

    public Chimney(JsonObject json) {
        this.id = json.get("id").getAsInt();
        this.name = json.get("name").getAsString();
        JsonObject position = json.getAsJsonObject("position");
        double lat = position.get("lat").getAsDouble();
        double lng = position.get("lng").getAsDouble();

        this.position = new LatLng(lat, lng);
        this.imageUrl = json.get("image").getAsString();
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LatLng getPosition() {
        return this.position;
    }

    public void setPosition(LatLng position) {
        this.position = position;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Bitmap getImage() {
        return this.image;
    }

    public void setImage(Bitmap bitmap) {
        this.image = bitmap;
    }

    public String getImageUrl() {
        return this.imageUrl;
    }

    public void setImageUrl(String url) {
        this.imageUrl = url;
    }
}
