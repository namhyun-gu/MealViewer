package com.earlier.yma.searchschool;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.earlier.yma.R;
import com.earlier.yma.data.SearchResult;
import com.earlier.yma.utilities.StringUtils;
import io.reactivex.Observable;
import java.util.ArrayList;
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
      mSearchResults = Observable.fromIterable(searchResults)
          .filter(searchResult ->
              searchResult.details() != null && searchResult.details().length != 0)
          .toList()
          .blockingGet();
      mSubHeaderPosition = getSubHeaderPosition();
    }
    notifyDataSetChanged();
  }

  private List<Integer> getSubHeaderPosition() {
    List<Integer> positionList = new ArrayList<>();
    int subHeaderPosition = 0;
    for (SearchResult result : mSearchResults) {
      positionList.add(subHeaderPosition);
      subHeaderPosition += result.details().length + 1;
    }
    return positionList;
  }

  @Override
  public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
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
  public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
    if (holder instanceof DefaultHolder) {
      int approximateIndex = 0;
      for (int index = 0; index < mSubHeaderPosition.size(); index++) {
        if (position < mSubHeaderPosition.get(index)) {
          break;
        }
        approximateIndex = index;
      }
      int itemPosition = position - mSubHeaderPosition.get(approximateIndex) - 1;
      SearchResult.Detail detail = mSearchResults.get(approximateIndex).details()[itemPosition];
      ((DefaultHolder) holder).bind(detail);
    } else if (holder instanceof SubHeaderHolder) {
      int index = mSubHeaderPosition.indexOf(position);

      int itemSize = mSearchResults.get(index).details().length;
      String pathName = mSearchResults.get(index).details()[0].getPathName();

      ((SubHeaderHolder) holder).bind(pathName + " (" + itemSize + ")");
    }
  }

  @Override
  public int getItemCount() {
    if (mSearchResults == null) {
      return 0;
    }

    int subHeaderItemCount = mSearchResults.size();
    int itemCount = 0;
    for (SearchResult result : mSearchResults) {
      itemCount += result.details().length;
    }
    return subHeaderItemCount + itemCount;
  }

  @Override
  public int getItemViewType(int position) {
    return mSubHeaderPosition.contains(position) ? TYPE_SUB_HEADER : TYPE_DEFAULT;
  }

  private interface ViewBinder<T> {

    void bind(T t);

  }

  public interface OnListItemClickListener {

    void onListItemClicked(SearchResult.Detail detail);

  }

  private class DefaultHolder extends RecyclerView.ViewHolder implements
      ViewBinder<SearchResult.Detail> {

    TextView titleView;
    TextView secondaryView;

    DefaultHolder(View itemView) {
      super(itemView);
      titleView = itemView.findViewById(R.id.tv_title);
      secondaryView = itemView.findViewById(R.id.tv_secondary);

    }

    @Override
    public void bind(SearchResult.Detail detail) {
      titleView.setText(detail.getSchoolName());
      if (!StringUtils.isNullOrEmpty(detail.getZipAddress().trim())) {
        secondaryView.setVisibility(View.VISIBLE);
        secondaryView.setText(detail.getZipAddress());
      }
      itemView.setOnClickListener(v -> mItemClickListener.onListItemClicked(detail));
    }
  }

  private class SubHeaderHolder extends RecyclerView.ViewHolder implements ViewBinder<String> {

    TextView titleView;

    SubHeaderHolder(View itemView) {
      super(itemView);
      titleView = itemView.findViewById(R.id.tv_title);
    }

    @Override
    public void bind(String s) {
      titleView.setText(s);
    }
  }
}
