package com.earlier.yma.utilities;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.earlier.yma.R;
import com.earlier.yma.data.Meal;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmList;

public class MealDataUtils {
    private static final String TABLE_SELECTOR = "#contents .sub_con table";
    private static final String TABLE_HEAD_SELECTOR = " thead";
    private static final String TABLE_BODY_SELECTOR = " tbody";

    private static final String DATE_SELECTOR =
            TABLE_SELECTOR + TABLE_HEAD_SELECTOR + " tr th[scope='col']";

    private static final String BODY_CONTENT_SELECTOR =
            TABLE_SELECTOR + TABLE_BODY_SELECTOR + " tr";

    private static final String ROW_NAME_SELECTOR = "th[scope='row']";
    private static final String DATA_SELECTOR = "td.textC";

    private static final String[] ROW_NAME_MEAL = {"조식", "중식", "석식"};
    private static final String ROW_NAME_KCAL = "에너지(kcal)";
    private static final String ROW_NAME_CARBOHYDRATE = "탄수화물(g)";
    private static final String ROW_NAME_PROTEIN = "단백질(g)";
    private static final String ROW_NAME_FAT = "지방(g)";

    @Nullable
    public static List<Meal> parseResponse(Realm realm, String response, int type) {
        final Document doc = Jsoup.parse(response);

        Elements dateElements = doc.select(DATE_SELECTOR);
        Elements mealElements = null;
        Elements kcalElements = null;
        Elements carbohydrateElements = null;
        Elements proteinElements = null;
        Elements fatElements = null;

        Elements bodyContentElements = doc.select(BODY_CONTENT_SELECTOR);

        for (Element element : bodyContentElements) {
            Element rowNameElement = element.select(ROW_NAME_SELECTOR).first();
            String rowName = rowNameElement.text();

            if (rowName.equals(ROW_NAME_MEAL[type - 1])) {
                mealElements = element.select(DATA_SELECTOR);
            } else if (rowName.equals(ROW_NAME_KCAL)) {
                kcalElements = element.select(DATA_SELECTOR);
            } else if (rowName.equals(ROW_NAME_CARBOHYDRATE)) {
                carbohydrateElements = element.select(DATA_SELECTOR);
            } else if (rowName.equals(ROW_NAME_PROTEIN)) {
                proteinElements = element.select(DATA_SELECTOR);
            } else if (rowName.equals(ROW_NAME_FAT)) {
                fatElements = element.select(DATA_SELECTOR);
            }
        }

        List<Meal> meals = new ArrayList<>();
        for (int index = 0; index < 7; index++) {
            Element dateElement = dateElements.get(index);
            Date date = ParseUtil.getDate(dateElement);

            if (date == null) continue;

            Element mealElement = mealElements.get(index);
            RealmList<RealmString> mealStrings = ParseUtil.getStrings(realm, mealElement);

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
                titleString = titleString.replace(key, "");
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

        static double getDouble(Element element) {
            String elementText = element.text();
            if (!TextUtils.isEmpty(elementText)) {
                try {
                    return Double.parseDouble(elementText);
                } catch (NumberFormatException e) {
                    Log.e("MealDataUtils", "Is not number (text: " + elementText + ")", e);
                    return 0;
                }
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
}
