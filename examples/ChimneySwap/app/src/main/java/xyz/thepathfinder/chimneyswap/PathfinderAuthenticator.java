package xyz.thepathfinder.chimneyswap;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

import xyz.thepathfinder.android.AuthenticationListener;

public class PathfinderAuthenticator extends AuthenticationListener {

    private Activity activity;

    public PathfinderAuthenticator(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void authenticationFailed(String reason) {
        Runnable task = new Runnable() {
            @Override
            public void run() {
                AlertDialog dialog = new AlertDialog.Builder(PathfinderAuthenticator.this.activity)
                        .setMessage("Pathfinder Authentication Failed.")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        }).create();
                dialog.show();
            }
        };
        this.activity.runOnUiThread(task);
    }
}
