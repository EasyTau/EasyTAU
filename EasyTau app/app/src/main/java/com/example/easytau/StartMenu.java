package com.example.easytau;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.Window;
import android.graphics.Typeface;
import android.widget.ImageButton;
import android.widget.Button;

import com.google.android.gms.ads.formats.NativeAd;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * Created by hanim on 21/12/2016.
 */

public class StartMenu extends AppCompatActivity {
    private static final int REQUEST_CHECK_SETTINGS = 0x1;
    private static GoogleApiClient mGoogleApiClient;
    private static final int ACCESS_FINE_LOCATION_INTENT_ID = 3;
    private static final String BROADCAST_ACTION = "android.location.PROVIDERS_CHANGED";

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    private ImageButton getParking;
    private ImageButton getMap;
    private ImageButton getPhones;
    private ImageButton nearMe;
    private ImageButton getCalendar;
    private ImageButton getCoursesNavigation;
    private ImageButton info;
    private int hasClickedPhones;
    private int hasClickedCourses;
    private int hasClickedEvents;





    private FileReader readFromFiles(String fileType) {
        FileReader fileReader = null;
        try {
            if (fileType.equals("Phones")) {
                fileReader = new FileReader(new File(getFilesDir(), "tauPhones.json"));

            } else if (fileType.equals("Events")) {
                fileReader = new FileReader(new File(getFilesDir(), "tauEvents.json"));

            } else  { //Courses
                fileReader = new FileReader(new File(getFilesDir(), "tauCourses.json"));
            }




        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return fileReader;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.start_menu);

        hasClickedPhones= 0;
        hasClickedCourses = 0;
        hasClickedEvents = 0;

        TextView tx1 = (TextView)findViewById(R.id.tv1);
        TextView tx2 = (TextView)findViewById(R.id.tv2);
        TextView tx3 = (TextView)findViewById(R.id.tv3);
        TextView tx4 = (TextView)findViewById(R.id.tv4);
        TextView tx5 = (TextView)findViewById(R.id.tv5);
        TextView tx6 = (TextView)findViewById(R.id.tv6);

        Typeface custom_font = Typeface.createFromAsset(getAssets(),  "fonts/Alef-Bold.ttf");
        tx1.setTypeface(custom_font);
        tx2.setTypeface(custom_font);
        tx3.setTypeface(custom_font);
        tx4.setTypeface(custom_font);
        tx5.setTypeface(custom_font);
        tx6.setTypeface(custom_font);

        initGoogleAPIClient();//Init Google API Client
        checkPermissions();//Check Permission

        getParking = (ImageButton) findViewById(R.id.getParking);
        getMap = (ImageButton) findViewById(R.id.getMap);
        getPhones = (ImageButton)  findViewById(R.id.getPhones);
        nearMe = (ImageButton) findViewById(R.id.near_me_icon);
        getCalendar = (ImageButton) findViewById(R.id.getCalander) ;
        getCoursesNavigation = (ImageButton) findViewById(R.id.profile_icon);
        info = (ImageButton) findViewById(R.id.info_icon);


        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartMenu.this, InformationActivity.class);
                startActivity(intent);
            }
        });

        getCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FillTotalInfo.isFilledDBCalender) {
                    Intent intent = new Intent(StartMenu.this, tauCalenderActivity.class);
                    startActivity(intent);
                    return;
                } else {
                    if (SplashActivity.processEventsStage == 0) {
                        hasClickedEvents += 1;
                        if (hasClickedEvents == 1) {
                            AsyncTask.execute(new Runnable() {
                                @Override
                                public void run() {
                                    FileReader filesReader = readFromFiles("Events");
                                    SplashActivity.readEventsDataFromFile(filesReader);
                                    FillTotalInfo.isFilledDBCalender = true;

                                }
                            });
                        }
                    }
                    Toast.makeText(
                            StartMenu.this,
                            "טוען נתונים... אנא המתינו ונסו שנית",
                            Toast.LENGTH_SHORT).show();

                }

            }
            });



        getParking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartMenu.this,ParkingActivity.class);
                startActivity(intent);
            }
        });



        getMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartMenu.this,MapsActivity.class);
                startActivity(intent);
            }
        });


        getPhones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(FillTotalInfo.isFilledDBPhone ) {
                    Intent intent = new Intent(StartMenu.this, PhoneActivity.class);
                    startActivity(intent);
                    return;
                }else{
                    if (SplashActivity.processPhonesStage == 0){

                        hasClickedPhones +=1;
                        if (hasClickedPhones == 1){
                            AsyncTask.execute(new Runnable() {
                                @Override
                                public void run() {
                                    FileReader filesReader = readFromFiles("Phones");
                                    SplashActivity.readPhonesDataFromFile(filesReader);
                                    FillTotalInfo.isFilledDBPhone = true;

                                }
                            });
                        }



                    }

                    Toast.makeText(
                            StartMenu.this,
                            "טוען נתונים... אנא המתינו ונסו שנית",
                            Toast.LENGTH_SHORT).show();
                }


            }
        });


        nearMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartMenu.this,NearMeActivity.class);
                startActivity(intent);

            }
        });


        getCoursesNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(FillTotalInfo.isFilledDB) {
                    Intent intent = new Intent(StartMenu.this, CoursesNavigationActivity.class);
                    startActivity(intent);
                    return;
                }else{
                    if (SplashActivity.processCoursesStage == 0){

                        hasClickedCourses +=1;
                        if (hasClickedCourses == 1){
                            AsyncTask.execute(new Runnable() {
                                @Override
                                public void run() {
                                    FileReader filesReader = readFromFiles("Courses");
                                    SplashActivity.readCoursesDataFromFile(filesReader);
                                    FillTotalInfo.isFilledDB = true;

                                }
                            });
                        }



                    }

                    Toast.makeText(
                            StartMenu.this,
                            "טוען נתונים... אנא המתינו ונסו שנית",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });




        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("StartMenu Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }


    /* Initiate Google API Client  */
    private void initGoogleAPIClient() {
        //Without Google API Client Auto Location Dialog will not work
        mGoogleApiClient = new GoogleApiClient.Builder(StartMenu.this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    /* Check Location Permission for Marshmallow Devices */
    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(StartMenu.this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED)
                requestLocationPermission();
            else
                showSettingDialog();
        } else
            showSettingDialog();

    }

    /*  Show Popup to access User Permission  */
    private void requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(StartMenu.this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            ActivityCompat.requestPermissions(StartMenu.this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    ACCESS_FINE_LOCATION_INTENT_ID);

        } else {
            ActivityCompat.requestPermissions(StartMenu.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    ACCESS_FINE_LOCATION_INTENT_ID);
        }
    }

    /* Show Location Access Dialog */
    private void showSettingDialog() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);//Setting priotity of Location request to high
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(5 * 1000);//5 sec Time interval for location update
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(false); //this is the key ingredient to show dialog always when GPS is off

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                final LocationSettingsStates state = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can initialize location
                        // requests here.
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(StartMenu.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            e.printStackTrace();
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                        break;
                }
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case RESULT_OK:
                        Log.e("Settings", "Result OK");
                        break;
                    case RESULT_CANCELED:
                        Log.e("Settings", "Result Cancel");
                        break;
                }
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        hasClickedPhones= 0;
        hasClickedCourses = 0;
        registerReceiver(gpsLocationReceiver, new IntentFilter(BROADCAST_ACTION));//Register broadcast receiver to check the status of GPS
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Unregister receiver on destroy
        if (gpsLocationReceiver != null)
            unregisterReceiver(gpsLocationReceiver);
    }

    //Run on UI
    private Runnable sendUpdatesToUI = new Runnable() {
        public void run() {
            showSettingDialog();
        }
    };

    /* Broadcast receiver to check status of GPS */
    private BroadcastReceiver gpsLocationReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            //If Action is Location
            if (intent.getAction().matches(BROADCAST_ACTION)) {
                LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
                //Check if GPS is turned ON or OFF
                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    Log.e("About GPS", "GPS is Enabled in your device");
                } else {
                    //If GPS turned OFF show Location Dialog
                    new Handler().postDelayed(sendUpdatesToUI, 10);
                    // showSettingDialog();
                    Log.e("About GPS", "GPS is Disabled in your device");
                }

            }
        }
    };




    /* On Request permission method to check the permisison is granted or not for Marshmallow+ Devices  */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case ACCESS_FINE_LOCATION_INTENT_ID: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    //If permission granted show location dialog if APIClient is not null
                    if (mGoogleApiClient == null) {
                        initGoogleAPIClient();
                        showSettingDialog();
                    } else
                        showSettingDialog();


                } else {
                    Toast.makeText(StartMenu.this, "Location Permission denied.", Toast.LENGTH_SHORT).show();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }
    }


    @Override
    public void onBackPressed() {
        // super.onBackPressed();
        //Intent intent = new Intent(StartMenu.this,);
        moveTaskToBack(true);


    }
}
