package com.earlier.yma.meal;

import static com.google.common.base.Preconditions.checkNotNull;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.earlier.yma.R;
import com.earlier.yma.data.Meal;
import com.earlier.yma.data.MealDataUtil;
import com.earlier.yma.data.model.item.DividerItem;
import com.earlier.yma.data.model.item.Item;
import com.earlier.yma.data.model.item.meal.DefaultItem;
import com.earlier.yma.data.model.item.meal.GraphItem;
import com.earlier.yma.data.model.item.meal.KcalItem;
import com.earlier.yma.ui.MealAdapter;
import com.earlier.yma.utilities.RealmString;

import java.util.ArrayList;
import java.util.List;

public class MealFragment extends Fragment implements MealContract.View {

    private MealContract.Presenter mPresenter;

    private MealAdapter mAdapter;
    private RecyclerView mMealView;
    private View mStubNoMeal;

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
        mAdapter = new MealAdapter(getActivity());
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
        View rootView = inflater.inflate(R.layout.fragment_main, container);

        mStubNoMeal = rootView.findViewById(R.id.stub_no_results);
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
        mStubNoMeal.setVisibility(View.GONE);
        mMealView.setVisibility(View.VISIBLE);

        List<Item> items = new ArrayList<>();
        items.add(new GraphItem(meal.getCarbohydrate(), meal.getProtein(), meal.getFat()));
        items.add(new KcalItem(meal.getKcal()));
        items.add(new DividerItem());
        for (RealmString s : meal.getMealList()) {
            DefaultItem mealItem = MealDataUtil.createDefaultItem(getContext(), s.getValue());
            items.add(mealItem);
        }
        mAdapter.setItems(items);
    }

    @Override
    public void showNoMeal() {
        mStubNoMeal.setVisibility(View.VISIBLE);
        mMealView.setVisibility(View.GONE);
        mAdapter.setItems(null);
    }
}
