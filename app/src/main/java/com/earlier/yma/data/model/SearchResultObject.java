/*
 * Copyright 2016 Namhyun, Gu
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.earlier.yma.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

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
