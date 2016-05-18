package com.earlier.yma.data.model;

import java.util.List;

/**
 * Created by namhyun on 2015-11-25.
 */
public class MealObject {
    private String date;
    private int resultCode;
    private List<Meal> data;

    public String getDate() {
        return date;
    }

    public int getResultCode() {
        return resultCode;
    }

    public List<Meal> getData() {
        return data;
    }

    public static class Meal {
        private List<String> meal;
        private double kcal;
        private double carbohydrate;
        private double protein;
        private double fat;

        public List<String> getMeal() {
            return meal;
        }

        public double getKcal() {
            return kcal;
        }

        public double getCarbohydrate() {
            return carbohydrate;
        }

        public double getProtein() {
            return protein;
        }

        public double getFat() {
            return fat;
        }
    }
}
