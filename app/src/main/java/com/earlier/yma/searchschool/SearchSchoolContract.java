package com.earlier.yma.searchschool;

import com.earlier.yma.BasePresenter;
import com.earlier.yma.BaseView;
import com.earlier.yma.data.SearchResult;
import java.util.List;

public interface SearchSchoolContract {

  interface View extends BaseView<SearchSchoolContract.Presenter> {

    void showEmptyError();

    void showResults(List<SearchResult> searchResults);

    void setupProgress();

    void finishProgress();

    void updateTitle(String query);
  }

  interface Presenter extends BasePresenter {

    void destroy();

    void searchSchool(String query);

    void saveSchool(SearchResult.Detail schoolDetail);

    void clearDatabase();

  }

}
