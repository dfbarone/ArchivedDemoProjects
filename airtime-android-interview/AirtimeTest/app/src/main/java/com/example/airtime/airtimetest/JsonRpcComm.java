package com.example.airtime.airtimetest;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by dominic barone on 9/8/2016.
 */
public class JsonRpcComm {

    public interface VolleyCallback{
        void onSuccess(JSONObject result);
    }

    private static final String CLASSTAG = JsonRpcComm.class.getName();
    private static final String EMAIL = "jabberwookie@gmail.com";

    private Context mContext;
    private RequestQueue mRequestQueue;

    // Simple message pump to make async requests synchronous for drones
    private Integer mRequestCount = 0;
    private ConcurrentHashMap<Integer, JSONObject> mResponseMap = new ConcurrentHashMap<>();

    public JsonRpcComm(Context context) {
        mContext = context;
        mRequestQueue = Volley.newRequestQueue(mContext);
    }

    // Get function for /start web method
    public void get(String url, final VolleyCallback callback) {
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        callback.onSuccess(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }) {
            @Override
            public Map getHeaders() throws AuthFailureError {
                Map headers = new HashMap();
                headers.put("x-commander-email", EMAIL);

                return headers;
            }
        };
        mRequestQueue.add(jsonRequest);
    }

    // Post function for /drone commands and /report
    public Integer post(String url, final String data) {

        // Update pass through value
        final Integer requestNum = mRequestCount++;

        JSONObject obj = null;
        try {
            obj = new JSONObject(data);
        } catch (JSONException ex) { }

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url, obj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        mResponseMap.put(requestNum, response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        mResponseMap.put(requestNum, new JSONObject());
                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<>();
                params.put("data",data);

                return params;
            }
            @Override
            public Map getHeaders() throws AuthFailureError {
                Map headers = new HashMap();
                headers.put("x-commander-email", EMAIL);
                headers.put("Content-Type", "application/json; charset=utf-8");

                return headers;
            }
        };
        mRequestQueue.add(jsonRequest);
        return requestNum;
    }

    public JSONObject waitPost(Integer requestNum) {
        JSONObject result = null;
        while (result == null) {
            if (mResponseMap.containsKey(requestNum)) {
                result = mResponseMap.get(requestNum);
                mResponseMap.remove(requestNum);
            }
            sleep(200);
        }
        return result;
    }

    public static void sleep(int sleep){
        try {
            Thread.sleep(sleep);
        } catch (InterruptedException ex) { }
    }
}
