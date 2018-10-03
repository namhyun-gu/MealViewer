package com.earlier.yma.data;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import com.earlier.yma.data.service.NeisService;
import com.earlier.yma.utilities.MealDataUtils;
import com.earlier.yma.utilities.ToStringConverterFactory;
import com.earlier.yma.utilities.Utils;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;
import java.util.Date;
import javax.inject.Singleton;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

@Singleton
public class MealRepository {

  private Retrofit mRetrofit;

  private Realm mRealm;

  public MealRepository() {
    mRealm = Realm.getDefaultInstance();
  }

  public Observable<Meal> getLocalData(@NonNull Date date, @IntRange(from = 1, to = 3) int type) {
    Date dateFrom = Utils.editDate(date, 0, 0);
    Date dateTo = Utils.editDate(date, 23, 59);

    return Observable.create(emitter -> {
      Meal meal = mRealm.where(Meal.class)
          .equalTo("type", type)
          .between("date", dateFrom, dateTo)
          .findFirst();

      if (meal != null) {
        emitter.onNext(meal);
      }
      emitter.onComplete();
    });
  }

  public Observable<Meal> getServerData(MealPreferences.SchoolInfo info,
      @NonNull Date date, @IntRange(from = 1, to = 3) int type) {

    mRetrofit = buildRetrofit(String.format(NeisService.BASE_URL, info.getPath()));
    NeisService service = mRetrofit.create(NeisService.class);

    Observable<String> response = service.weeklyMeal(info.getSchulCode(),
        info.getSchulCrseScCode(),
        info.getSchulKindCode(),
        type,
        Utils.getFormatDateString(date));

    return response
        .subscribeOn(Schedulers.io())
        .doOnNext(s -> {
          try (Realm realm = Realm.getDefaultInstance()) {
            realm.executeTransaction(realm1 ->
                realm1.copyFromRealm(MealDataUtils.parseResponse(realm1, s, type)));
          }
        })
        .observeOn(AndroidSchedulers.mainThread())
        .flatMap(s -> getLocalData(date, type));
  }

  public void closeRealm() {
    mRealm.close();
  }

  private Retrofit buildRetrofit(String baseUrl) {
    OkHttpClient client = new OkHttpClient();

    return new Retrofit.Builder()
        .baseUrl(baseUrl)
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .addConverterFactory(new ToStringConverterFactory())
        .client(client)
        .build();
  }

}
