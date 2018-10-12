package com.example.weissenberger.pubhub;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Travis on 12/14/2017.
 */

public class DealsChangeReceiver extends BroadcastReceiver {
    static String TAG = "BroadCastReceiver";
    private MainActivity mainActivity;
    // constructor takes a reference to main activity so we can communicate with it
    public DealsChangeReceiver(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        //Extract the list of deals and send it to mainactivity
        if (intent.getAction().equals(DealChangeService.ADD_SINGLE_INTENT)){
            Deal myDeal = (Deal) intent.getSerializableExtra(DealChangeService.NEW_DEAL);
            Log.d(TAG, "SINGLE");
            mainActivity.addDeal(myDeal);
        }else if(intent.getAction().equals(DealChangeService.UPDATE_ALL_INTENT)){
            Log.d(TAG, "MULTI");

            List<Deal> mydeals = (ArrayList<Deal>) intent.getSerializableExtra(DealChangeService.DEALS_LIST);
            mainActivity.updateDeals(mydeals);
        }else if(intent.getAction().equals(DealChangeService.UPDATE_SINGLE_INTENT)) {
            Log.d(TAG, "UPDATE");

            Deal myDeal= (Deal) intent.getSerializableExtra(DealChangeService.UPDATED_DEAL);
            mainActivity.updateDeal(myDeal);
        }else if(intent.getAction().equals(DealChangeService.REMOVE_SINGLE_INTENT)) {
            Log.d(TAG, "REMOVE");

            Deal myDeal= (Deal) intent.getSerializableExtra(DealChangeService.REMOVED_DEAL);
            mainActivity.removeDeal(myDeal);
        }
    }

}

