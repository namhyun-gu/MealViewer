package com.earlier.yma.data.model.item.meal;

import android.support.annotation.NonNull;

import com.earlier.yma.data.model.item.Item;

/**
 * Created by namhyun on 2015-11-27.
 */
public class DefaultItem implements Item {
    private String title;
    private String summary;

    public DefaultItem(@NonNull String title, String summary) {
        this.title = title;
        this.summary = summary;
    }

    public String getTitle() {
        return title;
    }

    public String getSummary() {
        return summary;
    }
}
