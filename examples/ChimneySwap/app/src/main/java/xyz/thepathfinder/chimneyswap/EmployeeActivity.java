package xyz.thepathfinder.chimneyswap;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.JsonObject;

import java.util.List;

import xyz.thepathfinder.android.Action;
import xyz.thepathfinder.android.ActionStatus;
import xyz.thepathfinder.android.Cluster;
import xyz.thepathfinder.android.Pathfinder;
import xyz.thepathfinder.android.Route;
import xyz.thepathfinder.android.Transport;
import xyz.thepathfinder.android.TransportStatus;
import xyz.thepathfinder.chimneyswap.R;

public class EmployeeActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, OnMapReadyCallback {

    public static String MAPS_API_KEY;

    private GoogleApiClient googleApiClient;
    private LatLng startLocation;
    private GoogleMap map;
    protected volatile Transport employeeTransport;
    private volatile Route route;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee);

        this.offlineColor();

        MAPS_API_KEY = getString(R.string.google_maps_key);

        TextView actionText = (TextView) findViewById(R.id.action_text);
        Button actionButton = (Button) findViewById(R.id.action_button);
        actionText.setText("No Action");
        actionButton.setEnabled(false);

        this.googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        this.googleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        //Location location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        this.startLocation = new LatLng(44.32, -122.2131);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng terreHaute = new LatLng(39.469, -87.3898);

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(terreHaute, 12));

        Handler handler = new Handler();

        SharedPreferences preferences = this.getSharedPreferences(MainActivity.PREFERENCES_FILE, Context.MODE_PRIVATE);
        String employeeId = preferences.getString(MainActivity.EMPLOYEE_ID + SelectionActivity.cluster, "");
        String idToken = preferences.getString(MainActivity.ID_TOKEN, "");

        Pathfinder pathfinder = new Pathfinder(getString(R.string.pathfinder_app_id), idToken);
        pathfinder.connect(false);
        Transport transport;
        if(employeeId.equals("")) { // didn't find the employee's id
            Cluster cluster = pathfinder.getCluster(SelectionActivity.cluster);
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("chimney", 5);
            transport = cluster.createTransport(this.startLocation.latitude, this.startLocation.longitude, TransportStatus.OFFLINE, jsonObject);
            transport.addListener(new EmployeeTransportListener(this, map, handler));
            transport.create();
        } else {
            transport = pathfinder.getTransport(employeeId);
            transport.updateStatus(TransportStatus.OFFLINE);
            transport.updateLocation(this.startLocation.latitude, this.startLocation.longitude);
            transport.addListener(new EmployeeTransportListener(this, map, handler));
        }
        transport.routeSubscribe();
        this.employeeTransport = transport;
    }

    public void saveEmployeeId(String id) {
        SharedPreferences preferences = this.getSharedPreferences(MainActivity.PREFERENCES_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(MainActivity.EMPLOYEE_ID + SelectionActivity.cluster, id);
        editor.apply();
    }

    public void onlineColor() {
        Button button = (Button) findViewById(R.id.employee_online);
        button.setTextColor(Color.parseColor("#2196f3"));
        Button button2 = (Button) findViewById(R.id.employee_offline);
        button2.setTextColor(Color.parseColor("#000000"));
    }

    public void onClickOnline(View view) {
        if(this.employeeTransport != null) {
            this.employeeTransport.updateStatus(TransportStatus.ONLINE);
        }
    }

    public void onClickOffline(View view) {
        if(this.employeeTransport != null) {
            this.employeeTransport.updateStatus(TransportStatus.OFFLINE);
        }
    }

    public void offlineColor() {
        Button button = (Button) findViewById(R.id.employee_offline);
        button.setTextColor(Color.parseColor("#2196f3"));
        Button button2 = (Button) findViewById(R.id.employee_online);
        button2.setTextColor(Color.parseColor("#000000"));
    }

    public void setRoute(Route route) {
        this.route = route;
        TextView actionText = (TextView) findViewById(R.id.action_text);
        Button actionButton = (Button) findViewById(R.id.action_button);
        if (this.employeeTransport.getStatus() == TransportStatus.ONLINE && route.getActions().size() > 1) {
            List<Action> actions = route.getActions();
            Action nextAction = actions.get(1);

            String text = "";
            if (nextAction.getStatus() == ActionStatus.PICK_UP) {
                text = "Pick Up";
            } else if(nextAction.getStatus() == ActionStatus.DROP_OFF) {
                text = "Drop off";
            }
            actionText.setText(text);
            actionButton.setEnabled(true);
        } else {
            actionText.setText("No Action");
            actionButton.setEnabled(false);
        }
    }

    public void onClickCompleteAction(View view) {
        List<Action> actions = this.route.getActions();
        if(actions.size() > 1) {
            Action action = actions.get(1);
            if(action.getStatus() == ActionStatus.PICK_UP) {
                action.getCommodity().updatePickedUp(this.employeeTransport);
            } else if(action.getStatus() == ActionStatus.DROP_OFF) {
                action.getCommodity().updateDroppedOff();
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
