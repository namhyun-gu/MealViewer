package com.earlier.yma.meal;

import android.content.Context;

import com.earlier.yma.data.Meal;
import com.earlier.yma.util.Util;

import java.util.Date;

import javax.inject.Inject;

import io.realm.Realm;

public class MealPresenter implements MealContract.Presenter {

    private Context mContext;

    private MealContract.View mMealView;

    private MealFilterType mCurrentFiltering = MealFilterType.BREAKFAST;

    private Date mCurrentDate = new Date();

    private Realm mRealm;

    @Inject
    MealPresenter(Context context, MealContract.View mealView) {
        mContext = context;
        mMealView = mealView;
    }

    @Inject
    void setupListeners() {
        mMealView.setPresenter(this);
    }

    @Override
    public void start() {
        mRealm = Realm.getDefaultInstance();
        loadData();
    }

    @Override
    public void destroy() {
        mRealm.close();
    }

    @Override
    public void loadData() {
        Date dateFrom = Util.getEditedDateTime(mCurrentDate, 0, 0);
        Date dateTo = Util.getEditedDateTime(mCurrentDate, 23, 59);

        Meal meal = mRealm.where(Meal.class)
                .equalTo("type", mCurrentFiltering.ordinal() + 1)
                .between("date", dateFrom, dateTo)
                .findFirst();

        if (meal != null && meal.isValid()) {
            mMealView.showMeal(meal);
        } else {
            mMealView.showNoMeal();
        }
    }

    @Override
    public void setFiltering(MealFilterType filterType) {
        mCurrentFiltering = filterType;
        loadData();
    }

    @Override
    public MealFilterType getFiltering() {
        return mCurrentFiltering;
    }

    @Override
    public void setDate(Date date) {
        mCurrentDate = date;
    }

    @Override
    public Date getDate() {
        return mCurrentDate;
    }

}
