package com.earlier.yma.meal;

import static com.google.common.base.Preconditions.checkNotNull;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.earlier.yma.R;
import com.earlier.yma.data.Meal;
import com.earlier.yma.searchschool.SearchSchoolActivity;
import com.earlier.yma.utilities.Utils;

import java.util.Date;

public class MealFragment extends Fragment implements MealContract.View {

    private MealContract.Presenter mPresenter;

    private MealAdapter mAdapter;
    private RecyclerView mMealView;
    private ViewStub mStubError;
    private View mStubErrorView;
    private ProgressBar mLoadingProgress;
    private TextView mErrorMessage;
    private Button mRetryButton;

    public MealFragment() {
    
    }

    public static MealFragment newInstance() {
        return new MealFragment();
    }

    @Override
    public void setPresenter(MealContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new MealAdapter(getContext());
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
        View rootView = inflater.inflate(R.layout.fragment_meal, container);

        mStubError = (ViewStub) rootView.findViewById(R.id.stub_error);
        mLoadingProgress = (ProgressBar) rootView.findViewById(R.id.pb_loading);
        mMealView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        mMealView.setLayoutManager(new LinearLayoutManager(getContext()));
        mMealView.setHasFixedSize(true);
        mMealView.setAdapter(mAdapter);
        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.destroy();
    }

    @Override
    public void showMeal(Meal meal) {
        mStubError.setVisibility(View.GONE);
        mMealView.setVisibility(View.VISIBLE);
        mLoadingProgress.setVisibility(View.GONE);
        mAdapter.setMeal(meal);
    }

    @Override
    public void showNoMeal(Date timestamp) {
        inflateViewStub();

        mErrorMessage.setText(getString(R.string.no_results_timestamp, Utils.getFormatDateString(timestamp)));
        mRetryButton.setOnClickListener(v -> mPresenter.loadData());
        mRetryButton.setVisibility(View.VISIBLE);

        mStubError.setVisibility(View.VISIBLE);
        mMealView.setVisibility(View.GONE);
        mLoadingProgress.setVisibility(View.GONE);
        mAdapter.setMeal(null);
    }

    @Override
    public void showNetworkError() {
        inflateViewStub();

        mErrorMessage.setText(R.string.network_error);
        mRetryButton.setOnClickListener(v -> mPresenter.loadData());
        mRetryButton.setVisibility(View.VISIBLE);

        mStubError.setVisibility(View.VISIBLE);
        mMealView.setVisibility(View.GONE);
        mLoadingProgress.setVisibility(View.GONE);
        mAdapter.setMeal(null);
    }

    @Override
    public void showUnknownError() {
        inflateViewStub();

        mStubError.setVisibility(View.VISIBLE);
        mMealView.setVisibility(View.GONE);
        mLoadingProgress.setVisibility(View.GONE);
        mAdapter.setMeal(null);
    }

    @Override
    public void showProgress() {
        mStubError.setVisibility(View.GONE);
        mMealView.setVisibility(View.GONE);
        mLoadingProgress.setVisibility(View.VISIBLE);
    }

    @Override
    public void showSetupDialog() {
        MaterialDialog dialog = new MaterialDialog.Builder(getContext())
                .content(getString(R.string.dialog_not_initalized_content))
                .positiveText(R.string.action_go_settings)
                .negativeText(R.string.close)
                .onPositive((dialog1, which) -> {
                    Intent intent = new Intent(getContext(), SearchSchoolActivity.class);
                    getActivity().startActivity(intent);
                    getActivity().finish();
                })
                .onNegative((dialog1, which) -> {
                    getActivity().finish();
                })
                .cancelable(false)
                .canceledOnTouchOutside(false)
                .autoDismiss(false)
                .build();
        dialog.show();
    }

    @Override
    public void updateTitle(Date date) {
        getActivity().setTitle(Utils.getDateToString(getContext(), date));
    }

    private void inflateViewStub() {
        if (mStubErrorView == null) {
            mStubErrorView = mStubError.inflate();
            mErrorMessage = (TextView) mStubErrorView.findViewById(R.id.tv_error_message);
            mRetryButton = (Button) mStubErrorView.findViewById(R.id.btn_retry);
        }
    }
}
