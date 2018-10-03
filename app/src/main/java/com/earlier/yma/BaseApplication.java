package com.earlier.yma;

import android.app.Application;
import io.realm.Realm;
import io.realm.RealmConfiguration;

public class BaseApplication extends Application {

  @Override
  public void onCreate() {
    super.onCreate();

    // Initialize Realm
    Realm.init(this);

    RealmConfiguration configuration = new RealmConfiguration.Builder()
        .deleteRealmIfMigrationNeeded()
        .build();

    Realm.setDefaultConfiguration(configuration);
  }
}
