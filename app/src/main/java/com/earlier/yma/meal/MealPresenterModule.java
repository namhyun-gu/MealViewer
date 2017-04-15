package com.earlier.yma.meal;

import android.content.Context;

import dagger.Module;
import dagger.Provides;

@Module
public class MealPresenterModule {

    private final Context mContext;

    private final MealContract.View mView;

    public MealPresenterModule(Context context, MealContract.View view) {
        mContext = context;
        mView = view;
    }

    @Provides
    Context provideContext() {
        return mContext;
    }

    @Provides
    MealContract.View provideMealContractView() {
        return mView;
    }

}
