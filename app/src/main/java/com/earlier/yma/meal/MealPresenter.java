package com.earlier.yma.meal;

import com.earlier.yma.data.Meal;
import com.earlier.yma.utilities.Utils;

import java.util.Date;

import javax.inject.Inject;

import io.realm.Realm;

public class MealPresenter implements MealContract.Presenter {

    private MealContract.View mView;

    private MealFilterType mCurrentFiltering = MealFilterType.BREAKFAST;

    private Date mCurrentDate = new Date();

    private Realm mRealm;

    @Inject
    MealPresenter(MealContract.View view) {
        mView = view;
    }

    @Inject
    void setupListeners() {
        mView.setPresenter(this);
    }

    @Override
    public void start() {
        if (mRealm == null) {
            mRealm = Realm.getDefaultInstance();
        }
        loadData();
    }

    @Override
    public void destroy() {
        mRealm.close();
    }

    @Override
    public void loadData() {
        Date dateFrom = Utils.editDate(mCurrentDate, 0, 0);
        Date dateTo = Utils.editDate(mCurrentDate, 23, 59);

        Meal meal = mRealm.where(Meal.class)
                .equalTo("type", mCurrentFiltering.ordinal() + 1)
                .between("date", dateFrom, dateTo)
                .findFirst();

        if (meal != null && meal.isValid() && meal.getMealList().size() != 0) {
            mView.showMeal(meal);
        } else {
            mView.showNoMeal();
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
        loadData();
    }

    @Override
    public Date getDate() {
        return mCurrentDate;
    }

}
