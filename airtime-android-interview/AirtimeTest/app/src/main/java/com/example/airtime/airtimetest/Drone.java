package com.example.airtime.airtimetest;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by dominic barone on 9/8/2016.
 */
public class Drone implements Runnable {

    private static final String CLASSTAG = Drone.class.getName();

    public String mId;
    public String mSimpleId;
    // Reference to parent controller
    private DroneManager mDroneManager;

    public Drone (String id, String simpleId, DroneManager droneManager) {
        mId = id;
        mSimpleId = simpleId;
        mDroneManager = droneManager;
    }

    // Explore a room
    private void explore(Node parentNode)
    {
        String command = String.format("{ \"<commandId>\" : { \"explore\" : \"%1s\" } }", parentNode.mRoomId);
        Integer requestNumber = mDroneManager.mJSONComm.post(mDroneManager.URL + "/drone/" + mId + "/commands", command);
        JSONObject result = mDroneManager.mJSONComm.waitPost(requestNumber);
        handleConnections(parentNode, result);
    }

    // Handle connections received from the above explore
    private void handleConnections(Node parentNode, final JSONObject result) {
        try {
            if (result.has("<commandId>")) {
                JSONObject commandObj = result.getJSONObject("<commandId>");
                if (commandObj.has("connections")) {
                    JSONArray connections = commandObj.getJSONArray("connections");
                    ArrayList<Node> newNodes = new ArrayList<>();
                    for(int i = 0; i < connections.length(); i++) {
                        String s = (String)connections.get(i);
                        Node newNode = new Node(s);
                        if (!mDroneManager.mNodeMap.containsKey(s)) {
                            newNodes.add(newNode);
                            mDroneManager.mNodeMap.put(s, newNode);
                            Log.d(CLASSTAG, "d" + mSimpleId + " room found..." + s);
                            mDroneManager.log("d" + mSimpleId + " room found..." + s);
                        } else {
                            newNodes.add(mDroneManager.mNodeMap.get(s));
                        }
                    }

                    // If discovered for the first time
                    if (parentNode.mChildren.size() == 0) {
                        parentNode.mChildren = newNodes;
                        Log.d(CLASSTAG, "d" + mSimpleId + " room explored..." + parentNode.mRoomId);
                        mDroneManager.log("d" + mSimpleId + " room explored..." + parentNode.mRoomId);
                    }
                }
            }
        } catch (JSONException ex) { }
    }

    // Read message in a room
    private void read(Node parentNode)
    {
        String command = String.format("{ \"<commandId>\":{\"read\":\"%1s\"} }", parentNode.mRoomId);
        Integer requestNumber = mDroneManager.mJSONComm.post(mDroneManager.URL + "/drone/" + mId + "/commands", command);
        JSONObject result = mDroneManager.mJSONComm.waitPost(requestNumber);
        handleWritings(parentNode, result);
    }

    // Handle messages received from the above read
    private void handleWritings(Node parentNode, final JSONObject result) {
        try {
            if (result.has("<commandId>")) {
                JSONObject commandObj = result.getJSONObject("<commandId>");

                String writing = "";
                int order = 0;
                if (commandObj.has("writing")) {
                    writing = commandObj.getString("writing");
                }

                if (commandObj.has("order")) {
                    order = Integer.parseInt(commandObj.getString("order"));
                    if (order < 0)
                        return;
                }

                parentNode.mMessage =
                        mDroneManager.mNodeMap.get(parentNode.mRoomId).mMessage = writing;
                parentNode.mMessageOrder =
                        mDroneManager.mNodeMap.get(parentNode.mRoomId).mMessageOrder = order;

                Log.d(CLASSTAG, "d" + mSimpleId + " room read..." + writing + ".." + order);
                mDroneManager.log("d" + mSimpleId + " room read..." + writing + ".." + order);
            }
        } catch (JSONException ex) { }
    }

    private void depthFirstSearch(Node parentNode)
    {
        // Search new rooms
        if (!parentNode.mVisited) {
            parentNode.mVisited =
                    mDroneManager.mNodeMap.get(parentNode.mRoomId).mVisited = true;
            parentNode.mExploring =
                    mDroneManager.mNodeMap.get(parentNode.mRoomId).mExploring = true;

            // Batching orders via JSON do not seem to be working, so they are separate for now.
            explore(parentNode);
            read(parentNode);

            parentNode.mExploring =
                    mDroneManager.mNodeMap.get(parentNode.mRoomId).mExploring = false;
        }
        else if (parentNode.mExploring && parentNode.mChildren.size() == 0 ) {

            // If you are a drone going into a room being explored, wait for the room to complete
            // being explored before you proceed.
            while (parentNode.mExploring == true) {
                Log.d(CLASSTAG, "drone d" + mSimpleId + " waiting for work");
                mDroneManager.log("drone d" + mSimpleId + " waiting for work");
                JsonRpcComm.sleep(200);
            }
        }

        // While children are unvisited, continue your search
        for (Node childNode : parentNode.mChildren) {
            if (!childNode.mVisited) {
                depthFirstSearch(childNode);
            }
        }
    }

    public void run() {

        // Explore the caverns
        depthFirstSearch(mDroneManager.mRootNode);

        // run /report
        mDroneManager.report(mSimpleId);
    }
}
