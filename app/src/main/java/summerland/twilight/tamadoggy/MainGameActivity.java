package summerland.twilight.tamadoggy;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainGameActivity extends AppCompatActivity implements
        MainFragment.OnFragmentInteractionListener,
        WalkFragment.OnFragmentInteractionListener,
        ItemsFragment.OnFragmentInteractionListener,
        StoreFragment.OnFragmentInteractionListener{

    SharedPreferences m_sPref;
    Database m_db;

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    int m_valHunger, m_valFitness, m_valHygiene, m_valFun, m_nextUpdate, m_cash;
    HashMap<Integer, Const.Items> m_itemsMaps;
    Date m_lastDate;
    Location m_curLocation;

    Fragment m_fragmentMain;
    Fragment m_fragmentWalk;
    Fragment m_fragmentShop;
    Fragment m_fragmentInventory;


    LocationManager m_locationManager;
    LocationListener m_locationListener;

    Handler handleStat;
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            m_valHunger = m_valHunger -2;
            m_valFun = m_valFun - 3;
            m_valFitness = m_valFitness -2;
            m_valHygiene = m_valHygiene -1;
            m_lastDate = new Date();
            ((MainFragment) m_fragmentMain).setProgress(m_valFitness, m_valFun, m_valHygiene, m_valHunger, m_cash);
            ((StoreFragment) m_fragmentShop).updateCash(m_cash);
//            handleStat.postDelayed(this, TimeUnit.MINUTES.toMillis(1));
            handleStat.postDelayed(this, TimeUnit.HOURS.toMillis(1));
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_game);
        m_sPref = getSharedPreferences(Const.SHARED_BASIC_GAME_DATA, Context.MODE_PRIVATE);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation_game);


        m_locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        m_locationListener = new MyLocationListener();
        if(checkLocationPermission())
        {
            m_locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, m_locationListener);
        }
        m_db = new Database(this);
        ArrayList<Const.CurrentItems> currentItems = getCurrentItems();
        ArrayList<Const.Items> storeItems = new ArrayList<>();
        m_itemsMaps = getItems();
        storeItems.addAll(m_itemsMaps.values());
        final FragmentManager fragmentManager = getSupportFragmentManager();
        m_fragmentMain = new MainFragment();
        m_fragmentWalk = new WalkFragment();
        m_fragmentInventory = ItemsFragment.newInstance(currentItems);
        m_fragmentShop = StoreFragment.newInstance(storeItems, m_cash);
        FragmentTransaction fragmentTransactionHome = fragmentManager.beginTransaction();
        fragmentTransactionHome.replace(R.id.fragmentHolder, m_fragmentMain);
        fragmentTransactionHome.commit();

        bottomNav.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch(item.getItemId())
                        {
                            case R.id.action_home:
                                FragmentTransaction fragmentTransactionHome = fragmentManager.beginTransaction();
                                fragmentTransactionHome.replace(R.id.fragmentHolder, m_fragmentMain);
                                fragmentTransactionHome.commit();
                                break;
                            case R.id.action_inventory:
                                FragmentTransaction fragmentTransactionInvent = fragmentManager.beginTransaction();
                                fragmentTransactionInvent.replace(R.id.fragmentHolder, m_fragmentInventory);
                                fragmentTransactionInvent.commit();
                                break;
                            case R.id.action_shop:
                                FragmentTransaction fragmentTransactionShop = fragmentManager.beginTransaction();
                                fragmentTransactionShop.replace(R.id.fragmentHolder, m_fragmentShop);
                                fragmentTransactionShop.commit();
                                break;
                            case R.id.action_train:
                                break;
                            case R.id.action_walk:
                                if(m_curLocation == null){
                                    m_curLocation = getLastKnownLocation();
                                }
                                if(m_curLocation != null)
                                {
                                    Bundle args = new Bundle();
                                    args.putDouble("LAT", m_curLocation.getLatitude());
                                    args.putDouble("LONG", m_curLocation.getLongitude());
                                    m_fragmentWalk.setArguments(args);
                                }
                                FragmentTransaction fragmentTransactionWalk = fragmentManager.beginTransaction();
                                fragmentTransactionWalk.replace(R.id.fragmentHolder, m_fragmentWalk);
                                fragmentTransactionWalk.commit();
                                break;
                        }
                        return true;
                    }
        });
    }

    private ArrayList<Const.CurrentItems> getCurrentItems()
    {
        Cursor cursor = m_db.getData(Const.databaseView.CURRENT_ITEMS);
        cursor.moveToFirst();
        ArrayList<Const.CurrentItems> currentItems = new ArrayList <Const.CurrentItems>();
        while (!cursor.isAfterLast())
        {
            String name = cursor.getString(cursor.getColumnIndex(Const.ITEM_NAME));
            int itemId, itemFun, itemFitness, itemHygiene, itemAmount, itemHunger;
            itemId = cursor.getInt(cursor.getColumnIndex(Const.CURRENT_ITEMS_ID));
            itemAmount = cursor.getInt(cursor.getColumnIndex(Const.CURRENT_ITEMS_AMOUNT));
            itemFun = cursor.getInt(cursor.getColumnIndex(Const.ITEM_FUN));
            itemFitness = cursor.getInt(cursor.getColumnIndex(Const.ITEM_FITNESS));
            itemHunger = cursor.getInt(cursor.getColumnIndex(Const.ITEM_HUNGER));
            itemHygiene = cursor.getInt(cursor.getColumnIndex(Const.ITEM_HYGIENE));
            cursor.moveToNext();
            Const.CurrentItems curItem = new Const.CurrentItems();
            curItem.hunger = itemHunger;
            curItem.fun = itemFun;
            curItem.fitness = itemFitness;
            curItem.hygiene = itemHygiene;
            curItem.amount = itemAmount;
            curItem.id = itemId;
            curItem.itemName = name;
            if(itemAmount > 0)
            {
                currentItems.add(curItem);
            }
        }
        return currentItems;
    }
    private Const.CurrentItems getCurrentItem(int id)
    {
        Cursor cursor = m_db.getSpecificCurrentItem(Const.databaseView.SPECIFIC_CURRENT_ITEM, id);
        Const.CurrentItems item = null;
        cursor.moveToFirst();
        while(!cursor.isAfterLast())
        {
            String name = cursor.getString(cursor.getColumnIndex(Const.ITEM_NAME));
            int itemId, itemFun, itemFitness, itemHygiene, itemAmount, itemHunger;
            itemId = cursor.getInt(cursor.getColumnIndex(Const.CURRENT_ITEMS_ID));
            itemAmount = cursor.getInt(cursor.getColumnIndex(Const.CURRENT_ITEMS_AMOUNT));
            itemFun = cursor.getInt(cursor.getColumnIndex(Const.ITEM_FUN));
            itemFitness = cursor.getInt(cursor.getColumnIndex(Const.ITEM_FITNESS));
            itemHunger = cursor.getInt(cursor.getColumnIndex(Const.ITEM_HUNGER));
            itemHygiene = cursor.getInt(cursor.getColumnIndex(Const.ITEM_HYGIENE));
            cursor.moveToNext();
            Const.CurrentItems curItem = new Const.CurrentItems();
            curItem.hunger = itemHunger;
            curItem.fun = itemFun;
            curItem.fitness = itemFitness;
            curItem.hygiene = itemHygiene;
            curItem.amount = itemAmount;
            curItem.id = itemId;
            curItem.itemName = name;
            item = curItem;
        }
        return item;
    }
    private HashMap<Integer, Const.Items> getItems()
    {
        Cursor cursor = m_db.getData(Const.databaseView.ALL_ITEMS);
        cursor.moveToFirst();
        HashMap<Integer, Const.Items> items = new HashMap<Integer, Const.Items>();
        while (!cursor.isAfterLast())
        {
            String name = cursor.getString(cursor.getColumnIndex(Const.ITEM_NAME));
            int itemId, itemFun, itemFitness, itemHygiene, itemHunger, itemCost;
            itemId = cursor.getInt(cursor.getColumnIndex(Const.UID));
            itemFun = cursor.getInt(cursor.getColumnIndex(Const.ITEM_FUN));
            itemFitness = cursor.getInt(cursor.getColumnIndex(Const.ITEM_FITNESS));
            itemHunger = cursor.getInt(cursor.getColumnIndex(Const.ITEM_HUNGER));
            itemHygiene = cursor.getInt(cursor.getColumnIndex(Const.ITEM_HYGIENE));
            itemCost = cursor.getInt(cursor.getColumnIndex(Const.ITEM_COST));
            cursor.moveToNext();
            Const.Items curItem = new Const.Items();
            curItem.cost = itemCost;
            curItem.hunger = itemHunger;
            curItem.fun = itemFun;
            curItem.fitness = itemFitness;
            curItem.hygiene = itemHygiene;
            curItem.id = itemId;
            curItem.itemName = name;
            items.put(curItem.id, curItem);
        }
        return items;
    }
    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public void updateGPS(double lat, double lon){

    }
    @Override
    protected void onResume(){
        super.onResume();
        m_valHunger = m_sPref.getInt(Const.SHARED_HUNGER, -1);
        m_valFitness = m_sPref.getInt(Const.SHARED_FITNESS, -1);
        m_valHygiene = m_sPref.getInt(Const.SHARED_HYGIENE, -1);
        m_valFun = m_sPref.getInt(Const.SHARED_FUN, -1);
        m_cash = m_sPref.getInt(Const.SHARED_CASH, -1);
        m_lastDate = new Date(m_sPref.getLong(Const.SHARED_DATE_LAST_GAME_UPDATE, 0));
        m_nextUpdate = calculateStatValue();

        ((MainFragment) m_fragmentMain).setProgress(m_valFitness, m_valFun, m_valHygiene, m_valHunger, m_cash);
        ((StoreFragment) m_fragmentShop).updateCash(m_cash);
        handleStat = new Handler();
        //handleStat.postDelayed(runnable, TimeUnit.MINUTES.toMillis(1));
        handleStat.postDelayed(runnable, TimeUnit.MINUTES.toMillis(m_nextUpdate));
    }
    @Override
    protected void onPause(){
        super.onPause();
        SharedPreferences.Editor editor = m_sPref.edit();
        editor.putLong(Const.SHARED_DATE_LAST_GAME_UPDATE, m_lastDate.getTime());
        editor.putInt(Const.SHARED_FUN, m_valFun);
        editor.putInt(Const.SHARED_HYGIENE, m_valHygiene);
        editor.putInt(Const.SHARED_FITNESS, m_valFitness);
        editor.putInt(Const.SHARED_HUNGER, m_valHunger);
        editor.putInt(Const.SHARED_CASH, m_cash);
        editor.commit();
    }
    private Location getLastKnownLocation() {
        List<String> providers = m_locationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            Location l = null;
            if(checkLocationPermission())
            {
                l = m_locationManager.getLastKnownLocation(provider);
            }
            if (l == null) {
                continue;
            }
            if (bestLocation == null
                    || l.getAccuracy() < bestLocation.getAccuracy()) {
                bestLocation = l;
            }
        }
        if (bestLocation == null) {
            return null;
        }
        return bestLocation;
    }
    private int calculateStatValue() {
        Date currDate = new Date();
        long timeDiff = currDate.getTime() - m_lastDate.getTime();
        int intTimeDiff = (int) TimeUnit.MILLISECONDS.toMinutes(timeDiff);
        if (intTimeDiff >= 60) {
            m_valHunger = m_valHunger - (2 * (intTimeDiff / 60));
            m_valFun = m_valFun - (3 * (intTimeDiff / 60));
            m_valFitness = m_valFitness - (2 * (intTimeDiff / 60));
            m_valHygiene = m_valHygiene - (1 * (intTimeDiff / 60));

            m_lastDate = new Date(m_lastDate.getTime() + TimeUnit.HOURS.toMillis(intTimeDiff / 60));
        }
        return 59 - (intTimeDiff % 60); //remaining time until next update;
    }
    public void useItem(int id, int amount){
        ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(150);
        Const.Items item = m_itemsMaps.get(id);
        m_valHunger = m_valHunger + item.hunger;
        m_valFitness = m_valFitness + item.fitness;
        m_valFun = m_valFun + item.fun;
        m_valHygiene = m_valHygiene + item.hygiene;
        ((MainFragment) m_fragmentMain).setProgress(m_valFitness, m_valFun, m_valHygiene, m_valHunger, m_cash);
        m_db.saveItems(id, amount);

    }

    public void buyItem(int id, int cost)
    {
        ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(150);
        int currentCash = m_cash - cost;
        if(currentCash < 0)
        {
            Toast.makeText(this,"Not Enough money to purchase the item!", Toast.LENGTH_LONG).show();
        }
        else{
            m_cash = currentCash;
            ((StoreFragment) m_fragmentShop).updateCash(m_cash);
            Const.CurrentItems currentItem = getCurrentItem(id);
            //Const.Items item = m_itemsMaps.get(id);
            int currentAmount = 1;
            if(currentItem != null)
            {
                currentAmount = currentItem.amount + currentAmount;
                currentItem.amount = currentAmount;
            }
            m_db.saveItems(id, currentAmount);
            m_fragmentInventory = ItemsFragment.newInstance(getCurrentItems());
        }

    }
    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Request Location")
                        .setMessage("Tamadoggy needs to know your location")
                        .setPositiveButton("Grant Permission", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(MainGameActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }
    public class MyLocationListener implements LocationListener{

        public void onLocationChanged(Location location) {
            m_curLocation = location;
        }
        public void onProviderDisabled(String arg0) {

        }
        public void onProviderEnabled(String provider) {

        }
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        //Request location updates:
                        m_locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, m_locationListener);
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                }
                return;
            }

        }
    }

}
