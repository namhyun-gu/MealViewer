package com.earlier.yma.meal;

import android.content.Context;
import android.util.Log;

import com.earlier.yma.data.Meal;
import com.earlier.yma.data.MealPreferences;
import com.earlier.yma.data.MealRepository;

import java.net.UnknownHostException;
import java.util.Date;

import javax.inject.Inject;

import io.reactivex.Observable;

public class MealPresenter implements MealContract.Presenter {

    private static final String TAG = MealPresenter.class.getSimpleName();

    private Context mContext;

    private MealRepository mRepository;

    private MealContract.View mView;

    private MealFilterType mCurrentFiltering = MealFilterType.LUNCH;

    private Date mCurrentDate = new Date();

    @Inject
    MealPresenter(Context context, MealRepository repository, MealContract.View view) {
        mContext = context;
        mRepository = repository;
        mView = view;
    }

    @Inject
    void setupListeners() {
        mView.setPresenter(this);
    }

    @Override
    public void start() {
        loadData();
    }

    @Override
    public void destroy() {
        mRepository.closeRealm();
    }

    @Override
    public void loadData() {
        MealPreferences.SchoolInfo info = MealPreferences.getSchoolInfo(mContext);
        int type = mCurrentFiltering.ordinal() + 1;

        if (info == null) {
            mView.showSetupDialog();
            return;
        }

        mView.updateTitle(mCurrentDate);

        Observable<Meal> local = mRepository.getLocalData(mCurrentDate, type);
        Observable<Meal> server = mRepository.getServerData(info, mCurrentDate, type);

        Observable.concat(local, server)
                .firstElement()
                .doOnSubscribe(disposable -> mView.showProgress())
                .subscribe(meal1 -> {
                            if (!meal1.getMealList().isEmpty()) {
                                mView.showMeal(meal1);
                            } else {
                                mView.showNoMeal(meal1.getTimestamp());
                            }
                        },
                        throwable -> {
                            if (throwable instanceof UnknownHostException) {
                                Log.e(TAG, "loadData: Network Error", throwable);
                                mView.showNetworkError();
                            } else {
                                Log.e(TAG, "loadData: Error occurred", throwable);
                                mView.showUnknownError();
                            }
                        });
    }

    @Override
    public MealFilterType getFiltering() {
        return mCurrentFiltering;
    }

    @Override
    public void setFiltering(MealFilterType filterType) {
        mCurrentFiltering = filterType;
        loadData();
    }

    @Override
    public Date getDate() {
        return mCurrentDate;
    }

    @Override
    public void setDate(Date date) {
        mCurrentDate = date;
        loadData();
    }

}
