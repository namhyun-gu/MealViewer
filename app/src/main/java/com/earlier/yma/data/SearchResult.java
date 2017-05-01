package com.earlier.yma.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SearchResult {

    private Result[] results;

    @Expose private String path;

    public SearchResult(Result[] results) {
        this.results = results;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public Result[] getResults() {
        return results;
    }

    public class Result {
        @SerializedName("kraOrgNm") private String schoolName;
        @SerializedName("orgCode") private String schulCode;
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
