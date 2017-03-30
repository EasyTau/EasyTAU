package com.example.easytau;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.app.ListActivity;

import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.*;
import com.google.android.gms.vision.text.Text;

import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import Modules.NearMeLocation;

import static com.google.android.gms.wearable.DataMap.TAG;


public class NearMeActivity extends ListActivity implements AdapterView.OnItemSelectedListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    ArrayList<NearMeLocation> locationList = new ArrayList<NearMeLocation>();
    ArrayList<NearMeLocation> FilteredlocationList;
    ArrayAdapter<String> adapter2; //DEFINING A STRING ADAPTER WHICH WILL HANDLE THE DATA OF THE LISTVIEW
    private Button home_button;
    Spinner spinner;
    ListView listView;
    private static HashMap<String, MarkerCustomized> nearMeMarkersMap = new HashMap<>();

    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private LocationRequest mLocationRequest;
    private boolean mRequestingLocationUpdates;

    public class costume_nearMe_list extends ArrayAdapter<String> {
        private final Activity context;
        public costume_nearMe_list (Activity context, List<String> locations) {
            super(context, R.layout.row_table, locations);
            this.context = context;

        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            LayoutInflater inflater = context.getLayoutInflater();
            final NearMeLocation loc = FilteredlocationList.get(position);

            String lang = Locale.getDefault().getLanguage();
            View rowView;
            if(lang.equals("עברית") || lang.equals("iw") || lang.equals("he")|| lang.equals("heb")  || lang.equals("hebrew") ) {
                rowView = inflater.inflate(R.layout.row_table_nearme, null, true);
            }
            else {
                rowView = inflater.inflate(R.layout.row_table_nearme_eng, null, true);

            }
            Typeface alef_font = Typeface.createFromAsset(getAssets(),  "fonts/Alef-Regular.ttf");
            Typeface bold_font = Typeface.createFromAsset(getAssets(),  "fonts/Alef-Bold.ttf");

            TextView location_name = (TextView) rowView.findViewById(R.id.Location);
            location_name.setText(loc.getName());

            location_name.setTypeface(bold_font);

            TextView distance = (TextView) rowView.findViewById(R.id.Distance);
            distance.setText("מרחק: " + loc.getDistance() +" מטר ממך");
            distance.setTypeface(alef_font);

            TextView lat = (TextView) rowView.findViewById(R.id.latitude);
            TextView longi = (TextView) rowView.findViewById(R.id.longitude);

            if(loc.getAddress()!= null) {
                lat.setText("" + loc.getAddress().latitude);
                longi.setText("" + loc.getAddress().longitude);

            }
            else {
                lat.setText("");
                longi.setText("");
            }
            longi.setVisibility(View.GONE);
            lat.setVisibility(View.GONE);


            ImageButton marker = (ImageButton) rowView.findViewById(R.id.Marker);

            marker.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LatLng dest = null;
                    View listRow = (View) v.getParent();

                    TextView sample1 = (TextView) listRow.findViewById(R.id.latitude);
                    TextView sample2 = (TextView) listRow.findViewById(R.id.longitude);

                    String latitude = sample1.getText().toString();
                    String longitude = sample2.getText().toString();

                    togglePeriodicLocationUpdates();


                    if (!latitude.equals("") && !longitude.equals("") && (mLastLocation != null)) {
                        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                                Uri.parse("http://maps.google.com/maps?saddr=" + mLastLocation.getLatitude() + "," + mLastLocation.getLongitude() + "&daddr=" + latitude + "," + longitude));
                        startActivity(intent);

                    }else if(mLastLocation == null) {/*If the GPS/ have no permission to grt curr location does not work, tell the user to turn it on*/
                        AlertDialog.Builder builderSingle = new AlertDialog.Builder(NearMeActivity.this);
                        LayoutInflater inflater = getLayoutInflater();
                        View view = inflater.inflate(R.layout.dialog, null);
                        TextView title = (TextView) view.findViewById(R.id.title);
                        title.setText("לא ניתן לנווט");
                        builderSingle.setCustomTitle(view);
                        builderSingle.setMessage("ל- EasyTAU אין גישה למיקום הנוכחי, לשימוש ביישום יש לאפשר גישה בהגדרות המכשיר");
                        builderSingle.setNegativeButton("אישור", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                        AlertDialog dialog = builderSingle.show();
                    }

                    return;
                }
            });
            return rowView;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_near_me);

        spinner = (Spinner) findViewById(R.id.PlacesList);

        String[] categoryArray = {"בחר/י קטגוריה", "קפיטריות", "קפה אמון", "ספריות", "חניונים", "שערים", "שונות", "ספורט"};
        int[] images = {0, R.drawable.food, R.drawable.amun, R.drawable.library, R.drawable.parking, R.drawable.gate, R.drawable.general, R.drawable.fitness};
        String leng = Locale.getDefault().getLanguage();

        NearMeAdapter adapter;
        if(leng.equals("עברית") || leng.equals("iw") || leng.equals("he")|| leng.equals("heb")  || leng.equals("hebrew") ) {
            adapter = new NearMeAdapter(this, categoryArray, images,1);
        } else {
            adapter = new NearMeAdapter(this, categoryArray, images);
        }

        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        String jsonText;
        JSONParser parser = new JSONParser();

        try {
            InputStream is = getAssets().open("buildings.json");
            //int size = is.available();
            jsonText = getStringFromInputStream(is);
            //byte[] buffer = new byte[1024];

            //is.read(buffer);
            // is.close();

            // jsonText = new String(buffer, "UTF-8");

            JSONArray allPlaces = (JSONArray) parser.parse(jsonText);

            for (int i = 0; i < allPlaces.size(); i++) {
                org.json.simple.JSONObject placeObj = (org.json.simple.JSONObject) allPlaces.get(i);

                String name = (String) placeObj.get("name");
                String type = (String) placeObj.get("type");
                float coord1 = (Float.valueOf((String) placeObj.get("coord1")));
                float coord2 = (Float.valueOf((String) placeObj.get("coord2")));
                String info = (String) placeObj.get("info");
                String url = (String) placeObj.get("url");
                String logo = (String) placeObj.get("logo");

                LatLng positionCoords = new LatLng(coord1, coord2);
                if ((!type.equals("building")) && (!type.equals("dorms")) && (!type.equals(""))) {
                    MarkerCustomized markerCustom = new MarkerCustomized(name, type, positionCoords, info, url, logo);
                    nearMeMarkersMap.put(name, markerCustom);
                }
            }

        } catch (org.json.simple.parser.ParseException e) {
            e.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        createLocationList();

        /*get current location */
        initGoogleApiClient();
        createLocationRequest();

        home_button = (Button) findViewById(R.id.home_button);
        home_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                didTapButton(view);
                Intent i = new Intent(view.getContext(), StartMenu.class);
                startActivity(i);
            }
        });

    }



    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        togglePeriodicLocationUpdates();

        List<String> names = new ArrayList<String>();
        String select = spinner.getSelectedItem().toString();
        if (mLastLocation != null) {
            if (locationList != null) {
                FilteredlocationList= new ArrayList<NearMeLocation>();

                for (NearMeLocation loc : locationList) {

                    Location loc2 = new Location("");
                    loc2.setLatitude(loc.getAddress().latitude);
                    loc2.setLongitude(loc.getAddress().longitude);
                    float dis = (float) fmt(mLastLocation.distanceTo(loc2));
                    loc.setDistance(dis);
                    if(loc.getType().equals(select)) {
                        FilteredlocationList.add(loc);
                    }

                }
                Collections.sort(FilteredlocationList, new Comparator<NearMeLocation>() {
                    public int compare(NearMeLocation o1, NearMeLocation o2) {
                        if (o1.getDistance() == o2.getDistance())
                            return 0;
                        return o1.getDistance() < o2.getDistance() ? -1 : 1;
                    }
                });

                ArrayList <String> locStr = new ArrayList<>();

                    for (NearMeLocation loc : FilteredlocationList) {
                        locStr.add(loc.getName());
                    }

                adapter2= new costume_nearMe_list(NearMeActivity.this,locStr);

                listView = (ListView) findViewById(android.R.id.list);
                listView.setVisibility(View.VISIBLE);
                listView.setAdapter(adapter2);
                listView.setClickable(false);
            }
        } else {
            if (!select.equals("בחר/י קטגוריה")) {
                ArrayList <String> noNav = new ArrayList<>();
                noNav.add("ל- EasyTAU אין גישה למיקום הנוכחי, לשימוש ביישום יש לאפשר גישה בהגדרות המכשיר");
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.near_me_list_text, noNav) {

                    public View getView(int position, View convertView, ViewGroup parent) {
                        View v = super.getView(position, convertView, parent);

                        Typeface externalFont = Typeface.createFromAsset(getAssets(), "fonts/Alef-Bold.ttf");
                        ((TextView) v).setTypeface(externalFont);
                        ((TextView) v).setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                        ((TextView) v).setTextSize(20);
                        return v;
                    }
                };
                listView = (ListView) findViewById(android.R.id.list);
                listView.setAdapter(adapter);
                listView.setClickable(false);

            }
        }
    }

    public String GetLocationTitle(String str) {
        int i = str.indexOf(":");
        String title = str.substring(0, i);
        return title;
    }

    public void didTapButton(View view) {
        final Animation myAnim = AnimationUtils.loadAnimation(this, R.anim.bounce);
        BounceInterpolator interpolator = new BounceInterpolator(0.5, 50);
        myAnim.setInterpolator(interpolator);
        home_button.startAnimation(myAnim);
    }


    public static String getStringFromInputStream(InputStream stream) throws IOException
    {
        int n = 0;
        char[] buffer = new char[1024 * 4];
        InputStreamReader reader = new InputStreamReader(stream, "UTF8");
        StringWriter writer = new StringWriter();
        while (-1 != (n = reader.read(buffer))) writer.write(buffer, 0, n);
        return writer.toString();
    }
    public static double fmt(float d) {
        double x = Math.floor(d * 100) / 100;
        return x;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    public void createLocationList() {

        for (MarkerCustomized marker : nearMeMarkersMap.values()) {
            if (marker.getType().equals("library")) {
                locationList.add(new NearMeLocation(marker.getName(), "ספריות", marker.getPosition()));
            } else if (marker.getType().equals("food")) {
                locationList.add(new NearMeLocation(marker.getName(), "קפיטריות", marker.getPosition()));
            } else if (marker.getType().equals("parking")) {
                locationList.add(new NearMeLocation(marker.getName(), "חניונים", marker.getPosition()));
            } else if (marker.getType().equals("general")) {
                locationList.add(new NearMeLocation(marker.getName(), "שונות", marker.getPosition()));
            } else if (marker.getType().equals("fitness")) {
                locationList.add(new NearMeLocation(marker.getName(), "ספורט", marker.getPosition()));
            } else if (marker.getType().equals("gate")) {
                locationList.add(new NearMeLocation(marker.getName(), "שערים", marker.getPosition()));
            } else if (marker.getType().equals("amun")) {
                locationList.add(new NearMeLocation(marker.getName(), "קפה אמון", marker.getPosition()));
            }
        }

    }


    public class NearMeAdapter extends ArrayAdapter<String> {

        Context c;
        String[] places;
        int[] images;

        public NearMeAdapter(Context ctx, String[] places, int[] images) {
            super(ctx, R.layout.image_spinner_text, places);
            this.c = ctx;
            this.places = places;
            this.images = images;
        }

        public NearMeAdapter(Context ctx, String[] places, int[] images, int heb) {
            super(ctx, R.layout.image_spinner_text_heb, places);
            this.c = ctx;
            this.places = places;
            this.images = images;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                String leng = Locale.getDefault().getLanguage();

                if(leng.equals("עברית") || leng.equals("iw") || leng.equals("he")|| leng.equals("heb")  || leng.equals("hebrew") ) {
                    convertView = inflater.inflate(R.layout.image_spinner_text_heb, null);
                } else {
                    convertView = inflater.inflate(R.layout.image_spinner_text, null);
                }

            }


            TextView place = (TextView) convertView.findViewById(R.id.sppinerText);
            Typeface externalFont = Typeface.createFromAsset(getAssets(), "fonts/Alef-Bold.ttf");
            ((TextView) place).setTypeface(externalFont);
            ((TextView) place).setTextSize(20);

            ImageView img = (ImageView) convertView.findViewById(R.id.image1);
            img.setImageResource(images[position]);
            place.setText(places[position]);


            return convertView;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                String leng = Locale.getDefault().getLanguage();
                if(leng.equals("עברית") || leng.equals("iw") || leng.equals("he")|| leng.equals("heb")  || leng.equals("hebrew") ) {
                    convertView = inflater.inflate(R.layout.image_spinner_text_heb, null);
                } else {
                    convertView = inflater.inflate(R.layout.image_spinner_text, null);
                }
            }


            TextView place = (TextView) convertView.findViewById(R.id.sppinerText);
            Typeface externalFont = Typeface.createFromAsset(getAssets(), "fonts/Alef-Bold.ttf");
            ((TextView) place).setTypeface(externalFont);
            ((TextView) place).setTextSize(20);

            ImageView img = (ImageView) convertView.findViewById(R.id.image1);
            img.setImageResource(images[position]);
            place.setText(places[position]);


            return convertView;
        }
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
        if(mGoogleApiClient.isConnected())
            stopLocationUpdates();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            mLastLocation = null;
        } else {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        }
        if (mGoogleApiClient.isConnected() && mRequestingLocationUpdates) {
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
            if(!mGoogleApiClient.isConnected())
                mGoogleApiClient.connect();
            if(mGoogleApiClient.isConnected())
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
            return;
        }
        if(mLastLocation == null)
            createLocationRequest();

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

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
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

    }
}