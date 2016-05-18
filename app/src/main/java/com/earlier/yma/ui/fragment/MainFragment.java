package com.earlier.yma.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;

import com.earlier.yma.R;
import com.earlier.yma.data.MealDataManager;
import com.earlier.yma.data.MealDataUtil;
import com.earlier.yma.data.model.MealObject;
import com.earlier.yma.data.model.item.Item;
import com.earlier.yma.data.model.item.meal.DefaultItem;
import com.earlier.yma.data.model.item.meal.GraphItem;
import com.earlier.yma.data.model.item.meal.KcalItem;
import com.earlier.yma.ui.MainActivity;
import com.earlier.yma.ui.MealAdapter;
import com.earlier.yma.util.RxBus;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.functions.Action1;
import rx.observables.ConnectableObservable;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by namhyun on 2015-11-26.
 */
public class MainFragment extends Fragment {
    private static final String BUNDLE_TYPE = "type";
    private final String TAG = getClass().getSimpleName();

    private MealAdapter mAdapter;
    private RxBus mBus;
    private CompositeSubscription mSubscription;

    private int mTypeState;

    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @Bind(R.id.stub_no_results)
    ViewStub mViewStubNoResults;

    public static MainFragment newInstance(int type) {
        Bundle bundle = new Bundle();
        bundle.putInt(BUNDLE_TYPE, type);

        MainFragment fragment = new MainFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle.containsKey(BUNDLE_TYPE)) {
            mTypeState = bundle.getInt(BUNDLE_TYPE);
        }
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, rootView);
        // Initial recycler view
        mAdapter = new MealAdapter(getActivity());
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setVisibility(View.GONE);
        mViewStubNoResults.setVisibility(View.VISIBLE);

        // this.refresh(Util.getDayIndexFromCalendar(Util.getTodayCalender()));
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mBus = RxBus.getInstance();
    }

    @Override
    public void onStart() {
        super.onStart();
        ConnectableObservable<Object> dataUpdateEventEmitter = mBus.toObservable().publish();
        mSubscription = new CompositeSubscription();
        mSubscription.add(dataUpdateEventEmitter.subscribe(new Action1<Object>() {
            @Override
            public void call(Object o) {
                if (o instanceof MainActivity.DataUpdateEvent) {
                    Log.d(TAG, "data update event called");
                    refresh(((MainActivity.DataUpdateEvent) o).dateIndex);

                }
            }
        }));
        mSubscription.add(dataUpdateEventEmitter.connect());
    }

    @Override
    public void onStop() {
        super.onStop();
        mSubscription.clear();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
         inflater.inflate(R.menu.menu_main_fragment, menu);
         MenuItem item = menu.findItem(R.id.menu_item_share);
         item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
             @Override
             public boolean onMenuItemClick(MenuItem item) {
                 startActivity(createShareIntent());
                 return true;
             }
         });
    }

    private Intent createShareIntent() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, createSharedRaw(mAdapter.getItems()));
        return intent;
    }

    private String createSharedRaw(List<Item> items) {
        final String[] menuArray = getResources().getStringArray(R.array.default_menu_array);
        final String nutrientStringFormat = "%s : %.1f g\n";
        final String kcalStringFormat = "%s : %.1f kcal\n\n";
        CharSequence dateString = ((AppCompatActivity) getActivity()).getSupportActionBar().getTitle();

        StringBuilder builder = new StringBuilder();
        builder.append(String.format("%s, %s\n\n", dateString, menuArray[mTypeState - 1]));

        if (items != null && !items.isEmpty()) {
            for (Item item : items) {
                if (item instanceof GraphItem) {
                    final String[] nutirentArray = {
                            getString(R.string.item_content_carbohydrate),
                            getString(R.string.item_content_protein),
                            getString(R.string.item_content_fat)
                    };

                    double[] values = ((GraphItem) item).getSummarys();
                    for (int index = 0; index < values.length; index++) {
                        builder.append(String.format(nutrientStringFormat,
                                nutirentArray[index], values[index]));
                    }
                } else if (item instanceof KcalItem) {
                    final String kcalString = getString(R.string.item_content_kcal);
                    builder.append(String.format(kcalStringFormat, kcalString, ((KcalItem) item).getSummary()));
                } else if (item instanceof DefaultItem) {
                    builder.append(((DefaultItem) item).getTitle()).append("\n");
                }
            }
            builder.append(getString(R.string.shared_raw_content));
        } else {
            builder.append(getString(R.string.shared_raw_not_content));
        }
        return builder.toString();
    }

    private void refresh(int index) {
        // Refresh SharedActionProvider
//         if (mSharedActionProvider != null)
//             mSharedActionProvider.setShareIntent(createShareIntent());

        // Refresh recycler view
        MealDataManager dataManager = MealDataManager.getInstance();
        MealObject object = dataManager.read(mTypeState);
        if (object != null && object.getData() != null) {
            List<MealObject.Meal> mealList = object.getData();
            List<Item> items =
                    MealDataUtil.translateMealToItemList(getActivity(), mealList.get(index));

            if (items == null) {
                mRecyclerView.setVisibility(View.GONE);
                mViewStubNoResults.setVisibility(View.VISIBLE);
                return;
            }
            mAdapter.clearItem();
            mAdapter.addItems(items);
            mRecyclerView.setVisibility(View.VISIBLE);
            mViewStubNoResults.setVisibility(View.GONE);
        } else {
            mAdapter.clearItem();
            mRecyclerView.setVisibility(View.GONE);
            mViewStubNoResults.setVisibility(View.VISIBLE);
        }
    }
}
