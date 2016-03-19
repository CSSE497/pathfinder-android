package xyz.thepathfinder.www.chimneyswap;

import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class EasyJsonObjectRequest extends JsonObjectRequest {

    private static final String TAG = "EasyJsonObjectRequest";

    public EasyJsonObjectRequest(int method, String url, JSONObject jsonRequest, Response.Listener<JSONObject> listener) {
        super(method, url, jsonRequest, listener, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, error.toString());
            }
        });
    }

    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            Log.e(TAG, jsonString);
            if (jsonString.length() == 0) {
                return Response.success(null, HttpHeaderParser.parseCacheHeaders(response));
            }
            return Response.success(new JSONObject(jsonString), HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JSONException je) {
            return Response.error(new ParseError(je));
        }
    }
}
