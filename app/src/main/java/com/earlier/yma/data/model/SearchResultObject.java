package com.earlier.yma.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by namhyun on 2015-12-14.
 */
public class SearchResultObject {
    @Expose
    private String path;
    @SerializedName("resultSVO")
    private Result result;

    public void setPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public Result getResult() {
        return result;
    }

    public class Result {
        @SerializedName("kraOrgNm")
        private String searchName;

        @SerializedName("orgDVOList")
        private List<SchoolInfo> schoolList;

        public String getSearchName() {
            return searchName;
        }

        public List<SchoolInfo> getSchoolList() {
            return schoolList;
        }
    }

    public class SchoolInfo {
        @SerializedName("kraOrgNm")
        private String schoolName;
        @SerializedName("orgCode")
        private String schulCode;
        private String schulCrseScCode;
        private String schulKndScCode;

        public String getSchoolName() {
            return schoolName;
        }

        public String getSchulCode() {
            return schulCode;
        }

        public String getSchulCrseScCode() {
            return schulCrseScCode;
        }

        public String getSchulKndScCode() {
            return schulKndScCode;
        }
    }
}
