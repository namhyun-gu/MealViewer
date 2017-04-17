package com.earlier.yma.meal;

import dagger.Module;
import dagger.Provides;

@Module
public class MealPresenterModule {

    private final MealContract.View mView;

    public MealPresenterModule(MealContract.View view) {
        mView = view;
    }

    @Provides
    MealContract.View provideMealContractView() {
        return mView;
    }

}
