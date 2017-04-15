package com.earlier.yma.data;

import com.earlier.yma.meal.MealFilterType;

import java.util.Date;

public interface MealDataSource {

    interface LoadDataCallback {

        void onDataLoaded(Meal meal);

        void onDataNotAvailable();

    }

    void getMeal(MealFilterType type, Date date , LoadDataCallback callback);

    void saveMeal(Meal meal);

}
