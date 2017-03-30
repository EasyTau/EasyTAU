package com.example.easytau;

import com.mongodb.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.content.*;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by royn on 19/01/2017.
 */
public class FillTotalInfo  {
    static Boolean isFilledDB = false;
    static Boolean isFilledDBPhone =false;
    static Boolean isFilledDBCalender =false;
    public static void fill() {

        try {

            //        long start = System.nanoTime();
            // Get current time
            long start = System.currentTimeMillis();

            // To connect to mongodb server
            MongoClientURI mongoClientURI = new MongoClientURI("mongodb://easytau:easytau@ds159737.mlab.com:59737/easytau");
            MongoClient mongoClient = new MongoClient(mongoClientURI);

            // Now connect to databases
            DB db = mongoClient.getDB("easytau");


            System.out.println("Connect to database successfully");

            ///////// DB DOWNLOAD PART 1

            DBCollection coll = db.getCollection("everything5");

            System.out.println("Collection everything5 selected successfully");

            DBCursor cursor = coll.find();

            Map<String, Faculty> faculties = new HashMap<String, Faculty>();
            Map<String, PhoneCategory> phoneCategories = new HashMap<String, PhoneCategory>();
            List<TauEvent> tauEvents = new ArrayList<TauEvent>();
            TotalInfoFromDb.beginSchoolsCounter();
            TotalInfoFromDb.beginPhoneNumbersCounter();
            TotalInfoFromDb.beginPhoneUnitsCounter();

            for (DBObject dbObject : coll.find().toArray()) {

                String typeStr = ((String) " " + dbObject.get("type") + " ");
                int type = 0;
                if (typeStr.contains("courses_schools_faculties")) {
                    type = 1;
                }
                if (typeStr.contains("unitsphones")) {
                    type = 2;
                }
                if (typeStr.contains("tau_calendar")) {
                    type = 3;
                }
                switch (type) {
                    case 1:
                        String courseStr = (String) dbObject.get("course_name");
                        String facultyStr = (String) dbObject.get("faculty");
                        String schoolStr = (String) dbObject.get("school");

                        Faculty faculty = faculties.get(facultyStr);
                        if (faculty != null) { //faculty already exists
                            Map<String, School> schools = faculty.getSchools();
                            School school = schools.get(schoolStr);
                            if (school != null) { //school already exists
                                school.addCourse(courseStr);
                            } else { //school is new
                                school = new School();
                                school.setName(schoolStr);
                                school.addCourse(courseStr);
                                faculty.addSchool(schoolStr, school);
                                TotalInfoFromDb.incrementSchoolsCounter();
                            }
                        } else { //faculty is new (so school must be new also)
                            faculty = new Faculty();
                            faculty.setName(facultyStr);
                            School school = new School();
                            school.setName(schoolStr);
                            school.addCourse(courseStr);
                            faculty.addSchool(schoolStr, school);
                            faculties.put(facultyStr, faculty);
                            TotalInfoFromDb.incrementSchoolsCounter();
                        }
                        break;
                    case 2:
                        PhoneNumber phoneNumber = new PhoneNumber();
                        phoneNumber.setName((String) dbObject.get("name"));
                        phoneNumber.setFax((String) dbObject.get("fax"));
                        String phoneUnitStr = (String) dbObject.get("unit");
                        phoneNumber.setUnit(phoneUnitStr);
                        String phoneCategoryStr = (String) dbObject.get("category");
                        phoneNumber.setCategory(phoneCategoryStr);
                        phoneNumber.setInnerPhone((String) dbObject.get("inner_phone"));

                        PhoneCategory phoneCategory = phoneCategories.get(phoneCategoryStr);
                        if (phoneCategory != null) { //phoneCategory already exists
                            Map<String, PhoneUnit> units = phoneCategory.getUnits();
                            PhoneUnit phoneUnit = units.get(phoneUnitStr);
                            if (phoneUnit != null) { //unit already exists
                                phoneUnit.addPhoneNumber(phoneNumber);
                                TotalInfoFromDb.incrementPhoneNumbersCounter();
                            } else { //unit is new
                                phoneUnit = new PhoneUnit();
                                phoneUnit.setName(phoneUnitStr);
                                phoneUnit.addPhoneNumber(phoneNumber);
                                phoneCategory.addUnit(phoneUnitStr, phoneUnit);
                                TotalInfoFromDb.incrementPhoneNumbersCounter();
                                TotalInfoFromDb.incrementPhoneUnitsCounter();
                            }
                        } else { //phoneCategory is new (so phoneUnit must be new also)
                            phoneCategory = new PhoneCategory();
                            phoneCategory.setName(phoneCategoryStr);
                            PhoneUnit phoneUnit = new PhoneUnit();
                            phoneUnit.setName(phoneUnitStr);
                            phoneUnit.addPhoneNumber(phoneNumber);
                            phoneCategory.addUnit(phoneUnitStr, phoneUnit);
                            phoneCategories.put(phoneCategoryStr, phoneCategory);
                            TotalInfoFromDb.incrementPhoneNumbersCounter();
                            TotalInfoFromDb.incrementPhoneUnitsCounter();
                        }

                        isFilledDBPhone = true;


                        break;
                    case 3:
                        TauEvent tauEvent = new TauEvent();
                        tauEvent.setEvent((String) dbObject.get("event"));
                        tauEvent.setDate((String) dbObject.get("date"));
                        tauEvent.setHebrewDate((String) dbObject.get("hebrew_date"));
                        tauEvents.add(tauEvent);
                        isFilledDBCalender=true;
                        break;
                    default:
                        break;
                }
            }
            TotalInfoFromDb.setFaculties(faculties);
            TotalInfoFromDb.setPhoneCategories(phoneCategories);
            TotalInfoFromDb.setTauEvents(tauEvents);

//        long elapsedTime = System.nanoTime() - start;
//        System.out.println("filling took time: " + elapsedTime);
            // Get elapsed time in milliseconds
            long elapsedTimeMillis = System.currentTimeMillis() - start;
// Get elapsed time in seconds
            float elapsedTimeSec = elapsedTimeMillis / 1000F;
            System.out.println("filling1 took time milis: " + elapsedTimeMillis);
            System.out.println("filling1 took time seconds: " + elapsedTimeSec);
            System.out.println("");
            System.out.println("");


            ///////// DB DOWNLOAD PART 2
            coll = db.getCollection("courses");
            System.out.println("Collection courses selected successfully");

            cursor = coll.find();

            Map<String, ArrayList<Course>> courses = new HashMap<String, ArrayList<Course>>();
            TotalInfoFromDb.beginCoursesCounter();

            for (DBObject dbObject : coll.find().toArray()) {
                Course course = new Course();
                course.setCourseNumber((String) dbObject.get("course_number"));
                String courseStr = (String) dbObject.get("course_name");
                course.setCourseName(courseStr);
                course.setDay((String) dbObject.get("day"));
                course.setCourseType((String) dbObject.get("course_type"));
                course.setFaculty((String) dbObject.get("faculty"));
                course.setSchool((String) dbObject.get("school"));
                course.setTeacher((String) dbObject.get("teacher"));
                course.setSemester((String) dbObject.get("semester"));
                course.setBuilding((String) dbObject.get("building"));
                course.setHours((String) dbObject.get("hours"));
                course.setRoom((String) dbObject.get("room"));

                ArrayList<Course> courseLessons = courses.get(courseStr);
                if (courseLessons == null) { //this is the first lesson found of this course
                    courseLessons = new ArrayList<Course>();
                }
                courseLessons.add(course); //add this lesson to the lessons of the course
                courses.put(courseStr, courseLessons); //update the map with the new courseLessons
                TotalInfoFromDb.incrementCoursesCounter();
            }
            TotalInfoFromDb.setCourses(courses);

            //        long start = System.nanoTime();
            // Get current time
            isFilledDB = true;

            long start2 = System.currentTimeMillis();

            //        long elapsedTime = System.nanoTime() - start;
//        System.out.println("filling took time: " + elapsedTime);
            // Get elapsed time in milliseconds
            long elapsedTimeMillis2 = System.currentTimeMillis() - start2;
// Get elapsed time in seconds
            float elapsedTimeSec2 = elapsedTimeMillis2 / 1000F;
            System.out.println("filling2 took time milis: " + elapsedTimeMillis);
            System.out.println("filling2 took time seconds: " + elapsedTimeSec);
            System.out.println("");
            System.out.println("");


        }catch(Exception e){
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
        }

    }

}