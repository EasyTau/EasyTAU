package com.example.easytau;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import android.widget.AdapterView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;

import static com.google.android.gms.wearable.DataMap.TAG;

/**
 * Created by TT on 1/17/2017.
 */

public class CoursesNavigationActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private CustomAutoCompleteTextView textSearchCourse;

    List<Course> courseList = new ArrayList<>();
    Map<String,LatLng> mapBuild = new HashMap<>();
    private Button home_button;
    ListView listView;

    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private LocationRequest mLocationRequest;
    private boolean mRequestingLocationUpdates;

    public class costume_courseNev_list extends ArrayAdapter<String> {
        private final Activity context;
        public costume_courseNev_list (Activity context, List<String> courses) {
            super(context, R.layout.row_table, courses);
            this.context = context;

        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            LayoutInflater inflater = context.getLayoutInflater();
            final Course course = courseList.get(position);

            String lang = Locale.getDefault().getLanguage();
            View rowView;
            if(lang.equals("עברית") || lang.equals("iw") || lang.equals("he")|| lang.equals("heb")  || lang.equals("hebrew") ) {
                rowView = inflater.inflate(R.layout.row_table, null, true);
            }
            else {
                rowView = inflater.inflate(R.layout.row_table_eng, null, true);

            }
            Typeface alef_font = Typeface.createFromAsset(getAssets(),  "fonts/Alef-Regular.ttf");

            TextView c1 = (TextView) rowView.findViewById(R.id.Semester);
            c1.setText("סמסטר " +course.getSemester());
            TextView c2 = (TextView) rowView.findViewById(R.id.typeAndTeacher);
            c2.setText(course.getCourseType() + " " +"עם המרצה: " + course.getTeacher()+", ");
            TextView c3 = (TextView) rowView.findViewById(R.id.DayAndHours);
            c3.setText(  "יום "+ course.getDay() + ", "+ "שעות: " + course.getHours() + " ");
            TextView c4 = (TextView) rowView.findViewById(R.id.Building);
            c4.setText(course.getBuilding());
            TextView c5 = (TextView) rowView.findViewById(R.id.RoomAndBulding);
            c5.setText(course.getBuilding() +" חדר "+ course.getRoom()+ ".");
            c1.setTypeface(alef_font);
            c2.setTypeface(alef_font);
            c3.setTypeface(alef_font);
            c4.setTypeface(alef_font);
            c5.setTypeface(alef_font);


            ImageButton marker = (ImageButton) rowView.findViewById(R.id.Marker);

            marker.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LatLng dest = null;
                    View listRow = (View) v.getParent();
                    TextView sample = (TextView) listRow.findViewById(R.id.Building);
                    String result = sample.getText().toString();

                    togglePeriodicLocationUpdates();

                    for (Map.Entry<String, LatLng> entry : mapBuild.entrySet()) {
                        if (entry.getKey().contains(result)) {
                            dest = entry.getValue();
                        }
                    }

                    if ((dest != null) && (mLastLocation != null)) {
                        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                                Uri.parse("http://maps.google.com/maps?saddr=" + mLastLocation.getLatitude() + "," + mLastLocation.getLongitude() + "&daddr=" + dest.latitude + "," + dest.longitude));
                        startActivity(intent);

                    }else if(mLastLocation == null) {/*If the GPS/ have no permission to grt curr location does not work, tell the user to turn it on*/
                        AlertDialog.Builder builderSingle = new AlertDialog.Builder(CoursesNavigationActivity.this);
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
                    } else if (dest == null) { /*if there is no building show an alert*/

                        AlertDialog.Builder builderSingle = new AlertDialog.Builder(CoursesNavigationActivity.this);
                        LayoutInflater inflater = getLayoutInflater();
                        View view = inflater.inflate(R.layout.dialog, null);
                        TextView title = (TextView) view.findViewById(R.id.title);
                        title.setText("לא ניתן לנווט");
                        builderSingle.setCustomTitle(view);
                        builderSingle.setMessage("אין מידע אודות הבניין בו מתקיים הקורס המבוקש");
                        builderSingle.setNegativeButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

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
        setContentView(R.layout.activity_courses_navigation);
        getWindow().setBackgroundDrawableResource(R.drawable.entrance_background);

        home_button = (Button)findViewById(R.id.home_button);
        home_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                didTapButton(view);
                Intent i = new Intent(view.getContext(), StartMenu.class);
                startActivity(i);
            }
        });
        listView = (ListView) findViewById(R.id.courseList);
        listView.setVisibility(View.INVISIBLE);


        /*this is for the auto-complete */
        Set<String> allCoursesSet = new HashSet<>();
        final List<String> allCoursesList = new ArrayList<>();
        for (String course: TotalInfoFromDb.getCourses().keySet()){
            ArrayList<Course> courses = TotalInfoFromDb.getCourses().get(course);
            for (Course c : courses) {
                if (!c.getBuilding().contains("None")) {    //check if there's an available location information
                    allCoursesSet.add(course);
                }
            }
        }

        allCoursesList.addAll(allCoursesSet);
        Collections.sort(allCoursesList);

        /*end of part for auto-complete*/

        textSearchCourse=(CustomAutoCompleteTextView)findViewById(R.id.textSearchCourse);


        AutoCompleteAdapter adapter = new AutoCompleteAdapter(this,android.R.layout.simple_list_item_1, android.R.id.text1,allCoursesList);
        textSearchCourse.setAdapter(adapter);

        textSearchCourse.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (v == textSearchCourse) {
                    if (hasFocus) {
                        // Open keyboard
                        ((InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(textSearchCourse, InputMethodManager.SHOW_FORCED);
                    } else {
                        // Close keyboard
                        ((InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(textSearchCourse.getWindowToken(), 0);
                    }
                }
            }
        });


        textSearchCourse.setOnItemClickListener(new AdapterView.OnItemClickListener() {


            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                courseList.clear();
                String courseToSearch = textSearchCourse.getText().toString();
                List<String> coursesToShow = new ArrayList<String>();

                for(String courseStr: TotalInfoFromDb.getCourses().keySet()) {
                    if (courseStr.equals(courseToSearch)){
                        ArrayList<Course> courses = TotalInfoFromDb.getCourses().get(courseStr);
                        for (Course c : courses){
                            if (!c.getBuilding().contains("None") ) {    //check if there's an available location information
                                courseList.add(c);
                            }
                        }

                    }
                }

                for(Course course: courseList){
                    coursesToShow.add(course.getCourseName());
                }

                CoursesNavigationActivity.costume_courseNev_list adapter2= new
                        CoursesNavigationActivity.costume_courseNev_list(CoursesNavigationActivity.this,coursesToShow);

                listView.setVisibility(View.VISIBLE);
                listView.setAdapter(adapter2);

                setTextFocus(false);
                textSearchCourse.setFocusableInTouchMode(true);
                //setTextFocus(true);


            }


        });

        String jsonText;
        JSONParser parser = new JSONParser();

        try {
            InputStream is = getAssets().open("buildings.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            jsonText = new String(buffer, "UTF-8");

            JSONArray allPlaces = (JSONArray) parser.parse(jsonText);

            for (int i = 0; i < allPlaces.size(); i++) {
                org.json.simple.JSONObject placeObj = (org.json.simple.JSONObject) allPlaces.get(i);

                String coursesNav = (String) placeObj.get("coursesNav");
                if (coursesNav != null) {
                    float coord1 = (Float.valueOf((String) placeObj.get("coord1")));
                    float coord2 = (Float.valueOf((String) placeObj.get("coord2")));
                    LatLng positionCoords = new LatLng(coord1, coord2);

                    mapBuild.put(coursesNav, positionCoords);

                }
            }

        } catch (org.json.simple.parser.ParseException e) {
            e.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        /*get current location */
        initGoogleApiClient();
        createLocationRequest();

    }


    public void didTapButton(View view) {
        final Animation myAnim = AnimationUtils.loadAnimation(this, R.anim.bounce);
        BounceInterpolator interpolator = new BounceInterpolator(0.5, 50);
        myAnim.setInterpolator(interpolator);
        setTextFocus(false);
        home_button.startAnimation(myAnim);
    }



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //Changes 'back' button action
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            setTextFocus(false);
            Intent intent = new Intent(CoursesNavigationActivity.this,StartMenu.class);
            startActivity(intent);
        }
        return true;
    }



    public void setTextFocus(boolean isFocused) {

        textSearchCourse.setFocusable(isFocused);
        textSearchCourse.setFocusableInTouchMode(isFocused);

        if (isFocused) {
            textSearchCourse.requestFocus();
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


