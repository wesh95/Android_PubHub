package com.example.weissenberger.pubhub;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Travis on 12/14/2017.
 */

public class DealChangeService extends Service implements ValueEventListener, ChildEventListener {
    public static final String REMOVE_SINGLE_INTENT = "removed deal";
    public static final String UPDATE_ALL_INTENT = "updated all deals";
    public static final String ADD_SINGLE_INTENT = "added single deal";
    public static final String UPDATE_SINGLE_INTENT = "updated single deal";


    public static String DEALS_LIST = "deals list";
    public static String NEW_DEAL = "new deal";
    public static String UPDATED_DEAL = "updated deal";
    public static String REMOVED_DEAL = "removed deal";


    public static ArrayList<String> favorites;

    private DatabaseReference mDatabase;

    private final IBinder iBinder = new MyBinder();

    public DealChangeService() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        //mDatabase.child("Deals").addListenerForSingleValueEvent(this);

    }

    @Override
    public void onCreate() {
        super.onCreate();
        mDatabase.child("Deals").addChildEventListener(this);
        favorites = new ArrayList<String>();
        //favorites.add("Sharkeys");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return iBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }



    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        Log.d("TEST", "in datachange");
        List<Deal> myDeals = new ArrayList<>();
        if (dataSnapshot.exists()) {
            for (DataSnapshot deal : dataSnapshot.getChildren()) {
                Deal myDeal = deal.getValue(Deal.class);
                myDeal.setKey(deal.getKey());

                myDeals.add(myDeal);

            }
            Intent intent = new Intent(UPDATE_ALL_INTENT);

            intent.putExtra(DEALS_LIST,(Serializable)myDeals);

            sendBroadcast(intent);
        }


    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        if (dataSnapshot.exists()) {
            Deal myDeal = dataSnapshot.getValue(Deal.class);
            myDeal.setKey(dataSnapshot.getKey());
            if (favorites.contains(myDeal.getBarName())) {
                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
                mBuilder.setSmallIcon(R.drawable.long_island);
                mBuilder.setContentTitle("New Deal at " + myDeal.getBarName());
                mBuilder.setContentText(myDeal.getDescription());
                NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                mNotificationManager.notify(0, mBuilder.build());
            }
            Intent intent = new Intent(ADD_SINGLE_INTENT);
            intent.putExtra(NEW_DEAL,(Serializable)myDeal);

            sendBroadcast(intent);
        }


    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
        if (dataSnapshot.exists()) {
            Deal myDeal = dataSnapshot.getValue(Deal.class);
            myDeal.setKey(dataSnapshot.getKey());
            Intent intent = new Intent(UPDATE_SINGLE_INTENT);
            intent.putExtra(UPDATED_DEAL,(Serializable)myDeal);

            sendBroadcast(intent);
        }
    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {
        if (dataSnapshot.exists()) {
            Deal myDeal = dataSnapshot.getValue(Deal.class);
            myDeal.setKey(dataSnapshot.getKey());
            Intent intent = new Intent(REMOVE_SINGLE_INTENT);
            intent.putExtra(REMOVED_DEAL,(Serializable)myDeal);

            sendBroadcast(intent);
        }
    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }

    public void forceUpdate() {
        mDatabase.child("Deals").addListenerForSingleValueEvent(this);
    }

    public class MyBinder extends Binder {
        DealChangeService getService() {
            return DealChangeService.this;
        }
    }
}
