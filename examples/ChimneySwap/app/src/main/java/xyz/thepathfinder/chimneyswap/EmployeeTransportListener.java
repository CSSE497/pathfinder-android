package xyz.thepathfinder.chimneyswap;

import android.os.Handler;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;

import xyz.thepathfinder.android.Route;
import xyz.thepathfinder.android.Transport;
import xyz.thepathfinder.android.TransportListener;
import xyz.thepathfinder.android.TransportStatus;

public class EmployeeTransportListener extends TransportListener {

    private static final String TAG = "EmployeeListener";

    private EmployeeActivity activity;
    private GoogleMap map;
    private Handler handler;

    public EmployeeTransportListener(EmployeeActivity activity, GoogleMap map, Handler handler) {
        this.activity = activity;
        this.map = map;
        this.handler = handler;
    }

    public void created(Transport transport) {
        this.activity.saveEmployeeId(transport.getPathName());
    }

    @Override
    public void routed(final Route route) {
        Log.i(TAG, "Routed Employee");

        this.handler.post(new Runnable() {
            public void run() {
                EmployeeTransportListener.this.activity.setRoute(route);
                if (route.getActions().size() > 1) {
                    new RouteHandler(EmployeeTransportListener.this.activity, EmployeeTransportListener.this.map).getDirections(route);
                } else {
                    EmployeeTransportListener.this.map.clear();
                }
            }
        });
    }

    @Override
    public void statusUpdated(TransportStatus status) {
        if (status == TransportStatus.ONLINE) {
            this.handler.post(new Runnable() {
                public void run() {
                    EmployeeTransportListener.this.activity.onlineColor();
                }
            });
        }

        if (status == TransportStatus.OFFLINE) {
            this.handler.post(new Runnable() {
                public void run() {
                    EmployeeTransportListener.this.activity.offlineColor();
                    EmployeeTransportListener.this.map.clear();
                }
            });
        }
    }
}
