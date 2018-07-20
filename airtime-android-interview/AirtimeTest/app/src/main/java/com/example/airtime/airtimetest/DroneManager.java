package com.example.airtime.airtimetest;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by dominic barone on 9/8/2016.
 */
public class DroneManager implements Runnable {

    public interface GenericCallback{
        void onSuccess(String result);
    }

    private static final String CLASSTAG = DroneManager.class.getName();
    public static final String URL = "http://challenge2.airtime.com:10001";

    private Context mContext;
    public Node mRootNode = new Node("");
    private ArrayList<Drone> mDrones = new ArrayList<>();
    public ConcurrentHashMap<String, Node> mNodeMap = new ConcurrentHashMap<>();
    public String mSuccessMessage;

    // Class to make JSON REST calls
    public JsonRpcComm mJSONComm;

    public DroneManager(Context context) {
        mContext = context;
        mJSONComm = new JsonRpcComm(mContext);
    }

    private void start(final GenericCallback callback)
    {
        mJSONComm.get(URL + "/start", new JsonRpcComm.VolleyCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                handleStart(result);
                callback.onSuccess("");
            }
        });
    }

    private void handleStart(JSONObject result) {
        try {
            // dead in the water if initial web call fails
            if (result == null)
                return;

            mSuccessMessage = "";
            mNodeMap.clear();

            if (result.has("roomId")) {
                String roomId = result.getString("roomId");
                mRootNode = new Node(roomId);
                mNodeMap.put(mRootNode.mRoomId, mRootNode);
                Log.d(CLASSTAG, "root node..." + mRootNode.mRoomId);
                log("root node..." + mRootNode.mRoomId);
            }

            if (result.has("drones")) {
                mDrones.clear();
                JSONArray droneNameList = result.getJSONArray("drones");
                for(int i = 0; i < droneNameList.length(); i++) {
                    String droneId = (String)droneNameList.get(i);
                    Drone d = new Drone(droneId, Integer.toString(i), this);
                    mDrones.add(d);
                    Log.d(CLASSTAG, "drone...d" + i + " " + droneId);
                    log("drone...d" + i + " " + droneId);
                }
            }
        } catch(JSONException ex) {

        }
    }

    public void report(String simpleDroneId)
    {
        if (mSuccessMessage != null &&
                mSuccessMessage.toLowerCase().contains("success"))
            return;

        ArrayList<Node> msgList = new ArrayList<>();
        for (Node n : mNodeMap.values())
        {
            if (n.mMessageOrder != -1 && n.mMessage != null && !n.mMessage.isEmpty() )
                msgList.add(n);
        }

        Collections.sort(msgList, new MessageOrderComparator());

        String message = "";
        for (Node n : msgList) {
            message += n.mMessage;
        }

        String command = String.format("{ \"message\":\"%1s\" }", message);
        Integer requestNumber = mJSONComm.post(URL + "/report", command);
        JSONObject result = mJSONComm.waitPost(requestNumber);

        try {
            mSuccessMessage = result.getString("response");
            Log.d(CLASSTAG, "d" + simpleDroneId + " " + mSuccessMessage);

            if (mSuccessMessage.toLowerCase().contains("success")) {
                log("d" + simpleDroneId + " " + mSuccessMessage);
            }
        } catch (JSONException ex) {}
    }

    // Log function for MainActivity to override for output
    public void log(String output) {

    }

    public void run() {
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);

        // Get marching orders
        start(new GenericCallback() {
            @Override
            public void onSuccess(String result) {
                // Go exploring
                ExecutorService executor = Executors.newFixedThreadPool(mDrones.size());
                for (int i = 0; i < mDrones.size(); i++) {
                    executor.execute(mDrones.get(i));
                }
                executor.shutdown();
            }
        });
    }
}
