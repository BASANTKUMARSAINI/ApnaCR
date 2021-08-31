package com.example.apnacr.Models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class ClassRoom implements Parcelable {
    List<String>assignments;
    String id;
    String tid;
    String subject;
    String name;
    long cnumber;
    String date;
    List<String>students;
    long timeMillis;
    String url;
    List<String>times;

    public ClassRoom() {
    }

    public ClassRoom(List<String> assignments, String id, String tid, String subject, String name, long cnumber, String date, List<String> students, long timeMillis) {
        this.assignments = assignments;
        this.id = id;
        this.tid = tid;
        this.subject = subject;
        this.name = name;
        this.cnumber = cnumber;
        this.date = date;
        this.students = students;
        this.timeMillis=timeMillis;
    }

    protected ClassRoom(Parcel in) {
        assignments = in.createStringArrayList();
        id = in.readString();
        tid = in.readString();
        subject = in.readString();
        name = in.readString();
        cnumber = in.readLong();
        date = in.readString();
        students = in.createStringArrayList();
        timeMillis = in.readLong();
        url=in.readString();
        times=in.createStringArrayList();
    }

    public static final Creator<ClassRoom> CREATOR = new Creator<ClassRoom>() {
        @Override
        public ClassRoom createFromParcel(Parcel in) {
            return new ClassRoom(in);
        }

        @Override
        public ClassRoom[] newArray(int size) {
            return new ClassRoom[size];
        }
    };

    public List<String> getAssignments() {
        return assignments;
    }

    public void setAssignments(List<String> assignments) {
        this.assignments = assignments;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getCnumber() {
        return cnumber;
    }

    public void setCnumber(long cnumber) {
        this.cnumber = cnumber;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<String> getStudents() {
        return students;
    }

    public void setStudents(List<String> students) {
        this.students = students;
    }

    public long getTimeMillis() {
        return timeMillis;
    }

    public void setTimeMillis(long timeMillis) {
        this.timeMillis = timeMillis;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<String> getTimes() {
        return times;
    }

    public void setTimes(List<String> times) {
        this.times = times;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeStringList(assignments);
        parcel.writeString(id);
        parcel.writeString(tid);
        parcel.writeString(subject);
        parcel.writeString(name);
        parcel.writeLong(cnumber);
        parcel.writeString(date);
        parcel.writeStringList(students);
        parcel.writeLong(timeMillis);
        parcel.writeString(url);
        parcel.writeStringList(times);
    }
}
