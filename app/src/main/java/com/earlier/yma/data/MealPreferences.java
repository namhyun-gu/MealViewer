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

    public static final String PREF_UPDATE_WEEK = "update_week";

    public static SchoolInfo getSchoolInfo(Context context) {
        SharedPreferences preferences =
                PreferenceManager.getDefaultSharedPreferences(context);

        String path = preferences.getString(PREF_PATH, null);
        String schulCode = preferences.getString(PREF_SCHUL_CODE, null);
        String schulCrseScCode = preferences.getString(PREF_SCHUL_CRSESC, null);
        String schulKndScCode = preferences.getString(PREF_SCHUL_KIND, null);

        return new SchoolInfo(path, schulCode, schulCrseScCode, schulKndScCode);
    }

    public static void setSchoolInfo(Context context,
            String path, String schulCode,
            String schulCrseScCode, String schulKindCode) {

        SharedPreferences preferences =
                PreferenceManager.getDefaultSharedPreferences(context);

        preferences.edit()
                .putString(PREF_PATH, path)
                .putString(PREF_SCHUL_CODE, schulCode)
                .putString(PREF_SCHUL_CRSESC, schulCrseScCode)
                .putString(PREF_SCHUL_KIND, schulKindCode)
                .apply();
    }

    public static int getUpdateWeek(Context context) {
        SharedPreferences preferences =
                PreferenceManager.getDefaultSharedPreferences(context);

        return preferences.getInt(PREF_UPDATE_WEEK, 0);
    }

    public static void setUpdateWeek(Context context, int week) {
        SharedPreferences preferences =
                PreferenceManager.getDefaultSharedPreferences(context);

        preferences.edit()
                .putInt(PREF_UPDATE_WEEK, week)
                .apply();
    }

    public static class SchoolInfo {
        private String path;
        private String schulCode;
        private String schulCrseScCode;
        private String schulKindCode;

        public SchoolInfo(String path, String schulCode, String schulCrseScCode,
                String schulKndScCode) {
            this.path = path;
            this.schulCode = schulCode;
            this.schulCrseScCode = schulCrseScCode;
            this.schulKindCode = schulKndScCode;
        }

        public String getPath() {
            return path;
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
