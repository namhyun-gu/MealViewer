package com.earlier.yma.searchschool;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.afollestad.materialdialogs.MaterialDialog;
import com.earlier.yma.R;
import com.earlier.yma.data.SearchResult;
import com.earlier.yma.meal.MealActivity;
import com.earlier.yma.utilities.Utils;
import java.util.List;

public class SearchSchoolFragment extends Fragment
    implements SearchSchoolContract.View, SearchSchoolAdapter.OnListItemClickListener {

  private SearchSchoolContract.Presenter mPresenter;

  private SearchSchoolAdapter mAdapter;
  private View mStubNoData;
  private RecyclerView mResultView;
  private MaterialDialog mDialog;

  public SearchSchoolFragment() {

  }

  public static SearchSchoolFragment newInstance() {
    return new SearchSchoolFragment();
  }

  @Override
  public void setPresenter(SearchSchoolContract.Presenter presenter) {
    mPresenter = Utils.checkNotNull(presenter);
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mAdapter = new SearchSchoolAdapter(getContext(), this);
  }

  @Override
  public void onResume() {
    super.onResume();
    mPresenter.start();
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.fragment_search_school, container);

    mStubNoData = rootView.findViewById(R.id.stub_error);
    mResultView = rootView.findViewById(R.id.recycler_view);
    mResultView.setLayoutManager(new LinearLayoutManager(getContext()));
    mResultView.setHasFixedSize(true);
    mResultView.setAdapter(mAdapter);
    return rootView;
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    mPresenter.destroy();
  }

  @Override
  public void onListItemClicked(SearchResult.Detail detail) {
    MaterialDialog dialog = new MaterialDialog.Builder(getContext())
        .content(getString(R.string.dialog_school_result_content, detail.getSchoolName()))
        .positiveText(android.R.string.yes)
        .negativeText(android.R.string.no)
        .onPositive((dialog1, which) -> {
          mPresenter.saveSchool(detail);
          mPresenter.clearDatabase();
          Intent intent = new Intent(getContext(), MealActivity.class);
          getActivity().startActivity(intent);
          getActivity().finish();
        }).build();
    dialog.show();
  }

  @Override
  public void showEmptyError() {
    mStubNoData.setVisibility(View.VISIBLE);
    mResultView.setVisibility(View.GONE);
    mAdapter.setResults(null);
  }

  @Override
  public void showResults(List<SearchResult> searchResults) {
    mStubNoData.setVisibility(View.GONE);
    mResultView.setVisibility(View.VISIBLE);
    mAdapter.setResults(searchResults);
  }

  @Override
  public void setupProgress() {
    mDialog = new MaterialDialog.Builder(getContext())
        .content(getString(R.string.dialog_search))
        .cancelable(false)
        .canceledOnTouchOutside(false)
        .autoDismiss(false)
        .progress(true, 0)
        .show();
  }

  @Override
  public void finishProgress() {
    mDialog.dismiss();
  }

  @Override
  public void updateTitle(String query) {
    getActivity().setTitle(getString(R.string.activity_search_result_title, query));
  }
}
