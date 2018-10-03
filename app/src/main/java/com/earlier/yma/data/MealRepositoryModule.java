package com.earlier.yma.data;

import dagger.Module;
import dagger.Provides;

@Module
public class MealRepositoryModule {

  private final MealRepository mRepository;

  public MealRepositoryModule(MealRepository repository) {
    mRepository = repository;
  }

  @Provides
  MealRepository provideRepository() {
    return mRepository;
  }

}
