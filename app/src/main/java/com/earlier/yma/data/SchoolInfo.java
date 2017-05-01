package com.earlier.yma.data;

import io.realm.RealmObject;

public class SchoolInfo extends RealmObject {

    private String path;
    private String name;
    private String schulCode;
    private String schulCrseScCode;
    private String schulKindCode;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSchulCode() {
        return schulCode;
    }

    public void setSchulCode(String schulCode) {
        this.schulCode = schulCode;
    }

    public String getSchulCrseScCode() {
        return schulCrseScCode;
    }

    public void setSchulCrseScCode(String schulCrseScCode) {
        this.schulCrseScCode = schulCrseScCode;
    }

    public String getSchulKindCode() {
        return schulKindCode;
    }

    public void setSchulKindCode(String schulKindCode) {
        this.schulKindCode = schulKindCode;
    }

}
