package com.example.easytau;

/**
 * Created by TT on 1/17/2017.
 */

public class Course {
    private String course_name;
    private String course_number;
    private String faculty;
    private String school;
    private String semester;
    private String building;
    private String room;
    private String course_type; //lesson or recitation
    private String teacher;
    private String day;
    private String hours;


    public String getCourseNumber() {
        return course_number;
    }

    public void setCourseNumber(String course_number) {
        this.course_number = course_number;
    }

    public String getCourseName() {
        return course_name;
    }

    public void setCourseName(String course_name) {
        this.course_name = course_name;
    }

    public String getFaculty() {
        return faculty;
    }

    public void setFaculty(String faculty) {
        this.faculty = faculty;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getBuilding() {
        return building;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public String getCourseType() {
        return course_type;
    }

    public void setCourseType(String course_type) {
        this.course_type = course_type;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getHours() {
        return hours;
    }

    public void setHours(String hours) {
        this.hours = hours;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }
}


