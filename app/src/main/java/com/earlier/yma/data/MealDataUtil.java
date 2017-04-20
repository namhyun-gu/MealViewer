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

package com.earlier.yma.data;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.earlier.yma.R;
import com.earlier.yma.utilities.RealmString;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmList;

public class MealDataUtil {
    @Nullable
    public static List<Meal> parseResponse(Realm realm, String response, int type) {
        final Document doc = Jsoup.parse(response);

        Elements dates = doc.select(Constants.DATE_SELECTOR);
        Elements dateElements = dates.select(Constants.DATE_ELEMENT_SELECTOR);

        Elements contents = doc.select(Constants.CONTENTS_SELECTOR);

        // Check meal is exists
        Element mealContent = contents.get(Constants.INDEX_MEAL);
        Elements mealElements = mealContent.select(Constants.ELEMENT_SELECTOR);

        if (mealElements.isEmpty()) {
            return null;
        }

        Element kcalContent = contents.get(Constants.INDEX_KCAL);
        Elements kcalElements = kcalContent.select(Constants.ELEMENT_SELECTOR);

        Element carbohydrateContent = contents.get(Constants.INDEX_CARBOHYDRATE);
        Elements carbohydrateElements = carbohydrateContent.select(Constants.ELEMENT_SELECTOR);

        Element proteinContent = contents.get(Constants.INDEX_PROTEIN);
        Elements proteinElements = proteinContent.select(Constants.ELEMENT_SELECTOR);

        Element fatContent = contents.get(Constants.INDEX_FAT);
        Elements fatElements = fatContent.select(Constants.ELEMENT_SELECTOR);

        List<Meal> meals = new ArrayList<>();
        for (int index = 0; index < 7; index++) {
            Element dateElement = dateElements.get(index);
            Date date = ParseUtil.getDate(dateElement);

            if (date == null) {
                continue;
            }

            Element mealElement = mealElements.get(index);
            RealmList<RealmString> mealStrings = ParseUtil.getStrings(realm, mealElement);

            if (mealStrings == null) {
                continue;
            }

            Element kcalElement = kcalElements.get(index);
            double kcal = ParseUtil.getDouble(kcalElement);

            Element carbohydrateElement = carbohydrateElements.get(index);
            double carbohydrate = ParseUtil.getDouble(carbohydrateElement);

            Element proteinElement = proteinElements.get(index);
            double protein = ParseUtil.getDouble(proteinElement);

            Element fatElement = fatElements.get(index);
            double fat = ParseUtil.getDouble(fatElement);

            Meal meal = realm.createObject(Meal.class);
            meal.setType(type);
            meal.setDate(date);
            meal.setMealList(mealStrings);
            meal.setKcal(kcal);
            meal.setCarbohydrate(carbohydrate);
            meal.setProtein(protein);
            meal.setFat(fat);
            meals.add(meal);
        }
        return meals;
    }

    public static String[] getFilteredMealString(Context context, String value) {
        Resources resources = context.getResources();

        String[] allergyKey = resources.getStringArray(R.array.allergy_info_key);
        String[] allergyName = resources.getStringArray(R.array.allergy_info_value);
        String titleString = value;

        StringBuilder builder = new StringBuilder();
        for (int i = allergyKey.length - 1; i >= 0; i--) {
            String key = allergyKey[i];
            String name = allergyName[i];
            if (titleString.contains(key)) {
                titleString = titleString.replace(key + ".", "");
                builder.append(name).append(", ");
            }
        }

        String secondaryString = null;
        if (builder.length() > 0) {
            builder.replace(builder.lastIndexOf(","), builder.length() - 1, "");
            secondaryString = builder.toString();
        }
        return new String[]{titleString, secondaryString};
    }

    private static class ParseUtil {
        private ParseUtil() {
            // No-op
        }

        static RealmList<RealmString> getStrings(Realm realm, Element element) {
            String elementText = element.text();
            String removeSpacesText = elementText.replace("\\s", "");
            if (!TextUtils.isEmpty(removeSpacesText)) {
                RealmList<RealmString> strings = new RealmList<>();
                for (String s : elementText.split("\\s")) {
                    RealmString realmString = realm.createObject(RealmString.class);
                    realmString.setValue(s);
                    strings.add(realmString);
                }
                return strings;
            }
            return null;
        }

        static List<String> getStringsLegacy(Element element) {
            String elementText = element.text();
            String removeSpacesText = elementText.replace("\\s", "");
            if (!TextUtils.isEmpty(removeSpacesText)) {
                return Arrays.asList(elementText.split("\\s"));
            }
            return null;
        }

        static double getDouble(Element element) {
            String elementText = element.text();
            if (!TextUtils.isEmpty(elementText)) {
                return Double.parseDouble(elementText);
            }
            return 0;
        }

        static Date getDate(Element element) {
            String dateString = element.text();
            if (dateString.length() < 3) {
                return null;
            }
            dateString = dateString.substring(0, dateString.length() - 3);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd", Locale.KOREA);
            try {
                return dateFormat.parse(dateString);
            } catch (ParseException e) {
                Log.e("ParseUtil", "Can't parse string to date", e);
                return null;
            }
        }
    }

    private class Constants {
        static final int INDEX_MEAL = 1;
        static final int INDEX_KCAL = 23;
        static final int INDEX_CARBOHYDRATE = 24;
        static final int INDEX_PROTEIN = 25;
        static final int INDEX_FAT = 26;

        static final String DATE_SELECTOR = "#contents .sub_con table thead tr";
        static final String DATE_ELEMENT_SELECTOR = "th[scope='col']";

        static final String CONTENTS_SELECTOR = "#contents .sub_con table tbody tr";
        static final String ELEMENT_SELECTOR = "td.textC";

        private Constants() {
            // No-op
        }
    }
}
