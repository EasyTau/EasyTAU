package com.example.easytau;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.mongodb.util.JSONParseException;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.simple.JSONArray;
import org.json.JSONException;
import org.json.simple.JSONObject;
import org.json.*;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Created by TT on 12/8/2016.
 */
public class SplashActivity extends AppCompatActivity implements ParkingLotListener{

    private int SPLASH_DISPLAY_LENGTH = 15000;
    private boolean filesStatus;
    SharedPreferences settings;
    AlertDialog alert11;
    public static int processPhonesStage = 0;
    public static int processCoursesStage = 0;
    public static int processEventsStage = 0;
    static boolean is_first = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        filesStatus = false; //are there internal files ready to be used?
        settings =  getPreferences(MODE_PRIVATE);


        //phonesFileReady, coursesFileReady, eventsFileReady
        if (settings.contains("phonesFileReady") && settings.contains("coursesFileReady") && settings.contains("eventsFileReady")){
           /* their initial value is true */
            filesStatus = true;
        }
        if (!filesStatus) {
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    is_first=true;
                    URL url = null;
                    try {
                        url = new URL("https://gisn.tel-aviv.gov.il/wsgis/service.asmx/GetLayer?layerCode=970&layerWhere=&xmin=180837&ymin=667485&xmax=181937&ymax=669428&projection=");
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                    String xmlStr = null;
                    try {
                        xmlStr = UrlManager.getUrlResponse(url);
                        xmlStr = xmlStr.replaceAll("<?xml version=\"1.0\" encoding=\"utf-8\"?>", "");
                        xmlStr = xmlStr.replaceAll("<string xmlns=\"http://tempuri.org/\">", "");
                        xmlStr = xmlStr.replaceAll("</string>", "");
                        if (xmlStr.contains("<?xml")) {
                            xmlStr = xmlStr.substring(xmlStr.indexOf("?>") + 2);
                        }


                        org.json.JSONObject json = new org.json.JSONObject(xmlStr);

                        org.json.JSONArray ParkingLotsArr = json.getJSONArray("features");
                        for (int i = 0; i < ParkingLotsArr.length(); i++) {
                            org.json.JSONObject jsonAttributes = ParkingLotsArr.getJSONObject(i);

                            org.json.JSONObject attribute = jsonAttributes.getJSONObject("attributes");
                            if ((!(attribute.getString("shem_chenyon")).equals("גולפיטק - חברת גני יהושוע")) && (!(attribute.getString("shem_chenyon")).equals("סלודור")) && (!(attribute.getString("shem_chenyon")).equals("טאגור"))) {
                                ParkingLot parkingLot = new ParkingLot();
                                parkingLot.setName(attribute.getString("shem_chenyon"));
                                parkingLot.setAddress(attribute.getString("ktovet"));
                                parkingLot.setPrice_day(attribute.getString("taarif_yom"));
                                parkingLot.setPrice_night(attribute.getString(("taarif_layla")));
                                parkingLot.setPrice_allday(attribute.getString("taarif_yomi"));
                                parkingLot.setNote(attribute.getString("hearot_taarif"));
                                parkingLot.setStatus(attribute.getString("status_chenyon"));
                                parkingLot.setStatus_time(attribute.getString("tr_status_chenyon"));
                                TotalInfoFromDb.addParkingLotListM(parkingLot);
                            }
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });
        }

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                String jsonText;
                JSONParser parser = new JSONParser();

                try {
                    InputStream is = getAssets().open("parking.json");
                    int size = is.available();
                    byte[] buffer = new byte[size];
                    is.read(buffer);
                    is.close();
                    jsonText = new String(buffer, "UTF-8");

                    JSONArray allPlaces = (JSONArray) parser.parse(jsonText);

                    for (int i = 0; i < allPlaces.size(); i++) {
                        org.json.simple.JSONObject placeObj = (org.json.simple.JSONObject) allPlaces.get(i);

                        String parkingNav = (String) placeObj.get("parkingLot");
                        if (parkingNav != null) {
                            float coord1 = (Float.valueOf((String) placeObj.get("coord1")));
                            float coord2 = (Float.valueOf((String) placeObj.get("coord2")));
                            LatLng positionCoords = new LatLng(coord1, coord2);

                            TotalInfoFromDb.addMapParking(parkingNav, positionCoords);

                        }
                    }

                } catch (org.json.simple.parser.ParseException e) {
                    e.printStackTrace();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }



            }


        });


        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                if (filesStatus) { //internal files are ready to be used - use them

                    Calendar cal = Calendar.getInstance();
                    int currYear = cal.get(Calendar.YEAR);

                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

                    try {
                        Date currDate = formatter.parse(formatter.format(new Date()));

                        String updateDateStr=  ""+cal.get(Calendar.YEAR)+"-"+"09-10";

                        Date updateDate = formatter.parse(updateDateStr);

                        if (updateDate.compareTo(currDate) <= 0){ //means currDate bigger than updateDate
                            if (!settings.contains(currYear+"Update")) { //means  need to update
                                raiseInfoMessage();
                                FillTotalInfo.fill();
                                saveCoursesDataInFile();
                                savePhonesDataInFile();
                                saveEventsDataInFile();


                            }else{

                                SPLASH_DISPLAY_LENGTH = 2000;
                                processPhonesStage = 1;
                                processCoursesStage = 1;
                                processEventsStage = 1;
                                FileReader filesReader = readFromFiles("Events");
                                readEventsDataFromFile(filesReader);
                                processEventsStage = 2;
                                FillTotalInfo.isFilledDBCalender = true;
                                filesReader = readFromFiles("Courses");
                                readCoursesDataFromFile(filesReader);
                                processCoursesStage = 2; //done
                                FillTotalInfo.isFilledDB = true;
                                filesReader = readFromFiles("Phones");
                                readPhonesDataFromFile(filesReader);
                                processPhonesStage = 2; //done
                                FillTotalInfo.isFilledDBPhone = true;

                            }
                        }else{

                            SPLASH_DISPLAY_LENGTH = 2000;
                            processPhonesStage = 1;
                            processCoursesStage = 1;
                            processEventsStage = 1;
                            FileReader filesReader = readFromFiles("Events");
                            readEventsDataFromFile(filesReader);
                            processEventsStage = 2;
                            FillTotalInfo.isFilledDBCalender = true;
                            filesReader = readFromFiles("Courses");
                            readCoursesDataFromFile(filesReader);
                            processCoursesStage = 2; //done
                            FillTotalInfo.isFilledDB = true;
                            filesReader = readFromFiles("Phones");
                            readPhonesDataFromFile(filesReader);
                            processPhonesStage = 2; //done
                            FillTotalInfo.isFilledDBPhone = true;

                        }
                    } catch (java.text.ParseException e) {
                        e.printStackTrace();
                    }




                } else { //connect to db and create internal files
                    raiseInfoMessage();
                    FillTotalInfo.fill();
                    saveCoursesDataInFile();
                    savePhonesDataInFile();
                    saveEventsDataInFile();

                }

            }
        });
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        StartAnimations();
        ImageView logo = (ImageView)findViewById(R.id.logo);

        Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
        logo.startAnimation(shake);

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                Intent mainIntent = new Intent(SplashActivity.this, StartMenu.class);
                SplashActivity.this.startActivity(mainIntent);
                SplashActivity.this.finish();
            }
        }, SPLASH_DISPLAY_LENGTH);





    }

    private void raiseInfoMessage(){
        new Thread() {
            public void run() {
                SplashActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        AlertDialog.Builder builderSingle = new AlertDialog.Builder(SplashActivity.this);
                        LayoutInflater inflater = getLayoutInflater();
                        View view = inflater.inflate(R.layout.dialog_first_entrance, null);
                        builderSingle.setView(view);

                        builderSingle.setCancelable(true);
                        builderSingle.setNegativeButton("אישור", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }

                            ;
                        });

                        alert11 = builderSingle.create();
                        alert11.show();

                    }
                });
            }
        }.start();
    }



    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        if (alert11!=null && alert11.isShowing()){
            alert11.dismiss();
        }
    }

    private void StartAnimations() {
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.alpha);
        anim.reset();
        LinearLayout l=(LinearLayout) findViewById(R.id.lin_lay);
        l.clearAnimation();
        l.startAnimation(anim);

        anim = AnimationUtils.loadAnimation(this, R.anim.translate);
        anim.reset();
        ImageView iv = (ImageView) findViewById(R.id.logo);
        iv.clearAnimation();
        iv.startAnimation(anim);

        new CountDownTimer(3000, 1000) {

            public void onTick(long millisUntilFinished) {

            }

            public void onFinish() {
                AVLoadingIndicatorView avi= (AVLoadingIndicatorView) findViewById(R.id.avi);
                avi.setVisibility(View.VISIBLE);
                avi.show();
            }
        }.start();


    }


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




    public static void readPhonesDataFromFile(FileReader reader){
        JSONParser parser = new JSONParser();
        try {
            JSONArray categoryArray = (JSONArray) parser.parse(reader);
            Map<String,PhoneCategory> mapCategory = new HashMap<String,PhoneCategory>();
            for(int i=0;i<categoryArray.size();i++){
                JSONObject categoryObj = (JSONObject)categoryArray.get(i);
                PhoneCategory phoneCategory = new PhoneCategory();
                phoneCategory.setUnits(new HashMap<String, PhoneUnit>());
                String nameCat = (String)categoryObj.get("name");
                JSONArray unitArray =(JSONArray)categoryObj.get("units");

                for(int j=0;j<unitArray.size();j++){
                    JSONObject unitObj = (JSONObject)unitArray.get(j);
                    PhoneUnit phoneUnit = new PhoneUnit();
                    String nameUnit = (String) unitObj.get("name");
                    JSONArray phoneArray =(JSONArray)unitObj.get("phones");
                    ArrayList<PhoneNumber> phoneNumbers = new ArrayList<>();

                    for(int k=0;k<phoneArray.size();k++){

                        JSONObject phonesObj = (JSONObject)phoneArray.get(k);
                        PhoneNumber phoneNumber = new PhoneNumber();
                        phoneNumber.setName((String)phonesObj.get("name"));
                        phoneNumber.setOuterPhone((String)phonesObj.get("outerPhone"));
                        phoneNumber.setCategory((String)phonesObj.get("category"));
                        phoneNumber.setFax((String)phonesObj.get("fax"));
                        phoneNumber.setInnerPhone((String)phonesObj.get("innerPhone"));
                        phoneNumber.setId((String)phonesObj.get("id"));
                        phoneNumber.setUnit((String)phonesObj.get("unit"));


                        phoneNumbers.add(phoneNumber);

                    }
                    phoneUnit.setName(nameUnit);
                    phoneUnit.setPhoneNumbers(phoneNumbers);
                    phoneCategory.addUnit(nameUnit,phoneUnit);
                }

                phoneCategory.setName(nameCat);
                mapCategory.put(nameCat,phoneCategory);

            }
            TotalInfoFromDb.setPhoneCategories(mapCategory);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    private void savePhonesDataInFile(){

        File file = new File(getFilesDir(), "tauPhones.json");
        JSONArray categoryArray = new JSONArray();

        for(PhoneCategory category : TotalInfoFromDb.getPhoneCategories().values()){

            JSONObject categoryObj = new JSONObject();
            String categoryName = category.getName();
            JSONArray unitArray = new JSONArray();

            for (PhoneUnit unit : category.getUnits().values()) {

                JSONObject unitObj = new JSONObject();
                String unitName = unit.getName();
                JSONArray phoneNumberArray = new JSONArray();

                for (PhoneNumber phoneNumber : unit.getPhoneNumbers()) {

                    JSONObject phoneNumberObj = new JSONObject();
                    phoneNumberObj.put("name", phoneNumber.getName());
                    phoneNumberObj.put("unit", phoneNumber.getUnit());
                    phoneNumberObj.put("innerPhone", phoneNumber.getInnerPhone());
                    phoneNumberObj.put("category", phoneNumber.getCategory());
                    phoneNumberObj.put("fax", phoneNumber.getFax());
                    phoneNumberObj.put("id", phoneNumber.getId());
                    phoneNumberObj.put("outerPhone", phoneNumber.getOuterPhone());

                    phoneNumberArray.add(phoneNumberObj);



                }
                unitObj.put("name",unitName);
                unitObj.put("phones",phoneNumberArray);
                unitArray.add(unitObj);
            }
            categoryObj.put("name",categoryName);
            categoryObj.put("units",unitArray);
            categoryArray.add(categoryObj);

        }
        try {
            file.createNewFile();
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(categoryArray.toJSONString());
            fileWriter.flush();
            fileWriter.close();


            settings.edit().putBoolean("phonesFileReady", false).commit();

            Calendar cal = Calendar.getInstance();
            int currYear = cal.get(Calendar.YEAR);

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

            try {
                Date currDate = formatter.parse(formatter.format(new Date()));

                String updateDateStr=  ""+cal.get(Calendar.YEAR)+"-"+"09-10";

                Date updateDate = formatter.parse(updateDateStr);

                if (updateDate.compareTo(currDate) <= 0) { //means currDate bigger than updateDate
                    settings.edit().putBoolean(currYear+"Update", true).commit();
                }
            } catch (java.text.ParseException e) {
                e.printStackTrace();
            }


        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private void saveCoursesDataInFile() {
        try {

            File file = new File(getFilesDir(), "tauCourses.json");

            org.json.simple.JSONArray facultiesArray = new org.json.simple.JSONArray();

            for (Faculty faculty : TotalInfoFromDb.getFaculties().values()) {
                org.json.simple.JSONObject facultyObj = new org.json.simple.JSONObject();
                String facultyName = faculty.getName();

                org.json.simple.JSONArray schoolsInFacultyArray = new org.json.simple.JSONArray();
                for (School currentSchool : faculty.getSchools().values()) {
                    org.json.simple.JSONObject schoolObj = new org.json.simple.JSONObject();


                    String schoolName = currentSchool.getName();

                    org.json.simple.JSONArray coursesInSchoolArray = new org.json.simple.JSONArray();
                    for (String courseName: currentSchool.getCourses()){




                        List<Course> course = TotalInfoFromDb.getCourses().get(courseName);
                        if (course!=null) {
                            for (Course course1 : course) {
                                org.json.simple.JSONObject courseObj = new org.json.simple.JSONObject();
                                courseObj.put("name", course1.getCourseName());
                                courseObj.put("faculty", course1.getFaculty());
                                courseObj.put("school", course1.getSchool());
                                courseObj.put("semester", course1.getSemester());
                                courseObj.put("number", course1.getCourseNumber());
                                courseObj.put("type", course1.getCourseType());
                                courseObj.put("teacher", course1.getTeacher());
                                courseObj.put("day", course1.getDay());
                                courseObj.put("hours", course1.getHours());
                                courseObj.put("building", course1.getBuilding());
                                courseObj.put("room", course1.getRoom());

                                coursesInSchoolArray.add(courseObj);

                            }

                        }
                    }
                    schoolObj.put("name", schoolName);
                    schoolObj.put("courses",coursesInSchoolArray);

                    schoolsInFacultyArray.add(schoolObj);

                }

                facultyObj.put("name", facultyName);
                facultyObj.put("schools", schoolsInFacultyArray);
                facultiesArray.add(facultyObj);
            }

            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(facultiesArray.toJSONString());
            fileWriter.flush();
            fileWriter.close();


            settings.edit().putBoolean("coursesFileReady", false).commit();

            Calendar cal = Calendar.getInstance();
            int currYear = cal.get(Calendar.YEAR);

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

            try {
                Date currDate = formatter.parse(formatter.format(new Date()));

                String updateDateStr=  ""+cal.get(Calendar.YEAR)+"-"+"09-10";

                Date updateDate = formatter.parse(updateDateStr);

                if (updateDate.compareTo(currDate) <= 0) { //means currDate bigger than updateDate
                    settings.edit().putBoolean(currYear+"Update", true).commit();
                }
            } catch (java.text.ParseException e) {
                e.printStackTrace();
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void readCoursesDataFromFile(FileReader reader){
        JSONParser parser = new JSONParser();

        try {

            JSONArray facultiesArray = (JSONArray) parser.parse(reader);

            Map<String, Faculty> faculties = new HashMap<String, Faculty>();
            Map<String, ArrayList<Course>> allCourses = new HashMap<>();
            TotalInfoFromDb.beginCoursesCounter();
            TotalInfoFromDb.beginSchoolsCounter();

            for (int i = 0; i < facultiesArray.size(); i++) {//go over all faculties
                Faculty faculty = new Faculty();
                org.json.simple.JSONObject facultyObj = (org.json.simple.JSONObject) facultiesArray.get(i);
                String facultyName = (String) facultyObj.get("name");

                faculty.setName(facultyName);
                org.json.simple.JSONArray schoolsInFacultyArray = (org.json.simple.JSONArray) facultyObj.get("schools");


                for (int j = 0; j < schoolsInFacultyArray.size(); j++) {
                    org.json.simple.JSONObject schoolObj = (org.json.simple.JSONObject) schoolsInFacultyArray.get(j);
                    String schoolName = (String) schoolObj.get("name");

                    School school = new School();
                    org.json.simple.JSONArray coursesInSchoolArray = (org.json.simple.JSONArray) schoolObj.get("courses");


                    ArrayList<String> coursesInSchool = new ArrayList<>();
                    for (int k = 0; k < coursesInSchoolArray.size(); k++) {
                        org.json.simple.JSONObject courseObj = (org.json.simple.JSONObject) coursesInSchoolArray.get(k);
                        Course course = new Course();
                        String courseName = (String) courseObj.get("name");
                        course.setCourseName(courseName);
                        course.setFaculty((String) courseObj.get("faculty"));
                        course.setSchool((String) courseObj.get("school"));
                        course.setSemester((String) courseObj.get("semester"));
                        course.setCourseNumber((String) courseObj.get("number"));
                        course.setCourseType((String) courseObj.get("type"));
                        course.setTeacher((String) courseObj.get("teacher"));
                        course.setDay((String) courseObj.get("day"));
                        course.setHours((String) courseObj.get("hours"));
                        course.setBuilding((String) courseObj.get("building"));
                        course.setRoom((String) courseObj.get("room"));

                        ArrayList<Course> courseLessons = allCourses.get(courseName);
                        if (courseLessons == null) { //this is the first lesson found of this course
                            courseLessons = new ArrayList<Course>();
                        }
                        courseLessons.add(course); //add this lesson to the lessons of the course

                        allCourses.put(courseName, courseLessons); //update the map with the new courseLessons
                        if (!coursesInSchool.contains(courseName)) {
                            coursesInSchool.add(courseName); //add the course
                        }

                        TotalInfoFromDb.incrementCoursesCounter();

                    }

                    school.setName(schoolName);
                    school.setCourses(coursesInSchool);
                    TotalInfoFromDb.incrementSchoolsCounter();
                    faculty.addSchool(schoolName, school);
                }


                faculties.put(facultyName, faculty);

            }

            TotalInfoFromDb.setCourses(allCourses);
            TotalInfoFromDb.setFaculties(faculties);


        } catch (org.json.simple.parser.ParseException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    private void saveEventsDataInFile() {
        try {
            File file = new File(getFilesDir(), "tauEvents.json");
            FileOutputStream fos = new FileOutputStream(file);

            org.json.simple.JSONArray eventsArray = new org.json.simple.JSONArray();

            for (TauEvent event : TotalInfoFromDb.getTauEvents()) {
                org.json.simple.JSONObject eventObj = new org.json.simple.JSONObject();

                eventObj.put("name", event.getEvent());
                eventObj.put("date", event.getDate());
                eventObj.put("hebrewdate", event.getHebrewDate());

                eventsArray.add(eventObj);

            }

            fos.write(eventsArray.toJSONString().getBytes());
            fos.flush();
            fos.close();

            settings.edit().putBoolean("eventsFileReady", false).commit();

            Calendar cal = Calendar.getInstance();
            int currYear = cal.get(Calendar.YEAR);

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

            try {
                Date currDate = formatter.parse(formatter.format(new Date()));

                String updateDateStr=  ""+cal.get(Calendar.YEAR)+"-"+"09-10";

                Date updateDate = formatter.parse(updateDateStr);

                if (updateDate.compareTo(currDate) <= 0) { //means currDate bigger than updateDate
                    settings.edit().putBoolean(currYear+"Update", true).commit();
                }
            } catch (java.text.ParseException e) {
                e.printStackTrace();
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void readEventsDataFromFile(FileReader reader){
        JSONParser parser = new JSONParser();

        try {


            List<TauEvent> tauEvents = new ArrayList<>();
            JSONArray eventsArray = (JSONArray) parser.parse(reader);


            for (int i = 0; i < eventsArray.size(); i++) {//go over all events
                TauEvent event = new TauEvent();
                org.json.simple.JSONObject eventObj = (org.json.simple.JSONObject) eventsArray.get(i);

                event.setEvent((String) eventObj.get("name"));
                event.setDate((String) eventObj.get("date"));
                event.setHebrewDate((String) eventObj.get("hebrewdate"));

                tauEvents.add(event);

            }
            TotalInfoFromDb.setTauEvents(tauEvents);

        } catch (org.json.simple.parser.ParseException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onParkingLotSuccess(List<ParkingLot> parkingLotList) {

    }
}

