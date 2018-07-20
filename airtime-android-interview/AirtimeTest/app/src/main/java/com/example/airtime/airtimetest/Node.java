package com.example.airtime.airtimetest;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * Created by dominic barone on 9/8/2016.
 */
public class Node
{
    public String mRoomId;
    public ArrayList<Node> mChildren;
    public int mMessageOrder;
    public String mMessage;
    public boolean mVisited;
    public boolean mExploring;

    public Node(String id)
    {
        mRoomId = id;
        mChildren = new ArrayList<Node>();
        mMessageOrder = 0;
        mVisited = false;
        mExploring = false;
    }
}

// Comparator to sort Node writings so we can /report the complete message
class MessageOrderComparator implements Comparator<Node> {
    public int compare(Node node1, Node node2) {
        return node1.mMessageOrder - node2.mMessageOrder;
    }
}
