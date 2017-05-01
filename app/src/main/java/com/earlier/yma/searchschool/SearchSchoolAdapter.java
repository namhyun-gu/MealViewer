package com.earlier.yma.searchschool;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.earlier.yma.R;
import com.earlier.yma.data.SearchResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SearchSchoolAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_SUB_HEADER = 1;
    private static final int TYPE_DEFAULT = 2;

    private List<SearchResult> mSearchResults;
    private List<Integer> mSubHeaderPosition;

    private OnListItemClickListener mItemClickListener;
    private Context mContext;

    public SearchSchoolAdapter(Context context, OnListItemClickListener itemClickListener) {
        mContext = context;
        mItemClickListener = itemClickListener;
    }

    public void setResults(List<SearchResult> searchResults) {
        if (searchResults != null) {
            mSubHeaderPosition = getSubHeaderPosition(searchResults);
        }
        mSearchResults = searchResults;
        notifyDataSetChanged();
    }

    private List<Integer> getSubHeaderPosition(List<SearchResult> searchResults) {
        List<Integer> positionList = new ArrayList<>();
        int subHeaderPosition = 0;
        for (SearchResult result : searchResults) {
            positionList.add(subHeaderPosition);
            subHeaderPosition += result.getResults().length + 1;
        }
        return positionList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View view;
        if (viewType == TYPE_DEFAULT) {
            view = inflater.inflate(R.layout.item_default, parent, false);
            return new DefaultHolder(view);
        } else if (viewType == TYPE_SUB_HEADER) {
            view = inflater.inflate(R.layout.item_subheader, parent, false);
            return new SubHeaderHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof DefaultHolder) {
            int approximateIndex = 0;
            for (int index = 0; index < mSubHeaderPosition.size(); index++) {
                if (position < mSubHeaderPosition.get(index)) break;
                approximateIndex = index;
            }
            String path = mSearchResults.get(approximateIndex).getPath();
            SearchResult.Result[] results = mSearchResults.get(approximateIndex).getResults();
            SearchResult.Result result = results[position - mSubHeaderPosition.get(approximateIndex) - 1];

            ((ViewBinder) holder).bind(result.getSchoolName(), path, result);
        } else if (holder instanceof SubHeaderHolder) {
            int index = mSubHeaderPosition.indexOf(position);

            int itemSize = mSearchResults.get(index).getResults().length;
            String currentPath = mSearchResults.get(index).getPath();

            String[] pathArrays = mContext.getResources().getStringArray(R.array.path_arrays);
            List<String> pathList = Arrays.asList(pathArrays);

            String[] nameArrays = mContext.getResources().getStringArray(R.array.path_name_arrays);

            ((ViewBinder) holder).bind(nameArrays[pathList.indexOf(currentPath)]
                    + " (" + itemSize + ")");
        }
    }

    @Override
    public int getItemCount() {
        if (mSearchResults == null) return 0;

        int subHeaderItemCount = mSearchResults.size();
        int itemCount = 0;
        for (SearchResult result : mSearchResults) {
            itemCount += result.getResults().length;
        }
        return subHeaderItemCount + itemCount;
    }

    @Override
    public int getItemViewType(int position) {
        return mSubHeaderPosition.contains(position) ? TYPE_SUB_HEADER : TYPE_DEFAULT;
    }

    private class DefaultHolder extends RecyclerView.ViewHolder implements ViewBinder {
        TextView titleView;

        DefaultHolder(View itemView) {
            super(itemView);
            titleView = (TextView) itemView.findViewById(R.id.tv_title);
        }

        @Override
        public void bind(String text, Object... param) {
            titleView.setText(text);

            final String path = (String) param[0];
            final SearchResult.Result result = (SearchResult.Result) param[1];
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mItemClickListener.onListItemClicked(path, result);
                }
            });
        }
    }

    private class SubHeaderHolder extends RecyclerView.ViewHolder implements ViewBinder {
        TextView titleView;

        SubHeaderHolder(View itemView) {
            super(itemView);
            titleView = (TextView) itemView.findViewById(R.id.tv_title);
        }

        @Override
        public void bind(String text, Object... param) {
            titleView.setText(text);
        }
    }

    private interface ViewBinder {

        void bind(String text, Object... param);

    }

    public interface OnListItemClickListener {

        void onListItemClicked(String path, SearchResult.Result result);

    }
}
