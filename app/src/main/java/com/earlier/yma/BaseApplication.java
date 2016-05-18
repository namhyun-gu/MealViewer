package com.earlier.yma;

import android.app.Application;

import com.earlier.yma.data.MealDataManager;

/**
 * Created by namhyun on 2015-04-28.
 */
public class BaseApplication extends Application {
    public String base64publicKey =
            "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAuFURC7" +
            "fATWaL3Jr9TCMxB0jr51NIhhUCOaVRPyVpmiRYgXNWeYZvnyg3VZqPxh10JiYc4OlFj7lFAkBI9ndfk58" +
            "4V5zDcPDtzCETTsQYXmLb8acjTi5VZcyWTM6B9yHOh6lTE5qyaAsXCB5caAJoY5c0cp5jekANH3nFFdWXM" +
            "iAnVf214VzTeeQ55hJVxcOdmLOMYrjBXT3WfzE+Z7c5ZqhH0pKlwl7Rs5zpDd35x0plPnMysNI+N0vIZF1" +
            "dj206KunPd/8t+DSSZY8jnhv9dWv4D5W9k12DT8gl6kjmlnt44amxiIRa0BCx94DaQcPSJ9H1Hz6nXtokz" +
            "elUaDktSQIDAQAB";

    @Override
    public void onCreate() {
        super.onCreate();
        MealDataManager.initialize(getApplicationContext());
    }
}
