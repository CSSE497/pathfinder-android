package xyz.thepathfinder.chimneyswap;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RadioButton;

import xyz.thepathfinder.chimneyswap.R;
import xyz.thepathfinder.chimneyswap.TradeChimneyActivity;

public class SelectionActivity extends AppCompatActivity {

    private final String TAG = "SelectionActivity";
    public static String cluster;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection);
        RadioButton terreHauteButton = (RadioButton) this.findViewById(R.id.terre_haute);
        terreHauteButton.setChecked(true);
        SelectionActivity.cluster = "/root";
    }

    public void onClickEmployee(View view) {
        Intent intent = new Intent(this, EmployeeActivity.class);
        startActivity(intent);
    }

    public void onClickCustomer(View view) {
        Intent intent = new Intent(this, TradeChimneyActivity.class);
        startActivity(intent);
    }

    public void onClickChangeCluster(View view) {
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.midwest:
                if (checked) {
                    SelectionActivity.cluster = "/root";
                }
                break;
            case R.id.terre_haute:
                if (checked) {
                    SelectionActivity.cluster = "/root";
                }
                break;
        }

    }
}
