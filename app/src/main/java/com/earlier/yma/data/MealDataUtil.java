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
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.earlier.yma.R;
import com.earlier.yma.data.model.MealObject;
import com.earlier.yma.data.model.item.DividerItem;
import com.earlier.yma.data.model.item.Item;
import com.earlier.yma.data.model.item.meal.DefaultItem;
import com.earlier.yma.data.model.item.meal.GraphItem;
import com.earlier.yma.data.model.item.meal.KcalItem;
import com.earlier.yma.util.RealmString;

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

    public static MealObject parseResponseLegacy(String response) {
        final Document doc = Jsoup.parse(response);

        Elements contents = doc.select(Constants.CONTENTS_SELECTOR);

        // Check meal is exists
        Element mealContent = contents.get(Constants.INDEX_MEAL);
        Elements mealElements = mealContent.select(Constants.ELEMENT_SELECTOR);

        if (mealElements.isEmpty()) {
            return new MealObject(null);
        }

        Element kcalContent = contents.get(Constants.INDEX_KCAL);
        Elements kcalElements = kcalContent.select(Constants.ELEMENT_SELECTOR);

        Element carbohydrateContent = contents.get(Constants.INDEX_CARBOHYDRATE);
        Elements carbohydrateElements = carbohydrateContent.select(Constants.ELEMENT_SELECTOR);

        Element proteinContent = contents.get(Constants.INDEX_PROTEIN);
        Elements proteinElements = proteinContent.select(Constants.ELEMENT_SELECTOR);

        Element fatContent = contents.get(Constants.INDEX_FAT);
        Elements fatElements = fatContent.select(Constants.ELEMENT_SELECTOR);

        List<MealObject.Meal> mealList = new ArrayList<>();
        for (int index = 0; index < 7; index++) {
            Element mealElement = mealElements.get(index);
            List<String> meal = ParseUtil.getStringsLegacy(mealElement);

            Element kcalElement = kcalElements.get(index);
            double kcal = ParseUtil.getDouble(kcalElement);

            Element carbohydrateElement = carbohydrateElements.get(index);
            double carbohydrate = ParseUtil.getDouble(carbohydrateElement);

            Element proteinElement = proteinElements.get(index);
            double protein = ParseUtil.getDouble(proteinElement);

            Element fatElement = fatElements.get(index);
            double fat = ParseUtil.getDouble(fatElement);

            mealList.add(new MealObject.Meal(meal, kcal, carbohydrate, protein, fat));
        }
        return new MealObject(mealList);
    }

    @Nullable
    public static List<Item> translateMealToItemList(Context context, MealObject.Meal meal) {
        if (meal.getMeal() == null || meal.getMeal().isEmpty()) {
            return null;
        }

        List<Item> items = new ArrayList<>();
        items.add(new GraphItem(meal.getCarbohydrate(), meal.getProtein(), meal.getFat()));
        items.add(new KcalItem(meal.getKcal()));
        items.add(new DividerItem());
        for (String value : meal.getMeal()) {
            items.add(createDefaultItem(context, value));
        }
        return items;
    }

    public static DefaultItem createDefaultItem(Context context, String value) {
        String[] allergy_value = context.getResources().getStringArray(R.array.allergy_info_value);
        String[] allergy_name = context.getResources().getStringArray(R.array.allergy_info_name);
        String filteredValue = value;
        StringBuilder builder = new StringBuilder();
        for (int i = allergy_value.length - 1; i >= 0; i--) {
            if (filteredValue.contains(allergy_value[i])) {
                filteredValue = filteredValue.replace(allergy_value[i], "").replace(".", "");
                builder.append(allergy_name[i]);
                builder.append(", ");
            }
        }
        if (builder.length() > 0) {
            builder.replace(builder.lastIndexOf(","), builder.length() - 1, "");
            return new DefaultItem(filteredValue, builder.toString());
        }
        return new DefaultItem(value, null);
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
