package com.example.apnacr.Models;

import androidx.core.view.ViewCompat;

public class Solution {
    String assid;
    String uid;
    String id;
    String file;
    String thumbnail;
    int marks;
    String desc;
    String  crid;

    public Solution() {
    }

    public Solution(String assid, String uid, String id, String file, String thumbnail,String desc,String crid) {
        this.assid = assid;
        this.uid = uid;
        this.id = id;
        this.file = file;
        this.thumbnail = thumbnail;
        this.marks=-100;
        this.desc=desc;
        this.crid= crid;
    }

    public String getCrid() {
        return crid;
    }

    public void setCrid(String crid) {
        this.crid = crid;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getMarks() {
        return marks;
    }

    public void setMarks(int marks) {
        this.marks = marks;
    }

    public String getAssid() {
        return assid;
    }

    public void setAssid(String assid) {
        this.assid = assid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }
}
