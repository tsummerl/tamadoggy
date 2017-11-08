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

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainGameActivity extends AppCompatActivity implements
        MainFragment.OnFragmentInteractionListener,
        WalkFragment.OnFragmentInteractionListener,
        ItemsFragment.OnFragmentInteractionListener{

    SharedPreferences sPref;
    Database db;

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    int valHunger, valFitness, valHygiene, valFun, nextUpdate;
    Date lastDate;
    Location curLocation;

    Fragment fragmentMain;
    Fragment fragmentWalk;
    Fragment fragmentShop;
    Fragment fragmentInventory;


    LocationManager locationManager;
    LocationListener locationListener;

    Handler handleStat;
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            valHunger = valHunger -2;
            valFun = valFun - 3;
            valFitness = valFitness -2;
            valHygiene = valHygiene -1;
            lastDate = new Date();
//            handleStat.postDelayed(this, TimeUnit.MINUTES.toMillis(1));
            handleStat.postDelayed(this, TimeUnit.HOURS.toMillis(1));
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_game);
        sPref = getSharedPreferences(Const.SHARED_BASIC_GAME_DATA, Context.MODE_PRIVATE);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation_game);


        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        locationListener = new MyLocationListener();
        if(checkLocationPermission())
        {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }

        final FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentMain = new MainFragment();
        fragmentWalk = new WalkFragment();
        fragmentInventory = new ItemsFragment();
        FragmentTransaction fragmentTransactionHome = fragmentManager.beginTransaction();
        fragmentTransactionHome.replace(R.id.fragmentHolder, fragmentMain);
        fragmentTransactionHome.commit();

        bottomNav.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch(item.getItemId())
                        {
                            case R.id.action_home:
                                FragmentTransaction fragmentTransactionHome = fragmentManager.beginTransaction();
                                fragmentTransactionHome.replace(R.id.fragmentHolder, fragmentMain);
                                fragmentTransactionHome.commit();
                                break;
                            case R.id.action_inventory:
                                FragmentTransaction fragmentTransactionInvent = fragmentManager.beginTransaction();
                                fragmentTransactionInvent.replace(R.id.fragmentHolder, fragmentInventory);
                                fragmentTransactionInvent.commit();
                                break;
                            case R.id.action_shop:
                                break;
                            case R.id.action_train:
                                break;
                            case R.id.action_walk:
                                if(curLocation == null){
                                    curLocation = getLastKnownLocation();
                                }
                                if(curLocation != null)
                                {
                                    Bundle args = new Bundle();
                                    args.putDouble("LAT", curLocation.getLatitude());
                                    args.putDouble("LONG", curLocation.getLongitude());
                                    fragmentWalk.setArguments(args);
                                }
                                FragmentTransaction fragmentTransactionWalk = fragmentManager.beginTransaction();
                                fragmentTransactionWalk.replace(R.id.fragmentHolder, fragmentWalk);
                                fragmentTransactionWalk.commit();
                                break;
                        }
                        return true;
                    }
        });
        db = new Database(this);
        Cursor cursor = db.getData(Const.databaseView.CURRENT_ITEMS);
        cursor.moveToFirst();
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
            ((ItemsFragment) fragmentInventory).addItemToView(curItem);
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public void updateGPS(double lat, double lon){

    }
    @Override
    protected void onResume(){
        super.onResume();
        valHunger = sPref.getInt(Const.SHARED_HUNGER, -1);
        valFitness = sPref.getInt(Const.SHARED_FITNESS, -1);
        valHygiene = sPref.getInt(Const.SHARED_HYGIENE, -1);
        valFun = sPref.getInt(Const.SHARED_FUN, -1);
        lastDate = new Date(sPref.getLong(Const.SHARED_DATE_LAST_GAME_UPDATE, 0));
        nextUpdate = calculateStatValue();

        ((MainFragment) fragmentMain).setProgress(valFitness, valFun, valHygiene, valHunger);
        handleStat = new Handler();
        //handleStat.postDelayed(runnable, TimeUnit.MINUTES.toMillis(1));
        handleStat.postDelayed(runnable, TimeUnit.MINUTES.toMillis(nextUpdate));
    }
    @Override
    protected void onPause(){
        super.onPause();
        SharedPreferences.Editor editor = sPref.edit();
        editor.putLong(Const.SHARED_DATE_LAST_GAME_UPDATE, lastDate.getTime());
        editor.putInt(Const.SHARED_FUN, valFun);
        editor.putInt(Const.SHARED_HYGIENE, valHygiene);
        editor.putInt(Const.SHARED_FITNESS, valFitness);
        editor.putInt(Const.SHARED_HUNGER, valHunger);
        editor.commit();
    }
    private Location getLastKnownLocation() {
        List<String> providers = locationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            Location l = null;
            if(checkLocationPermission())
            {
                l = locationManager.getLastKnownLocation(provider);
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
        long timeDiff = currDate.getTime() - lastDate.getTime();
        int intTimeDiff = (int) TimeUnit.MILLISECONDS.toMinutes(timeDiff);
        if (intTimeDiff >= 60) {
            valHunger = valHunger - (2 * (intTimeDiff / 60));
            valFun = valFun - (3 * (intTimeDiff / 60));
            valFitness = valFitness - (2 * (intTimeDiff / 60));
            valHygiene = valHygiene - (1 * (intTimeDiff / 60));

            lastDate = new Date(lastDate.getTime() + TimeUnit.HOURS.toMillis(intTimeDiff / 60));
        }
        return 59 - (intTimeDiff % 60); //remaining time until next update;
    }
    public void useItem(){
        ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(150);
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
            curLocation = location;
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
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
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
