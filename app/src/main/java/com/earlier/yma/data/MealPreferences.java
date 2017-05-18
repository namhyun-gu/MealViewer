package com.earlier.yma.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class MealPreferences {

    public static final String PREF_PATH = "path";

    public static final String PREF_SCHOOL_NAME = "school_name";

    public static final String PREF_SCHUL_CODE = "schul_code";

    public static final String PREF_SCHUL_CRSESC = "schul_crse_sc_code";

    public static final String PREF_SCHUL_KIND = "schul_knd_sc_code";

    public static SchoolInfo getSchoolInfo(Context context) {
        SharedPreferences preferences =
                PreferenceManager.getDefaultSharedPreferences(context);

        String path = preferences.getString(PREF_PATH, null);
        String schoolName = preferences.getString(PREF_SCHOOL_NAME, null);
        String schulCode = preferences.getString(PREF_SCHUL_CODE, null);
        String schulCrseScCode = preferences.getString(PREF_SCHUL_CRSESC, null);
        String schulKndScCode = preferences.getString(PREF_SCHUL_KIND, null);

        if (path == null) return null;
        return new SchoolInfo(path, schoolName, schulCode, schulCrseScCode, schulKndScCode);
    }

    public static void setSchoolInfo(Context context,
            String path, String schoolName, String schulCode,
            String schulCrseScCode, String schulKindCode) {

        SharedPreferences preferences =
                PreferenceManager.getDefaultSharedPreferences(context);

        preferences.edit()
                .putString(PREF_PATH, path)
                .putString(PREF_SCHOOL_NAME, schoolName)
                .putString(PREF_SCHUL_CODE, schulCode)
                .putString(PREF_SCHUL_CRSESC, schulCrseScCode)
                .putString(PREF_SCHUL_KIND, schulKindCode)
                .apply();
    }

    public static class SchoolInfo {
        private String path;
        private String schoolName;
        private String schulCode;
        private String schulCrseScCode;
        private String schulKindCode;

        public SchoolInfo(String path, String schoolName, String schulCode,
                String schulCrseScCode, String schulKindCode) {
            this.path = path;
            this.schoolName = schoolName;
            this.schulCode = schulCode;
            this.schulCrseScCode = schulCrseScCode;
            this.schulKindCode = schulKindCode;
        }

        public String getPath() {
            return path;
        }

        public String getSchoolName() {
            return schoolName;
        }

        public String getSchulCode() {
            return schulCode;
        }

        public String getSchulCrseScCode() {
            return schulCrseScCode;
        }

        public String getSchulKindCode() {
            return schulKindCode;
        }
    }

}
