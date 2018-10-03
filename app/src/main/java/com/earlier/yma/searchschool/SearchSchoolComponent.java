package com.earlier.yma.searchschool;

import dagger.Component;

@Component(modules = SearchSchoolPresenterModule.class)
public interface SearchSchoolComponent {

  void inject(SearchSchoolActivity activity);

}
