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

package com.earlier.yma.ui;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.earlier.yma.R;
import com.earlier.yma.data.model.item.DividerItem;
import com.earlier.yma.data.model.item.Item;
import com.earlier.yma.data.model.item.school.DefaultItem;
import com.earlier.yma.data.model.item.school.SubHeaderItem;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SchoolResultAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_DEFAULT = 0;
    private static final int TYPE_DIVIDER = 1;
    private static final int TYPE_SUB_HEADER = 2;

    private OnItemSelectedListener listener;
    private final LayoutInflater layoutInflater;
    private List<Item> itemList = new ArrayList<>();

    public SchoolResultAdapter(Activity hostActivity) {
        this.layoutInflater = LayoutInflater.from(hostActivity);
    }

    public void addItems(List<Item> items) {
        for (Item item : items)
            itemList.add(item);
        notifyDataSetChanged();
    }

    public void clearItem() {
        itemList.clear();
        notifyDataSetChanged();
    }

    public void setOnItemSelectedListener(OnItemSelectedListener listener) {
        this.listener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_DEFAULT:
                return createDefaultHolder(parent);
            case TYPE_DIVIDER:
                return createDividerHolder(parent);
            case TYPE_SUB_HEADER:
                return createSubHeaderHolder(parent);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final Item item = itemList.get(position);
        switch (getItemViewType(position)) {
            case TYPE_DEFAULT:
                DefaultItem defaultItem = (DefaultItem) item;
                DefaultHolder defaultHolder = (DefaultHolder) holder;
                defaultHolder.title.setText(defaultItem.getTitle());
                defaultHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (listener != null)
                            listener.onItemSelected(item);
                    }
                });
                break;
            case TYPE_SUB_HEADER:
                SubHeaderItem subHeaderItem = (SubHeaderItem) item;
                SubHeaderHolder subHeaderHolder = (SubHeaderHolder) holder;
                subHeaderHolder.title.setText(subHeaderItem.getTitle());
                break;
        }
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    @Override
    public int getItemViewType(int position) {
        Item item = itemList.get(position);
        if (item instanceof DefaultItem) {
            return TYPE_DEFAULT;
        } else if (item instanceof SubHeaderItem) {
            return TYPE_SUB_HEADER;
        } else if (item instanceof DividerItem) {
            return TYPE_DIVIDER;
        }
        return super.getItemViewType(position);
    }

    @NonNull
    private DefaultHolder createDefaultHolder(ViewGroup parent) {
        final DefaultHolder holder = new DefaultHolder(layoutInflater.inflate(
                R.layout.item_default, parent, false));
        return holder;
    }

    @NonNull
    private DividerHolder createDividerHolder(ViewGroup parent) {
        final DividerHolder holder = new DividerHolder(layoutInflater.inflate(
                R.layout.item_divider, parent, false
        ));
        return holder;
    }

    @NonNull
    private SubHeaderHolder createSubHeaderHolder(ViewGroup parent) {
        final SubHeaderHolder holder = new SubHeaderHolder(layoutInflater.inflate(
                R.layout.item_subheader, parent, false
        ));
        return holder;
    }

    public class DefaultHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_title) TextView title;
        @BindView(R.id.tv_secondary) TextView summary;

        public DefaultHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            // Remove summary text view
            summary.setVisibility(View.GONE);
        }
    }

    public class DividerHolder extends RecyclerView.ViewHolder {

        public DividerHolder(View itemView) {
            super(itemView);
        }
    }

    public class SubHeaderHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.default_title) TextView title;

        public SubHeaderHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnItemSelectedListener {
        void onItemSelected(Item item);
    }
}
