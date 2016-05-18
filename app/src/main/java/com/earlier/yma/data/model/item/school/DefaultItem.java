package com.earlier.yma.data.model.item.school;

import android.support.annotation.NonNull;

import com.earlier.yma.data.model.SearchResultObject;
import com.earlier.yma.data.model.item.Item;

/**
 * Created by namhyun on 2016-02-04.
 */
public class DefaultItem implements Item {
    private String title;
    private int categoryIndex;
    private SearchResultObject.SchoolInfo info;

    public DefaultItem(@NonNull String title, int categoryIndex, @NonNull SearchResultObject.SchoolInfo info) {
        this.title = title;
        this.categoryIndex = categoryIndex;
        this.info = info;
    }

    public String getTitle() {
        return title;
    }

    public int getCategoryIndex() {
        return categoryIndex;
    }

    public SearchResultObject.SchoolInfo getInfo() {
        return info;
    }
}
