package com.earlier.yma.meal;

import com.earlier.yma.ApplicationModule;
import com.earlier.yma.data.MealRepositoryModule;
import dagger.Component;

@Component(modules = {ApplicationModule.class, MealRepositoryModule.class,
    MealPresenterModule.class})
public interface MealComponent {

  void inject(MealActivity activity);

}
