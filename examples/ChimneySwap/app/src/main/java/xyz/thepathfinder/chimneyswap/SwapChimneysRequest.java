package xyz.thepathfinder.chimneyswap;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.JsonObject;

import java.io.IOException;

import xyz.thepathfinder.android.Cluster;
import xyz.thepathfinder.android.CommodityStatus;
import xyz.thepathfinder.android.Pathfinder;

public class SwapChimneysRequest {

    private Chimney chimney1;
    private Chimney chimney2;
    private String appId;

    public SwapChimneysRequest(String appId, Chimney chimney1, Chimney chimney2) {
        this.chimney1 = chimney1;
        this.chimney2 = chimney2;
        this.appId = appId;
    }

    public void swap() {
        Pathfinder pathfinder = Pathfinder.create(this.appId);
        Cluster defaultCluster = pathfinder.getCluster(SelectionActivity.cluster);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("chimney", 1);

        Chimney chimney1 = SwapChimneysRequest.this.chimney1;
        LatLng c1Position = chimney1.getPosition();

        Chimney chimney2 = SwapChimneysRequest.this.chimney2;
        LatLng c2Position = chimney2.getPosition();

        defaultCluster.createCommodity(
                c1Position.latitude,
                c1Position.longitude,
                c2Position.latitude,
                c2Position.longitude,
                CommodityStatus.WAITING,
                jsonObject).create();

        defaultCluster.createCommodity(
                c2Position.latitude,
                c2Position.longitude,
                c1Position.latitude,
                c1Position.longitude,
                CommodityStatus.WAITING,
                jsonObject).create();

    }
}
