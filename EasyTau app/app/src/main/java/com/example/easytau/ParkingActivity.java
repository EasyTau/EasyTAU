package com.example.easytau;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.ActivityCompat;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.view.ViewGroup;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.style.ForegroundColorSpan;
import android.widget.Button;

import android.content.DialogInterface;
import android.content.Intent;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.text.SpannableString;
import android.widget.Toast;

import java.util.Map;
import java.util.Set;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONException;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.xmlpull.v1.XmlPullParserException;
import java.io.IOException;

import static com.google.android.gms.wearable.DataMap.TAG;

public class ParkingActivity extends AppCompatActivity implements ParkingLotListener, GoogleApiClient.ConnectionCallbacks,  GoogleApiClient.OnConnectionFailedListener, LocationListener {
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    private ImageView logoAhuzatHof;
    private Button findParking;
    private Button home_button;
    private TextView name;
    private TextView address;
    private TextView status;
    List<ParkingLot> parkingLotListM = new ArrayList<>();
    private TextView priceDay;
    private TextView notes;
    private TextView notesStud;
    private TextView linkToWeb;
    private TextView status_time;
    private Animation animScale;
    Map<String,LatLng> mapParking = TotalInfoFromDb.getMapParking();
    AVLoadingIndicatorView avi;
    TextView tx7;

    LatLng destCheck = null;

    boolean gps_enabled = false;
    boolean network_enabled = false;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private LocationRequest mLocationRequest;
    private boolean mRequestingLocationUpdates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parking_info);
        animScale = AnimationUtils.loadAnimation(this, R.anim.anim_translate);
        findParking = (Button)findViewById(R.id.findParking);
        home_button = (Button)findViewById(R.id.home_button);
        logoAhuzatHof = (ImageView)findViewById(R.id.ahuzalHofLogo);
        name = (TextView)findViewById(R.id.parkingName);
        priceDay=(TextView)findViewById(R.id.priceDay);
        address = (TextView)findViewById(R.id.address);
        status =(TextView)findViewById(R.id.status);
        notes = (TextView)findViewById(R.id.notes);
        notesStud = (TextView)findViewById(R.id.notesStudents);
        linkToWeb =((TextView) findViewById(R.id.linkToWeb));
        status_time = (TextView) findViewById(R.id.date_status);

        TextView tx1 = (TextView)findViewById(R.id.parkingName);
        TextView tx2 = (TextView)findViewById(R.id.status);
        TextView tx3 = (TextView)findViewById(R.id.address);
        TextView tx4 = (TextView)findViewById(R.id.priceDay);
        TextView tx5 = (TextView)findViewById(R.id.notes);
        TextView tx6 = (TextView)findViewById(R.id.notesStudents);
        tx7 = (TextView) findViewById(R.id.loading) ;


        avi= (AVLoadingIndicatorView) findViewById(R.id.avi);


        Typeface bold_font = Typeface.createFromAsset(getAssets(),  "fonts/Alef-Bold.ttf");
        tx1.setTypeface(bold_font);
        tx2.setTypeface(bold_font);
        tx3.setTypeface(bold_font);
        tx4.setTypeface(bold_font);
        tx5.setTypeface(bold_font);
        tx6.setTypeface(bold_font);
        tx7.setTypeface(bold_font);

        home_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                didTapButton(view);
                Intent i = new Intent(view.getContext(), StartMenu.class);
                startActivity(i);
            }
        });

        if(SplashActivity.is_first){
            while (TotalInfoFromDb.getParkingLotListM().size()==0){
            }


        /*get current location */


            parkingLotListM = TotalInfoFromDb.getParkingLotListM();

            final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(ParkingActivity.this, android.R.layout.select_dialog_item){
                @Override
                public View getView(int position, View convertView, ViewGroup parent){
                    View view = super.getView(position, convertView, parent);
                    TextView textview = (TextView) view.findViewById(android.R.id.text1);
                    textview.setTextSize(17);
                    Typeface reg_font = Typeface.createFromAsset(getAssets(),  "fonts/Alef-Regular.ttf");
                    textview.setTypeface(reg_font);
                    return view;
                }
            };
            Set<String> stringSet = new HashSet<>();
            for (ParkingLot parking :parkingLotListM) {
                stringSet.add(parking.getName());
            }

            arrayAdapter.addAll(stringSet);

            windowChoice(arrayAdapter);


            findParking.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    windowChoice(arrayAdapter);
                }
            });


            avi.setVisibility(View.GONE);
            tx7.setVisibility(View.GONE);
            avi.smoothToHide();

        }else{
            try {
                tx7.setVisibility(View.VISIBLE);
                avi.setVisibility(View.VISIBLE);
                avi.show();
                new ParkingLotsList(this);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        initGoogleApiClient();
        createLocationRequest();
    }


    public void didTapButton(View view) {
        final Animation myAnim = AnimationUtils.loadAnimation(this, R.anim.bounce);
        BounceInterpolator interpolator = new BounceInterpolator(0.5, 50);
        myAnim.setInterpolator(interpolator);
        home_button.startAnimation(myAnim);
    }

    @Override
    public void onParkingLotSuccess(List<ParkingLot> parkingLotList) {

        this.parkingLotListM=parkingLotList;

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(ParkingActivity.this, android.R.layout.select_dialog_item){
            @Override
            public View getView(int position, View convertView, ViewGroup parent){
                View view = super.getView(position, convertView, parent);
                TextView textview = (TextView) view.findViewById(android.R.id.text1);
                textview.setTextSize(17);
                Typeface reg_font = Typeface.createFromAsset(getAssets(),  "fonts/Alef-Regular.ttf");
                textview.setTypeface(reg_font);
                return view;
            }
        };
        Set<String> stringSet = new HashSet<>();
        for (ParkingLot parking :parkingLotListM) {
            stringSet.add(parking.getName());
        }

        avi.setVisibility(View.GONE);
        tx7.setVisibility(View.GONE);
        avi.hide();

        arrayAdapter.addAll(stringSet);

        windowChoice(arrayAdapter);


        findParking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                windowChoice(arrayAdapter);
            }
        });
    }

    public static String getDate(long milliSeconds, String dateFormat)
    {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        calendar.add(Calendar.HOUR_OF_DAY,-2);
        return formatter.format(calendar.getTime());
    }




    public void windowChoice(final ArrayAdapter<String> arrayAdapter) {

        AlertDialog.Builder builderSingle = new AlertDialog.Builder(ParkingActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_activity, null);
        builderSingle.setCustomTitle(view);

        builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (name.getVisibility()!= View.VISIBLE){
                    Intent intent = new Intent(ParkingActivity.this, StartMenu.class);
                    startActivity(intent);
                }

            }



        });


        builderSingle.setOnKeyListener(new Dialog.OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface arg0, int keyCode,
                                 KeyEvent event) {
                // TODO Auto-generated method stub
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    finish();

                }
                return true;
            }
        });

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                home_button.setVisibility(View.VISIBLE);

                String strName = arrayAdapter.getItem(which);
                name.setText("חניון "+strName);
                name.setPaintFlags(name.getPaintFlags());
                name.setVisibility(View.VISIBLE);
                //name.setSingleLine();

                ParkingLot chosenParking =null;
                for (ParkingLot parking :parkingLotListM) {
                    if (parking.getName().equals(strName)){
                        chosenParking=parking;
                    }
                }
                ImageButton marker = (ImageButton) findViewById(R.id.Marker);


                String currStatus = chosenParking.getStatus();
                SpannableString statusTitle;
                if (currStatus.equals("null")){
                    statusTitle = new SpannableString("תפוסת החניון: "+"אין מידע זמין");
                }else{
                    statusTitle = new SpannableString("תפוסת החניון: "+ currStatus);
                }

                statusTitle.setSpan(new ForegroundColorSpan(Color.DKGRAY),0,14,0);
                if(currStatus.equals("פנוי")){
                    statusTitle.setSpan(new ForegroundColorSpan(Color.GREEN),14,statusTitle.length(),0);
                }
                else if(currStatus.equals("מלא")){
                    statusTitle.setSpan(new ForegroundColorSpan(Color.RED),14,statusTitle.length(),0);
                }
                status.setText(statusTitle);
                if( !chosenParking.getStatus_time().equals("null")) {

                    Long dateInMilli = Long.parseLong(chosenParking.getStatus_time());

                    String dateStatus = getDate(dateInMilli, "dd/MM/yyyy, HH:mm:ss");
                    SpannableString addDate = new SpannableString("עודכן ב: " + dateStatus);
                    addDate.setSpan(new ForegroundColorSpan(Color.GREEN), 0, 0, 0);
                    status_time.setText(addDate);
                    status_time.setVisibility(View.VISIBLE);
                }

                status.setVisibility(View.VISIBLE);
                findParking.setVisibility(View.VISIBLE);
                logoAhuzatHof.setVisibility(View.VISIBLE);



                final String name = chosenParking.getName();

                destCheck = null;

                for (Map.Entry<String, LatLng> entry : mapParking.entrySet()) {
                    if(entry.getKey().equals(name)){
                        destCheck = entry.getValue();
                    }
                    if (destCheck != null){
                        marker.setVisibility(View.VISIBLE);
                    }


                }

                marker.setOnClickListener(new View.OnClickListener() {
                    @Override

                    public void onClick(View v) {
                        togglePeriodicLocationUpdates();

                        if ((destCheck != null) && (mLastLocation != null)) {
                            Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                                    Uri.parse("http://maps.google.com/maps?saddr=" + mLastLocation.getLatitude() + "," + mLastLocation.getLongitude() + "&daddr=" + destCheck.latitude + "," + destCheck.longitude));
                            startActivity(intent);

                        } else if (mLastLocation == null) {/*If the GPS/ have no permission to grt curr location does not work, tell the user to turn it on*/

                            AlertDialog.Builder builderSingle = new AlertDialog.Builder(ParkingActivity.this);
                            LayoutInflater inflater = getLayoutInflater();
                            View view = inflater.inflate(R.layout.dialog, null);
                            TextView title = (TextView) view.findViewById(R.id.title);
                            title.setText("לא ניתן לנווט");
                            builderSingle.setCustomTitle(view);
                            builderSingle.setMessage("ל- EasyTau אין גישה למיקום הנוכחי, לשימוש ביישום יש לאפשר גישה בהגדרות המכשיר");
                            builderSingle.setNegativeButton("אישור", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });

                            AlertDialog dialog = builderSingle.show();
                        }

                    }
                });






                SpannableString addTitle = new SpannableString("כתובת החניון: "+ chosenParking.getAddress() );
                addTitle.setSpan(new ForegroundColorSpan(Color.DKGRAY),0,12,0);
                address.setText(addTitle);
                address.setVisibility(View.VISIBLE);

                SpannableString priceTitle = new SpannableString("מחיר: "+ chosenParking.getPrice_day());
                priceTitle.setSpan(new ForegroundColorSpan(Color.DKGRAY),0,5,0);
                priceDay.setText(priceTitle);
                priceDay.setVisibility(View.VISIBLE);

                SpannableString notesTitle = new SpannableString("הערות לתעריף: " + chosenParking.getNote());
                notesTitle.setSpan(new ForegroundColorSpan(Color.DKGRAY),0,13,0);
                notes.setText(notesTitle);
                notes.setVisibility(View.VISIBLE);

                SpannableString notesStudTitle = new SpannableString("הערות לסטודנט: ניתן לקבל הנחה של 50% במחיר החניון ללא עלות ע\"י עדכון פרטים באתר TAU, פרטים באתר אגודת הסטודנטים- ");
                notesStudTitle.setSpan(new ForegroundColorSpan(Color.DKGRAY),0,14,0);
                notesStud.setText(notesStudTitle);
                notesStud.setVisibility(View.VISIBLE);


                linkToWeb.setMovementMethod(LinkMovementMethod.getInstance());

                linkToWeb.setText(Html.fromHtml(getResources().getString(R.string.student_site)));


                linkToWeb.setVisibility(View.VISIBLE);

            }
        });

        AlertDialog dialog = builderSingle.show();
    }


    /*current location functions*/
    private void initGoogleApiClient() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Resuming the periodic location updates
        if (mGoogleApiClient.isConnected() && mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    protected  void  onRestart(){
        super.onRestart();

    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mGoogleApiClient.isConnected()){
            stopLocationUpdates();
        }
        //stopLocationUpdates();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            mLastLocation = null;
        } else {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        }
        if (mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    /**
     * Creating location request object
     * */
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }


    /**
     * Method to toggle periodic location updates
     * */
    private void togglePeriodicLocationUpdates() {
        if (!mRequestingLocationUpdates) {
            mRequestingLocationUpdates = true;
            // Starting the location updates
            startLocationUpdates();
            Log.d(TAG, "Periodic location updates started!");

        } else {
            mRequestingLocationUpdates = false;
            // Stopping the location updates
            stopLocationUpdates();
            Log.d(TAG, "Periodic location updates stopped!");
        }
    }

    /**
     *
     * Starting the location updates
     * */
    protected void startLocationUpdates() {
        final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );
        boolean GPSEnabled =  manager.isProviderEnabled( LocationManager.GPS_PROVIDER);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED || !GPSEnabled) {
            mLastLocation = null;
        }else {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }

    }

    /**
     * Stopping location updates
     */
    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    /**
     * Google api callback methods
     */
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = "
                + result.getErrorCode());
    }

    @Override
    public void onConnectionSuspended(int arg0) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        // Assign the new location
        mLastLocation = location;
        updateLocation2();
    }

    private void updateLocation2() {
        final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );
        boolean GPSEnabled =  manager.isProviderEnabled( LocationManager.GPS_PROVIDER);

        if (!GPSEnabled || ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            mLastLocation = null;
        }else {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        }

    }



}