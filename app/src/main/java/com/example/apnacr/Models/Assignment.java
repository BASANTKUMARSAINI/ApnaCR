package com.example.apnacr.Models;

import java.util.List;
//https://github.com/BASANTKUMARSAINI/ApnaCR.git
//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
//git remote set-url origin https://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@github.com/BASANTKUMARSAINI/ApnaCR.git
public class Assignment {
    String crid;
    String id;
    String tid;
    String sdate;
    String ldate;
    List<String>files;
    //List<String>students;
    long timeMillis;
    String thumbnail;
    String desc;
    int maxMarks;


    public Assignment() {
    }




    public String getCrid() {
        return crid;
    }

    public void setCrid(String crid) {
        this.crid = crid;
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

    public String getSdate() {
        return sdate;
    }

    public void setSdate(String sdate) {
        this.sdate = sdate;
    }

    public String getLdate() {
        return ldate;
    }

    public void setLdate(String ldate) {
        this.ldate = ldate;
    }

    public List<String> getFiles() {
        return files;
    }

    public void setFiles(List<String> files) {
        this.files = files;
    }



    public long getTimeMillis() {
        return timeMillis;
    }

    public void setTimeMillis(long timeMillis) {
        this.timeMillis = timeMillis;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getMaxMarks() {
        return maxMarks;
    }

    public void setMaxMarks(int maxMarks) {
        this.maxMarks = maxMarks;
    }



    public Assignment(String crid, String id, String tid, String sdate, String ldate, List<String> files , long timeMillis, String thumbnail, String desc,  int maxMarks) {
        this.crid = crid;
        this.id = id;
        this.tid = tid;
        this.sdate = sdate;
        this.ldate = ldate;
        this.files = files;
        //this.students = students;
        this.timeMillis=timeMillis;
        this.thumbnail=thumbnail;
        this.desc=desc;
        this.maxMarks=maxMarks;

    }

}
