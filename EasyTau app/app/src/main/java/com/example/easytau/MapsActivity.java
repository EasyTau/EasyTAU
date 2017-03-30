package com.example.easytau;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.SlidingDrawer;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.wunderlist.slidinglayer.SlidingLayer;

import com.wunderlist.slidinglayer.LayerTransformer;
import com.wunderlist.slidinglayer.SlidingLayer;
import com.wunderlist.slidinglayer.transformer.AlphaTransformer;
import com.wunderlist.slidinglayer.transformer.RotationTransformer;
import com.wunderlist.slidinglayer.transformer.SlideJoyTransformer;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
//import com.mongodb.DBCollection;
//import com.mongodb.client.MongoCollection;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.widget.ImageView;

//import org.bson.Document;
import org.json.JSONException;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.xmlpull.v1.XmlPullParserException;


public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback,GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
      private SlidingLayer filterSlidingLayer;
    private SlidingUpPanelLayout navigationSlidingPanel;
    private Button home_button;
    private CustomAutoCompleteTextView textOrigin;
    private CustomAutoCompleteTextView textDestination;
    private Button btnFindPath;
    private Button btnOpenNav;
    private Button btnCloseNav;
    private Button btnOpenFilter;
    private Button btnCloseFilter;
    private Marker mMyLocation;
    private GoogleMap mMap;
    private LatLng zoom;

    private static List<Marker> markerList = new ArrayList<>();
    private static HashMap<String,MarkerCustomized> customizedMarkersMap = new HashMap<>();
    private GoogleApiClient mGoogleApiClient;
    private Location mCurrentLocation;

    LocationRequest mLocationRequest;
    private static final int REQUEST_CHECK_SETTINGS = 0x1;
    private static final int ACCESS_FINE_LOCATION_INTENT_ID = 3;

    boolean mRequestingLocationUpdates;


    public static List<Marker> getMarkerList() {
        return markerList;
    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    class MyInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

        private final View myContentsView;

        MyInfoWindowAdapter(){
            myContentsView = getLayoutInflater().inflate(R.layout.custom_info_contents, null);
        }

        @Override
        public View getInfoContents(Marker marker) {

            setCustomInfo(marker, myContentsView);

            return myContentsView;
        }

        @Override
        public View getInfoWindow(Marker marker) {
            return null;
        }

    }

    public void setCustomInfo( Marker marker, View view) {
        int logoId =0;
        String markerName = marker.getTitle();
        MarkerCustomized m = customizedMarkersMap.get(markerName);
        String markerLogo = m.getLogo();
        boolean is_mefuch=false;
        boolean is_site = false;
        boolean is_rooms_site = false;
        boolean is_menu_site =false;

        try {
            Class res = R.drawable.class;
            Field field = res.getField(markerLogo);
            logoId = field.getInt(null);
        }
        catch (Exception e) {
            Log.e("MyTag", "Failure to get drawable id.", e);
        }

        if (marker.getTag().equals("current place")) {
            logoId = R.drawable.ic_smily_hands;
        }else if (m.getName().equals("ג'פניקה") || m.getName().equals("ארומה בית התפוצות")){
            is_menu_site = true;
        } else if (m.getName().equals("ספריית וינר") || m.getName().equals("בית התפוצות")){
            is_site = true;
        } else  if (m.getType().equals("food") && (!m.getUrl().equals(""))){
            is_mefuch = true;
        }else if (m.getType().equals("library") && (!m.getUrl().equals(""))){
            is_rooms_site = true;
        }

        ((ImageView) view.findViewById(R.id.logo)).setImageResource(logoId);
        /*set title to the marker custom info window*/
        String title = marker.getTitle();
        TextView titleUi = ((TextView) view.findViewById(R.id.title));
        if (title != null) {

            titleUi.setText(title);
        } else {
            titleUi.setText("");
        }

        String snippet = marker.getSnippet();
        TextView snippetUi = ((TextView) view.findViewById(R.id.snippet));

        if (snippet != null) {
            if (is_mefuch) {
                snippetUi.setText(snippet+"\n >>לקבלת מחירון מפוקח לחצו על חלון זה<<");
            }else if (is_rooms_site){
                snippetUi.setText(snippet+"\n >>להזמנת חדר בספרייה לחצו על חלון זה<<");
            }else if (is_site){
                snippetUi.setText(snippet+"\n >>לאתר לחצו על חלון זה<<");
            }else if (is_menu_site){
                snippetUi.setText(snippet+"\n >>לתפריט לחצו על חלון זה<<");
            }

            else{
                snippetUi.setText(snippet);
            }
        } else {
            if (is_mefuch) {
                snippetUi.setText(">>לקבלת מחירון מפוקח לחצו על חלון זה<<");
            }else if (is_rooms_site){
                snippetUi.setText(">>להזמנת חדר בספרייה לחצו על חלון זה<<");
            }else if (is_site){
                snippetUi.setText("\n >>לאתר לחצו על חלון זה<<");
            }else if (is_menu_site){
                snippetUi.setText(" >>לתפריט לחצו על חלון זה<<");
            }

            else
                snippetUi.setText("");
        }

    }





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        if (android.os.Build.VERSION.SDK_INT > 19) {
            setContentView(R.layout.activity_maps);
        }else{
            setContentView(R.layout.activity_maps_low_sdk);

        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        bindViews();
        initState();



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

                String name = (String) placeObj.get("name");
                String type = (String) placeObj.get("type");
                float coord1 = (Float.valueOf((String) placeObj.get("coord1")));
                float coord2 = (Float.valueOf((String) placeObj.get("coord2")));
                String info = (String) placeObj.get("info");
                String url = (String) placeObj.get("url");
                String logo = (String) placeObj.get("logo");

                LatLng positionCoords = new LatLng(coord1, coord2);

                MarkerCustomized markerCustom = new MarkerCustomized(name, type, positionCoords, info, url, logo);
                customizedMarkersMap.put(name, markerCustom);
            }

        } catch (org.json.simple.parser.ParseException e) {
            e.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }




        navigationSlidingPanel = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        navigationSlidingPanel.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                Log.i("MapsActivity", "onPanelSlide, offset " + slideOffset);
            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                Log.i("MapsActivity", "onPanelStateChanged " + newState);
            }
        });
        navigationSlidingPanel.setFadeOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigationSlidingPanel.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                btnOpenNav.setVisibility(View.VISIBLE);
                btnCloseNav.setVisibility(View.GONE);
            }
        });

        List<String> buildingsList = new ArrayList<>();
        buildingsList.addAll(customizedMarkersMap.keySet());
        buildingsList.add("המיקום שלי");

        btnFindPath = (Button) findViewById(R.id.btnFindPath);
        btnOpenNav = (Button) findViewById(R.id.buttonOpen);
        btnCloseNav = (Button) findViewById(R.id.buttonClose);
        btnOpenFilter = (Button) findViewById(R.id.buttonFilterOpen);
        btnCloseFilter = (Button) findViewById(R.id.buttonFilterClose);
        textOrigin=(CustomAutoCompleteTextView)findViewById(R.id.textOrigin);
        textDestination=(CustomAutoCompleteTextView)findViewById(R.id.textDestination);


        AutoCompleteAdapter adapter = new AutoCompleteAdapter(this,android.R.layout.simple_list_item_1, android.R.id.text1,buildingsList);
        textOrigin.setAdapter(adapter);
        textDestination.setAdapter(adapter);


        btnOpenNav.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                navigationSlidingPanel.setPanelState(SlidingUpPanelLayout.PanelState.ANCHORED);
                btnCloseNav.setVisibility(View.VISIBLE);
                view.setVisibility(View.GONE);

                return true;
            }
        });

        btnCloseNav.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                navigationSlidingPanel.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                btnOpenNav.setVisibility(View.VISIBLE);
                view.setVisibility(View.GONE);

                return true;
            }
        });


        btnOpenFilter.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                filterSlidingLayer.openLayer(true);
                btnCloseFilter.setVisibility(View.VISIBLE);

                return true;
            }
        });


        btnCloseFilter.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                filterSlidingLayer.closeLayer(true);
                btnOpenFilter.setVisibility(View.VISIBLE);

                return true;
            }
        });


        btnFindPath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sendRequest(v);
                textOrigin.setText("");
                textDestination.setText("");


            }
        });




        buildGoogleApiClient();// Create an instance of GoogleAPIClient.
        home_button = (Button)findViewById(R.id.home_button);
        home_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                didTapButton(view);
                Intent i = new Intent(view.getContext(), StartMenu.class);
                startActivity(i);
            }
        });



    }


    private void sendRequest(View v) {
        String origin = textOrigin.getText().toString();
        String destination = textDestination.getText().toString();
        if (origin.isEmpty()) {
            Toast.makeText(v.getContext(), "נא הזינו מקור", Toast.LENGTH_SHORT).show();
            return;
        }
        if (destination.isEmpty()) {
            Toast.makeText(v.getContext(), "נא הזינו יעד", Toast.LENGTH_SHORT).show();
            return;
        }


        LatLng value_origin= null;
        LatLng value_dest= null;
        List<Marker> markerList = MapsActivity.getMarkerList();

        for (Marker marker :markerList)
        {
            if (marker.getTitle().equalsIgnoreCase(origin)) {
                value_origin = marker.getPosition();
            }else if (marker.getTitle().equalsIgnoreCase(destination)){
                value_dest = marker.getPosition();
            }

        }
        if ((value_origin != null) &&(value_dest != null) ) {
            Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                    Uri.parse("http://maps.google.com/maps?saddr=" + value_origin.latitude +","+ value_origin.longitude+ "&daddr="+value_dest.latitude+","+ value_dest.longitude));
            startActivity(intent);

        }else if ((value_origin == null) && (value_dest == null)){
            Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                    Uri.parse("http://maps.google.com/maps?saddr=" + origin + "&daddr="+ destination));
            startActivity(intent);

        } else if ((value_origin == null) && (value_dest != null)){
            Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                    Uri.parse("http://maps.google.com/maps?saddr=" + origin + "&daddr="+value_dest.latitude+","+ value_dest.longitude));
            startActivity(intent);


        } else if ((value_dest == null) && (value_origin != null)){
            Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                    Uri.parse("http://maps.google.com/maps?saddr=" + value_origin.latitude +","+ value_origin.longitude+ "&daddr="+ destination));
            startActivity(intent);
        }




    }

    public void didTapButton(View view) {
        final Animation myAnim = AnimationUtils.loadAnimation(this, R.anim.bounce);
        BounceInterpolator interpolator = new BounceInterpolator(0.5, 50);
        myAnim.setInterpolator(interpolator);
        home_button.startAnimation(myAnim);
    }



    /**
     * View binding
     */
    private void bindViews() {

        filterSlidingLayer = (SlidingLayer) findViewById(R.id.slidingLayer2);
    }

    /**
     * Initializes the origin state of the layer
     */
    private void initState() {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        setupSlidingLayerPosition();
        setupSlidingLayerTransform(prefs.getString("layer_transform", "none"));

        setupShadow(prefs.getBoolean("layer_has_shadow", true));
        setupLayerOffset(prefs.getBoolean("layer_has_offset", false));
        setupPreviewMode(prefs.getBoolean("preview_mode_enabled", false));
    }


    private void setupSlidingLayerPosition() {

        RelativeLayout.LayoutParams rlp2 = (RelativeLayout.LayoutParams) filterSlidingLayer.getLayoutParams();

        filterSlidingLayer.setStickTo(SlidingLayer.STICK_TO_BOTTOM);
        rlp2.width = RelativeLayout.LayoutParams.MATCH_PARENT;
        rlp2.height = getResources().getDimensionPixelSize(R.dimen.filter_layer_size);
        filterSlidingLayer.setLayoutParams(rlp2);

    }

    private void setupSlidingLayerTransform(String layerTransform) {

        LayerTransformer transformer;

        switch (layerTransform) {
            case "alpha":
                transformer = new AlphaTransformer();
                break;
            case "rotation":
                transformer = new RotationTransformer();
                break;
            case "slide":
                transformer = new SlideJoyTransformer();
                break;
            default:
                return;
        }
;
        filterSlidingLayer.setLayerTransformer(transformer);
    }

    private void setupShadow(boolean enabled) {
        if (enabled) {
            filterSlidingLayer.setShadowSizeRes(R.dimen.shadow_size);

        } else {
            filterSlidingLayer.setShadowSize(0);
            filterSlidingLayer.setShadowDrawable(null);
        }
    }

    private void setupLayerOffset(boolean enabled) {
        int offsetDistance = enabled ? getResources().getDimensionPixelOffset(R.dimen.offset_distance) : 0;
        filterSlidingLayer.setOffsetDistance(offsetDistance);
    }

    private void setupPreviewMode(boolean enabled) {
        int previewOffset = enabled ? getResources().getDimensionPixelOffset(R.dimen.preview_offset_distance) : -1;
        filterSlidingLayer.setPreviewOffsetDistance(previewOffset);

    }




    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                if (filterSlidingLayer.isOpened()) {
                    filterSlidingLayer.closeLayer(true);
                    return true;
                }
            default:
                return super.onKeyDown(keyCode, event);
        }
    }



    protected synchronized void buildGoogleApiClient() {
        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            createLocationRequest();
        }

        if(mMyLocation != null){
            mMyLocation.remove();
        }
        mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if(mCurrentLocation != null){
            LatLng MYLOCATION = new LatLng(mCurrentLocation.getLatitude(),mCurrentLocation.getLongitude());
            mMyLocation = mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA))
                    .position(MYLOCATION)
                    .title("המיקום שלי"));
            mMyLocation.setTag("current place");
            MarkerCustomized myLocationMarker = new MarkerCustomized("המיקום שלי","",MYLOCATION,"","","");
            customizedMarkersMap.put("המיקום שלי", myLocationMarker);
        }
        if (mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (mLocationRequest != null)
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            else
                createLocationRequest();
        }
    }
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case RESULT_OK:
                        Log.e("Settings", "Result OK");
                        mRequestingLocationUpdates = true;
                        break;
                    case RESULT_CANCELED:
                        Log.e("Settings", "Result Cancel");
                        mRequestingLocationUpdates = false;
                        break;
                }
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        navigationSlidingPanel.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        btnOpenNav.setVisibility(View.VISIBLE);
        btnCloseNav.setVisibility(View.GONE);
        if (mGoogleApiClient.isConnected() && mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Unregister receiver on destroy
    }
    /* On Request permission method to check the permisison is granted or not for Marshmallow+ Devices */
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
                        buildGoogleApiClient();
                    }
                    createLocationRequest();
                }
                return;
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;

        if (mMyLocation != null) {
            mMyLocation.remove();
        }
        LatLng MYLOCATION = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(MYLOCATION);
        markerOptions.title("המיקום שלי");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        mMyLocation = mMap.addMarker(markerOptions);
        mMyLocation.setTag("current place");
        Toast.makeText(this,"Location Changed",Toast.LENGTH_SHORT).show();

        zoomToCurrentLocation();

        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }

    }

    protected void zoomToCurrentLocation(){
        //zoom to current position:
        if(mMyLocation!=null) {
            CameraPosition cameraPosition = new CameraPosition.Builder().target(mMyLocation.getPosition()).zoom(14).build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setPadding(0, 90, 10, 0);
        if (ContextCompat.checkSelfPermission(MapsActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED){
            mMap.setMyLocationEnabled(true);
        }

        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                if (mGoogleApiClient != null) {
                    if (ContextCompat.checkSelfPermission(MapsActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        if(mLocationRequest==null)
                        {
                            createLocationRequest();
                        }
                        Toast.makeText(MapsActivity.this, "Go to current Location", Toast.LENGTH_SHORT).show();
                        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, MapsActivity.this);
                        zoomToCurrentLocation();
                    }
                }
                return false;
            }
        });


        zoom = new LatLng(32.112582, 34.805218);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(zoom,15));



        for (MarkerCustomized markerCustomized: customizedMarkersMap.values()) {
            String name =  markerCustomized.getName();
            if (name.equals("המיקום שלי")){ //important
                continue;
            }
            String type =  markerCustomized.getType();
            LatLng position = markerCustomized.getPosition();
            String info = markerCustomized.getInfo();

            int logoId = 0;
            try {
                Class res = R.drawable.class;
                Field field = res.getField(type);
                logoId = field.getInt(null);
            }
            catch (Exception e) {
                Log.e("MyTag", "Failure to get drawable id.", e);
            }

            Marker marker = mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(logoId))
                    .position(position)
                    .title(name)
                    .snippet(info));

            markerList.add(marker);
            marker.setTag(type);


        }
        for(Marker marker:markerList){
            if ((!marker.getTag().equals("building")) &&(!marker.getTag().equals("library"))){
                marker.setVisible(false);
            }
        }

        mMap.setInfoWindowAdapter(new MyInfoWindowAdapter());
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {

            @Override
            public void onInfoWindowClick(Marker marker) {
                String name = marker.getTitle();
                MarkerCustomized m = customizedMarkersMap.get(name);
                String url = m.getUrl();

                if (!url.equals("")) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                }
            }

        });

    }

    public void onCheckSelect(View view){
        boolean checked = ((CheckBox) view).isChecked();
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(zoom,15));
        switch (view.getId()) {
            case R.id.chooseAll:
                if (checked) {
                    for (Marker marker : markerList) {
                        marker.setVisible(true);
                        ((CheckBox)findViewById(R.id.food)).setChecked(true);
                        ((CheckBox)findViewById(R.id.gate)).setChecked(true);
                        ((CheckBox)findViewById(R.id.lib)).setChecked(true);
                        ((CheckBox)findViewById(R.id.building)).setChecked(true);
                        ((CheckBox)findViewById(R.id.emon)).setChecked(true);
                        ((CheckBox)findViewById(R.id.parking)).setChecked(true);
                        ((CheckBox)findViewById(R.id.sport)).setChecked(true);
                        ((CheckBox)findViewById(R.id.general)).setChecked(true);
                        ((CheckBox)findViewById(R.id.dorms)).setChecked(true);
                    }
                } else {
                    for (Marker marker : markerList) {
                        marker.setVisible(false);
                    }
                    ((CheckBox)findViewById(R.id.food)).setChecked(false);
                    ((CheckBox)findViewById(R.id.gate)).setChecked(false);
                    ((CheckBox)findViewById(R.id.lib)).setChecked(false);
                    ((CheckBox)findViewById(R.id.building)).setChecked(false);
                    ((CheckBox)findViewById(R.id.emon)).setChecked(false);
                    ((CheckBox)findViewById(R.id.parking)).setChecked(false);
                    ((CheckBox)findViewById(R.id.sport)).setChecked(false);
                    ((CheckBox)findViewById(R.id.general)).setChecked(false);
                    ((CheckBox)findViewById(R.id.dorms)).setChecked(false);
                }
            case R.id.food:
                if (checked) {
                    for (Marker marker : markerList) {
                        if (marker.getTag().equals("food")) {
                            marker.setVisible(true);
                        }
                    }
                } else {
                    for (Marker marker : markerList) {
                        if (marker.getTag().equals("food")) {
                            marker.setVisible(false);
                        }
                        ((CheckBox)findViewById(R.id.chooseAll)).setChecked(false);
                    }
                }
                break;
            case R.id.parking:
                if (checked) {
                    for (Marker marker : markerList) {
                        if (marker.getTag().equals("parking")) {
                            marker.setVisible(true);
                        }
                    }
                } else {
                    for (Marker marker : markerList) {
                        if (marker.getTag().equals("parking")) {
                            marker.setVisible(false);
                        }
                        ((CheckBox)findViewById(R.id.chooseAll)).setChecked(false);
                    }
                }
                break;
            case R.id.gate:
                if (checked) {
                    for (Marker marker : markerList) {
                        if (marker.getTag().equals("gate")) {
                            marker.setVisible(true);
                        }
                    }
                } else {
                    for (Marker marker : markerList) {
                        if (marker.getTag().equals("gate")) {
                            marker.setVisible(false);
                        }
                        ((CheckBox)findViewById(R.id.chooseAll)).setChecked(false);
                    }
                }
                break;
            case R.id.lib:
                if (checked) {
                    for (Marker marker : markerList) {
                        if (marker.getTag().equals("library")) {
                            marker.setVisible(true);
                        }
                    }
                } else {
                    for (Marker marker : markerList) {
                        if (marker.getTag().equals("library")) {
                            marker.setVisible(false);
                        }
                        ((CheckBox)findViewById(R.id.chooseAll)).setChecked(false);
                    }
                }
                break;
            case R.id.building:
                if (checked) {
                    for (Marker marker : markerList) {
                        if (marker.getTag().equals("building")) {
                            marker.setVisible(true);
                        }
                    }
                } else {
                    for (Marker marker : markerList) {
                        if (marker.getTag().equals("building")) {
                            marker.setVisible(false);
                        }
                        ((CheckBox)findViewById(R.id.chooseAll)).setChecked(false);
                    }
                }
                break;
            case R.id.sport:
                if (checked) {
                    for (Marker marker : markerList) {
                        if (marker.getTag().equals("fitness")) {
                            marker.setVisible(true);
                        }
                    }
                } else {
                    for (Marker marker : markerList) {
                        if (marker.getTag().equals("fitness")) {
                            marker.setVisible(false);
                        }
                        ((CheckBox)findViewById(R.id.chooseAll)).setChecked(false);
                    }
                }
                break;
            case R.id.general:
                if (checked) {
                    for (Marker marker : markerList) {
                        if (marker.getTag().equals("general")) {
                            marker.setVisible(true);
                        }
                    }
                } else {
                    for (Marker marker : markerList) {
                        if (marker.getTag().equals("general")) {
                            marker.setVisible(false);
                        }
                        ((CheckBox)findViewById(R.id.chooseAll)).setChecked(false);
                    }
                }
                break;
            case R.id.dorms:
                if (checked) {
                    for (Marker marker : markerList) {
                        if (marker.getTag().equals("dorms")) {
                            marker.setVisible(true);
                        }
                    }
                } else {
                    for (Marker marker : markerList) {
                        if (marker.getTag().equals("dorms")) {
                            marker.setVisible(false);
                        }
                        ((CheckBox)findViewById(R.id.chooseAll)).setChecked(false);
                    }
                }
                break;
            case R.id.emon:
                if (checked) {
                    for (Marker marker : markerList) {
                        if (marker.getTag().equals("amun")) {
                            marker.setVisible(true);
                        }
                    }
                } else {
                    for (Marker marker : markerList) {
                        if (marker.getTag().equals("amun")) {
                            marker.setVisible(false);
                        }
                        ((CheckBox)findViewById(R.id.chooseAll)).setChecked(false);
                    }
                }
                break;
        }
    }

    @Override
    protected  void  onRestart(){
        super.onRestart();


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.demo, menu);
        MenuItem item = menu.findItem(R.id.action_toggle);
        if (navigationSlidingPanel != null) {
            if (navigationSlidingPanel.getPanelState() == SlidingUpPanelLayout.PanelState.HIDDEN) {
                item.setTitle(R.string.action_show);
            } else {
                item.setTitle(R.string.action_hide);
            }
        }
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_toggle: {
                if (navigationSlidingPanel != null) {
                    if (navigationSlidingPanel.getPanelState() != SlidingUpPanelLayout.PanelState.HIDDEN) {
                        navigationSlidingPanel.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
                        item.setTitle(R.string.action_show);
                    } else {
                        navigationSlidingPanel.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                        item.setTitle(R.string.action_hide);
                    }
                }
                return true;
            }
            case R.id.action_anchor: {
                if (navigationSlidingPanel != null) {
                    if (navigationSlidingPanel.getAnchorPoint() == 1.0f) {
                        navigationSlidingPanel.setAnchorPoint(0.7f);
                        navigationSlidingPanel.setPanelState(SlidingUpPanelLayout.PanelState.ANCHORED);
                        item.setTitle(R.string.action_anchor_disable);
                    } else {
                        navigationSlidingPanel.setAnchorPoint(1.0f);
                        navigationSlidingPanel.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                        item.setTitle(R.string.action_anchor_enable);
                    }
                }
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}