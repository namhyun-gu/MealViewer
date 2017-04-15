package com.earlier.yma.meal;

import com.earlier.yma.BasePresenter;
import com.earlier.yma.BaseView;
import com.earlier.yma.data.Meal;

import java.util.Date;

public interface MealContract {

    interface View extends BaseView<Presenter> {

        void showMeal(Meal meal);

        void showNoMeal();

    }

    interface Presenter extends BasePresenter {

        void loadData();

        void setFiltering(MealFilterType filterType);

        MealFilterType getFiltering();

        void setDate(Date date);

        Date getDate();

        void destroy();

    }

}
