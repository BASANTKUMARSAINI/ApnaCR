package com.example.apnacr.Models;

import java.util.List;

public class Teacher {

    String id;
    String name;
    List<String> classrooms;

    public Teacher() {
    }

    public Teacher(String id, String name, List<String> classrooms) {
        this.id = id;
        this.name = name;
        this.classrooms = classrooms;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getClassrooms() {
        return classrooms;
    }

    public void setClassrooms(List<String> classrooms) {
        this.classrooms = classrooms;
    }
}
