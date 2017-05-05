package com.earlier.yma.searchschool;

import static com.google.common.base.Preconditions.checkNotNull;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.MaterialDialog;
import com.earlier.yma.R;
import com.earlier.yma.data.MealPreferences;
import com.earlier.yma.data.SearchResult;

import java.util.List;

public class SearchSchoolFragment extends Fragment
        implements SearchSchoolContract.View, SearchSchoolAdapter.OnListItemClickListener {

    private SearchSchoolContract.Presenter mPresenter;

    private SearchSchoolAdapter mAdapter;
    private View mStubNoData;
    private RecyclerView mResultView;
    private MaterialDialog mDialog;

    private int maxProgressValue;

    public SearchSchoolFragment() {

    }

    public static SearchSchoolFragment newInstance() {
        return new SearchSchoolFragment();
    }

    @Override
    public void setPresenter(SearchSchoolContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
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

        mStubNoData = rootView.findViewById(R.id.stub_no_results);
        mResultView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
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
    public void onListItemClicked(final String path, final SearchResult.Result result) {
        new MaterialDialog.Builder(getContext())
                .content(getString(R.string.dialog_school_result_content, result.getSchoolName()))
                .positiveText(android.R.string.yes)
                .negativeText(android.R.string.no)
                .onPositive((dialog, which) -> {
                    MealPreferences.setSchoolInfo(getContext(),
                            path,
                            result.getSchoolName(),
                            result.getSchulCode(),
                            result.getSchulCrseScCode(),
                            result.getSchulKndScCode());
                    dialog.dismiss();

                    getActivity().finish();
                })
                .show();
    }

    @Override
    public void showEmptyError() {
        mStubNoData.setVisibility(View.VISIBLE);
        mResultView.setVisibility(View.GONE);
        mAdapter.setResults(null);
    }

    @Override
    public void showNetworkError() {

    }

    @Override
    public void showResults(List<SearchResult> searchResults) {
        mStubNoData.setVisibility(View.GONE);
        mResultView.setVisibility(View.VISIBLE);
        mAdapter.setResults(searchResults);
    }

    @Override
    public void setupProgress() {
        Resources resources = getContext().getResources();
        String[] paths = resources.getStringArray(R.array.path_arrays);

        maxProgressValue = paths.length;

        mDialog = new MaterialDialog.Builder(getContext())
                .content(String.format(getString(R.string.dialog_search), 0, maxProgressValue))
                .cancelable(false)
                .canceledOnTouchOutside(false)
                .autoDismiss(false)
                .progress(true, 0)
                .show();
    }

    @Override
    public void updateProgress(Integer value) {
        mDialog.setContent(String.format(getString(R.string.dialog_search), value, maxProgressValue));
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
