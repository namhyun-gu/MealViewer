/*
 * Copyright 2016 Namhyun, Gu
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.earlier.yma.meal;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.earlier.yma.R;
import com.earlier.yma.data.Meal;
import com.earlier.yma.utilities.MealDataUtils;
import com.google.common.base.Strings;

public class MealAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;

    private static final int TYPE_GRAPH = 0;
    private static final int TYPE_KCAL = 1;
    private static final int TYPE_DIVIDER = 2;
    private static final int TYPE_DEFAULT = 3;

    private static final int DETAIL_ITEM_SIZE = 3;
    private static final int DEFAULT_ITEM_POSITION = DETAIL_ITEM_SIZE;

    private Meal mMeal;

    public MealAdapter(Context context) {
        mContext = context;
    }

    public void setMeal(Meal meal) {
        mMeal = meal;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        int layoutId = getLayoutId(viewType);
        View view = inflater.inflate(layoutId, parent, false);
        return getViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((ViewBinder) holder).bind(mMeal);
    }

    @Override
    public int getItemCount() {
        if (mMeal == null) return 0;
        return DETAIL_ITEM_SIZE + mMeal.getMealList().size();
    }

    @Override
    public int getItemViewType(int position) {
        return position >= DEFAULT_ITEM_POSITION ? TYPE_DEFAULT : position;
    }

    private int getLayoutId(int viewType) {
        if (viewType == TYPE_GRAPH) {
            return R.layout.item_graph;
        } else if (viewType == TYPE_KCAL) {
            return R.layout.item_kcal;
        } else if (viewType == TYPE_DIVIDER) {
            return R.layout.item_divider;
        } else {
            return R.layout.item_default;
        }
    }

    private RecyclerView.ViewHolder getViewHolder(View view, int viewType) {
        if (viewType == TYPE_GRAPH) {
            return new GraphHolder(view);
        } else if (viewType == TYPE_KCAL) {
            return new KcalHolder(view);
        } else if (viewType == TYPE_DIVIDER) {
            return new DividerHolder(view);
        } else {
            return new DefaultHolder(view);
        }
    }

    private class GraphHolder extends RecyclerView.ViewHolder implements ViewBinder {
        TextView carbohydrateView;
        TextView proteinView;
        TextView fatView;

        public GraphHolder(View itemView) {
            super(itemView);
            carbohydrateView = (TextView) itemView.findViewById(R.id.tv_carbohydrate);
            proteinView = (TextView) itemView.findViewById(R.id.tv_protein);
            fatView = (TextView) itemView.findViewById(R.id.tv_fat);
        }

        @Override
        public void bind(Meal meal) {
            carbohydrateView.setText(String.format("%s g", meal.getCarbohydrate()));
            proteinView.setText(String.format("%s g", meal.getProtein()));
            fatView.setText(String.format("%s g", meal.getFat()));
        }
    }

    private class KcalHolder extends RecyclerView.ViewHolder implements ViewBinder {
        TextView kcalView;

        public KcalHolder(View itemView) {
            super(itemView);
            kcalView = (TextView) itemView.findViewById(R.id.tv_kcal);
        }

        @Override
        public void bind(Meal meal) {
            kcalView.setText(String.format("%s kcal", meal.getKcal()));
        }
    }

    private class DividerHolder extends RecyclerView.ViewHolder implements ViewBinder {

        public DividerHolder(View itemView) {
            super(itemView);
        }

        @Override
        public void bind(Meal meal) {
        }
    }

    private class DefaultHolder extends RecyclerView.ViewHolder implements ViewBinder {
        TextView titleView;
        TextView secondaryView;

        public DefaultHolder(View itemView) {
            super(itemView);
            titleView = (TextView) itemView.findViewById(R.id.tv_title);
            secondaryView = (TextView) itemView.findViewById(R.id.tv_secondary);
        }

        @Override
        public void bind(Meal meal) {
            int position = getAdapterPosition() - DEFAULT_ITEM_POSITION;
            String mealString = meal.getMealList().get(position).getValue();

            String filteredString[] = MealDataUtils.getFilteredMealString(mContext, mealString);
            titleView.setText(filteredString[0]);
            if (!Strings.isNullOrEmpty(filteredString[1])) {
                secondaryView.setText(filteredString[1]);
                secondaryView.setVisibility(View.VISIBLE);
            }
        }
    }

    private interface ViewBinder {

        void bind(Meal meal);

    }
}
