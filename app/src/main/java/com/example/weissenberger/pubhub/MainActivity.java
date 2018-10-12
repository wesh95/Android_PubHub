package com.example.weissenberger.pubhub;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements DailyDealsFrag.OnFragmentInteractionListener,
        MapDisplayFrag.OnFragmentInteractionListener, BarDetailsFrag.OnFragmentInteractionListener,
        SortSelect.OnFragmentInteractionListener, ServiceConnection {

    public static final String DEAL_INFO = "deal info";
    public static final String DEALS_FRAG = "daily deals fragment";
    public static final String MAP_FRAG = "map_display_fragment";
    public static final String DETAILS_FRAG = "bars details frag";
    public static String SORT_MODE;
    public static final String SORT_LOCATION = "location";
    public static final String SORT_COVER = "cover";
    public static final String SORT_WAIT = "wait";

    private Deal deal;

    private Intent startDealChangeService;
    private boolean isInitialized = false;
    private boolean isBound = false;

    private static Location mLastKnownLocation;
    private static FusedLocationProviderClient mFusedLocationProviderClient;

    private DealsChangeReceiver dealsChangeReceiver;
    private DealChangeService dealChangeService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setLogo(R.drawable.pub_hub_logo);


        // preparing the intent object that will launch the service
        startDealChangeService = new Intent(this, DealChangeService.class);

        // if not started we go ahead and start it
        if (!isInitialized) {
            startService(startDealChangeService);
            isInitialized = true;
        }

        dealsChangeReceiver = new DealsChangeReceiver(this);


        if (findViewById(R.id.fragment_container) != null) {
            if (savedInstanceState != null) {
                return;
            }
            DailyDealsFrag dailyDealsFrag = new DailyDealsFrag();
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, dailyDealsFrag, DEALS_FRAG).commit();
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle b) {
        super.onRestoreInstanceState(b);
        deal = (Deal) b.getSerializable((DEAL_INFO));
        BarDetailsFrag dFrag = (BarDetailsFrag) getSupportFragmentManager().findFragmentByTag(DETAILS_FRAG);

        if (deal != null) {
            dFrag.setDeal(deal);
            Log.d("" + (dFrag == null), "restore: ");
            dFrag.displayDealDetails(deal);
            //getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, dFrag, DETAILS_FRAG).commit();
        }
    }

    @Override
    public void onBackPressed() {
        deal = null;
    }

    @Override
    protected void onSaveInstanceState(Bundle b) {
        super.onSaveInstanceState(b);
        String test = "null";
        if (deal != null) {
            test = deal.getBarName();
        }
        Log.d(test, "onSaveInstanceState: ");
        b.putSerializable(DEAL_INFO, deal);
    }


    @Override
    protected void onPause() {
        super.onPause();
        Log.d("TEST", "onPause()");
        // unbinding from service
        // the service will have onUnbind() called after this
        // inside that method we will handle the logic of unbinding
        if (isBound) {
            unbindService(this);
            isBound = false;
        }
        //remove the broadcast receiver
        unregisterReceiver(dealsChangeReceiver);

    }

    @Override
    protected void onResume() {
        super.onResume();
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());

        //is service is initialized and not boud we bind to it
        if (isInitialized && !isBound) {
            bindService(startDealChangeService, this, Context.BIND_AUTO_CREATE);
        }

        // registering the broadcast receiver
        registerReceiver(dealsChangeReceiver, new IntentFilter(DealChangeService.UPDATE_ALL_INTENT));
        registerReceiver(dealsChangeReceiver, new IntentFilter(DealChangeService.UPDATE_SINGLE_INTENT));
        registerReceiver(dealsChangeReceiver, new IntentFilter(DealChangeService.ADD_SINGLE_INTENT));
        registerReceiver(dealsChangeReceiver, new IntentFilter(DealChangeService.REMOVE_SINGLE_INTENT));
    }


    @Override
    protected void onDestroy() {


        super.onDestroy();
    }
    //    public void addDefaultDeals() {
//        Deal sharkiesDeal = new Deal();
//        Deal totsDeal = new Deal();
//        Deal alsDeal = new Deal();
//        Deal champsDeal = new Deal();
//        Deal hokieHouse = new Deal();
//
//        sharkiesDeal.setDealTitle("$3.00 Long Island");
//        sharkiesDeal.setBarName("Sharkies");
//        sharkiesDeal.setCover(1);
//        sharkiesDeal.setDescription("SHARKIES. This is a description about the long island that sharkies has for sale. It is a wonderful deal that everyne shoudl get all the time. At this point I'm just trying to make this longer.");
//        sharkiesDeal.setWaitTime(20);
//        sharkiesDeal.setStart("1800");
//        sharkiesDeal.setDuration(120);
//        currentDeals.add(sharkiesDeal);
//
//        totsDeal.setDealTitle("$5.00 Rail");
//        totsDeal.setBarName("TOTS");
//        totsDeal.setCover(2);
//        totsDeal.setDescription("TOTS. This is a description about the long island that sharkies has for sale. It is a wonderful deal that everyne shoudl get all the time. At this point I'm just trying to make this longer.");
//        totsDeal.setWaitTime(45);
//        totsDeal.setStart("2200");
//        totsDeal.setDuration(60);
//        currentDeals.add(totsDeal);
//
//        alsDeal.setDealTitle("$2.00 Doubles");
//        alsDeal.setBarName("Big Als");
//        alsDeal.setCover(0);
//        alsDeal.setDescription("BIG ALS. This is a description about the long island that sharkies has for sale. It is a wonderful deal that everyne shoudl get all the time. At this point I'm just trying to make this longer.");
//        alsDeal.setWaitTime(0);
//        alsDeal.setStart("2030");
//        alsDeal.setDuration(180);
//        currentDeals.add(alsDeal);
//
//        champsDeal.setDealTitle("$4.00 Girl Scout Shooters");
//        champsDeal.setBarName("Champs");
//        champsDeal.setCover(0);
//        champsDeal.setDescription("CHAMPS. This is a description about the long island that sharkies has for sale. It is a wonderful deal that everyne shoudl get all the time. At this point I'm just trying to make this longer.");
//        champsDeal.setWaitTime(0);
//        champsDeal.setStart("1600");
//        champsDeal.setDuration(360);
//        currentDeals.add(champsDeal);
//
//        hokieHouse.setDealTitle("$2.00 Doubles $3.00 Tripsssssssssssles");
//        hokieHouse.setBarName("Hokie House");
//        hokieHouse.setCover(0);
//        hokieHouse.setDescription("HOKIE HOUSE. This is a description about the long island that sharkies has for sale. It is a wonderful deal that everyne shoudl get all the time. At this point I'm just trying to make this longer.");
//        hokieHouse.setStart("0800");
//        hokieHouse.setDuration(720);
//        currentDeals.add(hokieHouse);
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.toolbar_actions, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d("TEST", "IN");
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        deal = null;
        switch (item.getItemId()) {
            case R.id.action_favorite:
                DailyDealsFrag dFrag = (DailyDealsFrag) fm.findFragmentByTag(DEALS_FRAG);
                if (dFrag == null) {
                    dFrag = new DailyDealsFrag();
                }
                ft.replace(R.id.fragment_container, dFrag, DEALS_FRAG);
                //ft.addToBackStack(null);
                ft.commit();
                return true;

            case R.id.action_show_map:
                MapDisplayFrag mFrag = (MapDisplayFrag) fm.findFragmentByTag(MAP_FRAG);
                if (mFrag == null) {
                    mFrag = new MapDisplayFrag();
                }
                ft.replace(R.id.fragment_container, mFrag, MAP_FRAG);
                //ft.addToBackStack(null);
                ft.commit();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }


    public void displayDeals() {
        if (isBound) {
            dealChangeService.forceUpdate();
        }

    }

    public void updateDeals(List<Deal> newDeals) {
        Log.d("TEST", "updateDeals was called");
        if (isBound && findViewById(R.id.deal_listview) != null) {
            ListView listView = (ListView) findViewById(R.id.deal_listview);
            DealAdapter dealAdapter = (DealAdapter) listView.getAdapter();
            if (dealAdapter == null) {
                listView.setAdapter(new DealAdapter(getApplicationContext(), (ArrayList<Deal>) newDeals));
            } else {
                dealAdapter.clear();
                dealAdapter.addAll(newDeals);
            }
        } else if (isBound && findViewById(R.id.map_list_view) != null) {
            ListView listView = (ListView) findViewById(R.id.map_list_view);
            BarAdapter barAdapter = (BarAdapter) listView.getAdapter();
            if (barAdapter == null) {
                listView.setAdapter(new BarAdapter(getApplicationContext(), (ArrayList<Deal>) newDeals));
            } else {
                barAdapter.clear();
                barAdapter.addAll(newDeals);
            }
            MapDisplayFrag mFrag = (MapDisplayFrag) getSupportFragmentManager().findFragmentByTag(MAP_FRAG);
            for (Deal d : newDeals) {
                mFrag.addCoord(d);
            }

        }

    }

    public void addDeal(Deal myDeal) {
        if (isBound && findViewById(R.id.deal_listview) != null) {

            ListView listView = (ListView) findViewById(R.id.deal_listview);
            DealAdapter dealAdapter = (DealAdapter) listView.getAdapter();
            if (dealAdapter == null) {
                listView.setAdapter(new DealAdapter(getApplicationContext(), new ArrayList<Deal>()));
                dealAdapter = (DealAdapter) listView.getAdapter();

            }
            dealAdapter.add(myDeal);
        } else if (isBound && findViewById(R.id.map_list_view) != null) {

            ListView listView = (ListView) findViewById(R.id.map_list_view);
            BarAdapter barAdapter = (BarAdapter) listView.getAdapter();
            if (barAdapter == null) {
                listView.setAdapter(new BarAdapter(getApplicationContext(), new ArrayList<Deal>()));
                barAdapter = (BarAdapter) listView.getAdapter();

            }
            barAdapter.add(myDeal);
            MapDisplayFrag mFrag = (MapDisplayFrag) getSupportFragmentManager().findFragmentByTag(MAP_FRAG);
            mFrag.addCoord(myDeal);


        }
    }


    public void displayDetails(Deal deal) {
        FragmentManager fm = getSupportFragmentManager();
        BarDetailsFrag barDetailsFrag = (BarDetailsFrag) getSupportFragmentManager().findFragmentByTag(DETAILS_FRAG);
        if (barDetailsFrag == null) {
            barDetailsFrag = new BarDetailsFrag();
        }
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, barDetailsFrag, DETAILS_FRAG);
        //ft.addToBackStack(null);
        this.deal = deal;
        ft.commit();
        fm.executePendingTransactions();
        barDetailsFrag.displayDealDetails(deal);
    }


//    public boolean isFavorited() {
//        return dealChangeService.favoritesContains(deal);
//    }

    public class BarAdapter extends ArrayAdapter<Deal> {
        public BarAdapter(Context context, ArrayList<Deal> deals) {
            super(context, 0, deals);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            Deal deal = getItem(position);
            //Deal deal = getItem(position);

            if (convertView == null) {

                //the like button would need to be in single_movie.xml
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.single_bar, parent, false);
            }

            TextView barTitle = convertView.findViewById(R.id.bar_name_mapfrag);
            barTitle.setText(deal.getBarName());

            TextView waitTime = convertView.findViewById(R.id.bar_wait_time_mapfrag);
            waitTime.setText("Wait Time: " + deal.getWaitTime() + " minutes");

            TextView cover = convertView.findViewById(R.id.bar_cover_mapfrag);
            cover.setText("Cover: $" + deal.getCover() + ".00");

            return convertView;
        }
    }

    public class DealAdapter extends ArrayAdapter<Deal> {
        public DealAdapter(Context context, ArrayList<Deal> deals) {
            super(context, 0, deals);
        }


        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            Deal deal = getItem(position);
            //Deal deal = getItem(position);

            if (convertView == null) {

                //the like button would need to be in single_movie.xml
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.single_deal, parent, false);
            }

            TextView dealTitle = convertView.findViewById(R.id.deal_title);
            dealTitle.setText(deal.getDealTitle());

            TextView dealTimeActive = convertView.findViewById(R.id.deal_time_active);
            dealTimeActive.setText("Available: " + deal.getStart() + " - " + deal.getEnd());

            ImageView dealImage = convertView.findViewById(R.id.deal_image);
            if (deal.getImageData() !=null){
                dealImage.setImageBitmap(deal.getImageData());
            }else {
                new GetImage(dealImage, deal)
                        .execute(deal.getImage());
            }
            TextView barName = convertView.findViewById(R.id.bar_name);
            barName.setText(deal.getBarName());

            TextView waitTime = convertView.findViewById(R.id.bar_wait_time);
            waitTime.setText("Wait Time: " + deal.getWaitTime() + " minutes");

            TextView cover = convertView.findViewById(R.id.bar_cover);
            cover.setText("Cover: $" + deal.getCover() + ".00");

            return convertView;
        }
    }

    public Deal getDeal() {
        return this.deal;
    }


    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        Log.d("TEST", "onServiceConnected()");

        // the biner object gets us an object that we use to extract a reference to service
        DealChangeService.MyBinder binder = (DealChangeService.MyBinder) iBinder;

        // extracting the service object
        dealChangeService = binder.getService();

        // it is bound so we set the boolean
        isBound = true;
        dealChangeService.forceUpdate();
    }

    public boolean getIsBound() {
        return isBound;
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        Log.d("TEST", "onServiceDisconnected()");
        dealChangeService = null;
        isBound = false;
    }

    public void updateDeal(Deal myDeal) {
        if (isBound && findViewById(R.id.deal_listview) != null) {

            ListView listView = (ListView) findViewById(R.id.deal_listview);
            DealAdapter dealAdapter = (DealAdapter) listView.getAdapter();
            if (dealAdapter == null) {
                listView.setAdapter(new DealAdapter(getApplicationContext(), new ArrayList<Deal>()));
                dealAdapter = (DealAdapter) listView.getAdapter();

            }
            int index = dealAdapter.getPosition(myDeal);
            dealAdapter.remove(myDeal);
            dealAdapter.insert(myDeal, index);
        } else if (isBound && findViewById(R.id.map_list_view) != null) {

            ListView listView = (ListView) findViewById(R.id.map_list_view);
            BarAdapter barAdapter = (BarAdapter) listView.getAdapter();
            if (barAdapter == null) {
                listView.setAdapter(new BarAdapter(getApplicationContext(), new ArrayList<Deal>()));
                barAdapter = (BarAdapter) listView.getAdapter();

            }
            int index = barAdapter.getPosition(myDeal);
            barAdapter.remove(myDeal);
            barAdapter.insert(myDeal, index);
        }
    }


    public void removeDeal(Deal myDeal) {
        if (isBound && findViewById(R.id.deal_listview) != null) {

            ListView listView = (ListView) findViewById(R.id.deal_listview);
            DealAdapter dealAdapter = (DealAdapter) listView.getAdapter();
            if (dealAdapter == null) {
                listView.setAdapter(new DealAdapter(getApplicationContext(), new ArrayList<Deal>()));
                dealAdapter = (DealAdapter) listView.getAdapter();

            }
            dealAdapter.remove(myDeal);
        } else if (isBound && findViewById(R.id.map_list_view) != null) {

            ListView listView = (ListView) findViewById(R.id.map_list_view);
            BarAdapter barAdapter = (BarAdapter) listView.getAdapter();
            if (barAdapter == null) {
                listView.setAdapter(new BarAdapter(getApplicationContext(), new ArrayList<Deal>()));
                barAdapter = (BarAdapter) listView.getAdapter();

            }
            barAdapter.remove(myDeal);
        }
    }

    public Comparator<Deal> getSortComparator() {
        switch (SORT_MODE) {
            case SORT_LOCATION:
                return new Comparator<Deal>() {
                    @Override
                    public int compare(Deal d1, Deal d2) {
                        Location a = new Location("");//provider name is unnecessary
                        a.setLatitude(d1.getLatitude());//your coords of course
                        a.setLongitude(d1.getLongitude());

                        Location b = new Location("");//provider name is unnecessary
                        b.setLatitude(d2.getLatitude());//your coords of course
                        b.setLongitude(d2.getLongitude());

                        return Double.compare(mLastKnownLocation.distanceTo(a), mLastKnownLocation.distanceTo(b));   //or whatever your sorting algorithm
                    }
                };
            case SORT_COVER:
                return new Comparator<Deal>() {
                    @Override
                    public int compare(Deal d1, Deal d2) {
                        return Double.compare(d1.getCover(), d2.getCover());   //or whatever your sorting algorithm
                    }
                };
            case SORT_WAIT:
                return new Comparator<Deal>() {
                    @Override
                    public int compare(Deal d1, Deal d2) {
                        return Double.compare(d1.getWaitTime(), d2.getWaitTime());   //or whatever your sorting algorithm

                    }
                };
        }
        return null;
    }


    private void locationGet() {
  /*
   * Get the best and most recent location of the device, which may be null in rare
   * cases when a location is not available.
   */
        try {
            Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
            locationResult.addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    if (task.isSuccessful()) {
                        // Set the map's camera position to the current location of the device.
                        mLastKnownLocation = task.getResult();
                        sortDeals();
                    }
                }
            });

        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    @Override
    public void onSortChanged(String uri) {
        SORT_MODE = uri;
        sortDeals();
    }

    public void sortDeals() {
        if (mLastKnownLocation == null) {
            locationGet();
        } else if (isBound && findViewById(R.id.deal_listview) != null) {
            ListView listView = (ListView) findViewById(R.id.deal_listview);
            DealAdapter dealAdapter = (DealAdapter) listView.getAdapter();
            dealAdapter.sort(getSortComparator());
        } else if (isBound && findViewById(R.id.map_list_view) != null) {
            ListView listView = (ListView) findViewById(R.id.map_list_view);
            BarAdapter barAdapter = (BarAdapter) listView.getAdapter();
            barAdapter.sort(getSortComparator());
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
