package com.example.apnacr.Models;

import java.util.List;

public class Test {

    String id;
    String crid;
    String tid;
    String date;
    List<String>files;
    List<String>students;
    String desc;
    String time;
    long timeMillis;
    long marks;
public Test(){

}
    public Test(String id, String crid, String tid, String date, List<String> files, List<String> students, String desc,String time,long timeMillis,long marks) {
        this.id = id;
        this.crid = crid;
        this.tid = tid;
        this.date = date;
        this.files = files;
        this.students = students;
        this.desc = desc;
        this.time=time;
        this.marks=marks;
        this.timeMillis=timeMillis;
    }

    public long getMarks() {
        return marks;
    }

    public void setMarks(long marks) {
        this.marks = marks;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCrid() {
        return crid;
    }

    public void setCrid(String crid) {
        this.crid = crid;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<String> getFiles() {
        return files;
    }

    public void setFiles(List<String> files) {
        this.files = files;
    }

    public List<String> getStudents() {
        return students;
    }

    public void setStudents(List<String> students) {
        this.students = students;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public long getTimeMillis() {
        return timeMillis;
    }

    public void setTimeMillis(long timeMillis) {
        this.timeMillis = timeMillis;
    }
}
