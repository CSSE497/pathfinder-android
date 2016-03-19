package xyz.thepathfinder.www.chimneyswap;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;

public class TradeChimneyActivity extends AppCompatActivity {

    private static final String TAG = "TradeChimneyActivtiy";
    public static final String CHIMNEY_SWAP_URL = "http://chimneyswap.xyz";
    public static final String TRADED_CHIMNEY = "traded_chimney";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trade_chimney);

        String url = TradeChimneyActivity.CHIMNEY_SWAP_URL + "/chimneys";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JsonArray json = new JsonParser().parse(response).getAsJsonArray();
                        createChimneyList(json);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        Volley.newRequestQueue(this).add(stringRequest);
    }

    private void createChimneyList(JsonArray json) {
        final List<Chimney> chimneys = new ArrayList<>();
        for(JsonElement e: json) {
            chimneys.add(new Chimney(e.getAsJsonObject()));
        }

        final List<Bitmap> images = new ArrayList<>();
        for(final Chimney chimney : chimneys) {
            ImageRequest imageRequest = new ImageRequest(chimney.getImageUrl(), new Response.Listener<Bitmap>() {

                @Override
                public void onResponse(Bitmap image) {
                    Log.w(TAG, "Image received: " + chimney.getImageUrl());
                    chimney.setImage(image);
                    images.add(image);
                    if(chimneys.size() == images.size()) {
                        displayChimneyList(chimneys);
                    }
                }}, 200, 200, null,
                    new Response.ErrorListener() {
                        public void onErrorResponse(VolleyError e) {e.printStackTrace(); }
                    }
            );
            Volley.newRequestQueue(this).add(imageRequest);
        }


    }

    private void displayChimneyList(List<Chimney> chimneys) {
        ListView list = (ListView) this.findViewById(R.id.chimney_list);
        list.setAdapter(new ChimneyAdapter(this, chimneys));
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Chimney chimney = (Chimney) parent.getAdapter().getItem(position);
                Intent intent = new Intent(TradeChimneyActivity.this, PostChimneyActivity.class);
                intent.putExtra(TradeChimneyActivity.TRADED_CHIMNEY, new ParcelableChimney(chimney));
                TradeChimneyActivity.this.startActivity(intent);
            }
        });
    }
}
