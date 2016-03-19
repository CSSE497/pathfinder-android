package xyz.thepathfinder.www.chimneyswap;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import xyz.thepathfinder.android.Action;
import xyz.thepathfinder.android.ActionStatus;
import xyz.thepathfinder.android.Route;

public class RouteHandler {

    private static final String DIRECTIONS_API_BASE = "https://maps.googleapis.com/maps/api/directions/json";

    private String TAG = "DirectionsTask";
    private EmployeeActivity activity;
    private final GoogleMap map;
    private List<MarkerOptions> markers;

    public RouteHandler(EmployeeActivity activity, GoogleMap map) {
        this.activity = activity;
        this.map = map;
    }

    private String makeUrl(List<Action> actions) {
        String url = DIRECTIONS_API_BASE;

        try {
            if (actions.size() > 0) {
                url += "?origin=" + URLEncoder.encode(actions.get(0).getLatitude() + "," + actions.get(0).getLongitude(), "utf8");
            }

            if (actions.size() > 1) {
                url += "&destination=" + URLEncoder.encode(actions.get(actions.size() - 1).getLatitude() + "," + actions.get(actions.size() - 1).getLongitude(), "utf8");
            }

            String waypoints = "";
            for (int k = 1; k < actions.size() - 1; k++) {
                waypoints += actions.get(k).getLatitude() + "," + actions.get(k).getLongitude();
                if (k != actions.size() - 2) {
                    waypoints += "|";
                }
            }
            url += "&waypoints=" + URLEncoder.encode(waypoints, "utf8");
            url += "&key=" + EmployeeActivity.MAPS_API_KEY;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return url;
    }

    public void getDirections(Route route) {
        List<Action> actions = route.getActions();
        this.markers = new ArrayList<>();

        BitmapDescriptor color = BitmapDescriptorFactory.defaultMarker(200);

        for(int k = 1; k < actions.size(); k++) {
            Action action = actions.get(k);
            LatLng position = new LatLng(action.getLatitude(), action.getLongitude());

            String title = "";
            if(action.getStatus() == ActionStatus.DROP_OFF) {
                title = "Drop off";
            } else if(action.getStatus() == ActionStatus.PICK_UP) {
                title = "Pick Up";
            }

            MarkerOptions marker = new MarkerOptions().position(position).title(title);

            marker.icon(color);

            this.markers.add(marker);
        }

        LatLng position = new LatLng(actions.get(0).getLatitude(), actions.get(0).getLongitude());
        MarkerOptions transportMarker = new MarkerOptions()
                .position(position)
                .title(actions.get(0).getStatus().toString())
                .icon(BitmapDescriptorFactory.defaultMarker(5));
        this.markers.add(transportMarker);

        String url = this.makeUrl(actions);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JsonObject pathJson = new JsonParser().parse(response).getAsJsonObject();
                        displayRoutes(pathJson);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        Volley.newRequestQueue(activity).add(stringRequest);

    }

    private void displayRoutes(JsonObject pathJson) {
        final ArrayList<ArrayList<LatLng>> paths = new ArrayList<>();

        JsonObject route = pathJson.getAsJsonArray("routes").get(0).getAsJsonObject();
        JsonObject bounds = route.getAsJsonObject("bounds");
        LatLng maxSouthwest = this.getLatLng(bounds.getAsJsonObject("southwest"));
        LatLng maxNortheast = this.getLatLng(bounds.getAsJsonObject("northeast"));

        JsonArray legs = route.getAsJsonArray("legs");
        ArrayList<LatLng> stops = new ArrayList<>();
        int size = legs.size();
        for (int k = 0; k < size; k++) {
            JsonArray steps = legs.get(k).getAsJsonObject().getAsJsonArray("steps");
            for (JsonElement stepElement : steps) {
                JsonObject step = stepElement.getAsJsonObject();
                JsonObject start = step.getAsJsonObject("start_location");
                JsonObject end = step.getAsJsonObject("end_location");
                stops.add(this.getLatLng(start));
                stops.add(this.getLatLng(end));
            }
        }

        paths.add(stops);

        Log.w(TAG, paths.toString());

        LatLngBounds cameraBounds = new LatLngBounds(maxSouthwest, maxNortheast);
        this.map.clear();

        for (MarkerOptions marker : markers) {
            this.map.addMarker(marker);
        }

        this.map.moveCamera(CameraUpdateFactory.newLatLngBounds(cameraBounds, 100));

        for (ArrayList<LatLng> stopLocations : paths) {
            this.map.addPolyline(new PolylineOptions().add(stopLocations.toArray(new LatLng[stopLocations.size()])));
        }
    }

    private LatLng getLatLng(JsonObject location) {
        double lat = location.get("lat").getAsDouble();
        double lng = location.get("lng").getAsDouble();
        return new LatLng(lat, lng);
    }
}
