package com.earlier.yma.searchschool;

import android.content.Context;
import dagger.Module;
import dagger.Provides;

@Module
public class SearchSchoolPresenterModule {

  private final Context mContext;

  private final SearchSchoolContract.View mView;

  public SearchSchoolPresenterModule(Context context,
      SearchSchoolContract.View view) {
    mContext = context;
    mView = view;
  }

  @Provides
  Context provideContext() {
    return mContext;
  }

  @Provides
  SearchSchoolContract.View provideSearchSchoolContractView() {
    return mView;
  }

}
