package com.earlier.yma.meal;

import dagger.Component;

@Component(modules = MealPresenterModule.class)
public interface MealComponent {

    void inject(MealActivity activity);

}
