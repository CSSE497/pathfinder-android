package xyz.thepathfinder.chimneyswap;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PostChimneyActivity extends AppCompatActivity {

    private static final int READ_REQUEST_CODE = 4254; // just some random number
    private static final String TAG = "PostChimneyActivity";
    private Chimney tradedChimney;
    private Bitmap chimneyImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_chimney);

        ParcelableChimney parcelableChimney = getIntent().getParcelableExtra(TradeChimneyActivity.TRADED_CHIMNEY);
        this.tradedChimney = parcelableChimney.getChimney();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        if(requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if(resultData != null) {
                Uri uri = resultData.getData();
                getBitmapFromUri(uri);
            }
        }
    }

    private void getBitmapFromUri(Uri uri) {
        try {
            ParcelFileDescriptor parcelFileDescriptor =
                    getContentResolver().openFileDescriptor(uri, "r");
            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
            this.chimneyImage = BitmapFactory.decodeFileDescriptor(fileDescriptor);
            parcelFileDescriptor.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ImageView imageView = (ImageView) this.findViewById(R.id.chimney_icon);
        imageView.setImageBitmap(Bitmap.createScaledBitmap(this.chimneyImage, 200, 200, false));

        Button button = (Button) this.findViewById(R.id.add_chimney_image);
        button.setVisibility(View.GONE);

        imageView.setVisibility(View.VISIBLE);
    }

    private void postChimney(final String name, final String address, Bitmap image) {
        String url = TradeChimneyActivity.CHIMNEY_SWAP_URL + "/chimney";

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] imageBytes = baos.toByteArray();

        final String encodeImage = Base64.encodeToString(imageBytes, Base64.URL_SAFE);

        JSONObject json = new JSONObject();
        JSONObject imageJson = new JSONObject();
        try {
            imageJson.put("file_data", encodeImage);
            json.put("image", imageJson);
            json.put("name", name);
            json.put("address", address);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new EasyJsonObjectRequest(Request.Method.POST, url, json, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                getChimneyList();
            }
        });

        Volley.newRequestQueue(this).add(request);
    }

    public void onClickPickImage(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        intent.setType("image/*");
        startActivityForResult(intent, READ_REQUEST_CODE);
    }

    public void onClickPostChimney(View view) {
        ImageView imageView = (ImageView) this.findViewById(R.id.chimney_icon);
        Bitmap image = ((BitmapDrawable)imageView.getDrawable()).getBitmap();

        EditText nameEditText = (EditText) this.findViewById(R.id.chimney_name_edit_text);
        EditText addressEditText = (EditText) this.findViewById(R.id.chimney_address_edit_text);

        postChimney(nameEditText.getText().toString(), addressEditText.getText().toString(), image);
    }

    public void getChimneyList() {
        String url = TradeChimneyActivity.CHIMNEY_SWAP_URL + "/chimneys";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JsonArray json = new JsonParser().parse(response).getAsJsonArray();
                        swapChimneys(json);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        Volley.newRequestQueue(this).add(stringRequest);
    }

    public void swapChimneys(JsonArray jsonArray) {
        List<Chimney> chimneyList = new ArrayList<Chimney>();
        for(JsonElement chimney : jsonArray) {
            chimneyList.add(new Chimney(chimney.getAsJsonObject()));
        }

        int maxId = -1;
        int maxIndex = -1;
        for (int k = 0; k < chimneyList.size(); k++) {
            Chimney chimney = chimneyList.get(k);
            if (chimney.getId() > maxId) {
                maxId = chimney.getId();
                maxIndex = k;
            }
        }

        Chimney myChimney = chimneyList.get(maxIndex);

        SwapChimneysRequest chimneysRequest = new SwapChimneysRequest(getString(R.string.pathfinder_app_id), this.tradedChimney, myChimney);
        chimneysRequest.swap();

        this.deleteChimney(this.tradedChimney.getId());
        this.deleteChimney(myChimney.getId());

        try {
            Thread.sleep(400);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Intent intent = new Intent(this, SelectionActivity.class);
        this.startActivity(intent);
    }

    private void deleteChimney(int id) {
        String url = TradeChimneyActivity.CHIMNEY_SWAP_URL + "/chimney?id=" + id;

        StringRequest stringRequest = new StringRequest(Request.Method.DELETE, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        Volley.newRequestQueue(this).add(stringRequest);
    }
}
