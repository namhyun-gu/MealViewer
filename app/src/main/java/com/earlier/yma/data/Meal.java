package com.earlier.yma.data;

import com.earlier.yma.utilities.RealmString;

import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;

public class Meal extends RealmObject {

    private Date timestamp;
    private Date date;
    private int type;

    private RealmList<RealmString> mealList;
    private double kcal;
    private double carbohydrate;
    private double protein;
    private double fat;

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public RealmList<RealmString> getMealList() {
        return mealList;
    }

    public void setMealList(RealmList<RealmString> mealList) {
        this.mealList = mealList;
    }

    public double getKcal() {
        return kcal;
    }

    public void setKcal(double kcal) {
        this.kcal = kcal;
    }

    public double getCarbohydrate() {
        return carbohydrate;
    }

    public void setCarbohydrate(double carbohydrate) {
        this.carbohydrate = carbohydrate;
    }

    public double getProtein() {
        return protein;
    }

    public void setProtein(double protein) {
        this.protein = protein;
    }

    public double getFat() {
        return fat;
    }

    public void setFat(double fat) {
        this.fat = fat;
    }
}
