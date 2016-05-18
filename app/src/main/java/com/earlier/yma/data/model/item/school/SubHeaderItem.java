package com.earlier.yma.data.model.item.school;

import android.support.annotation.NonNull;

import com.earlier.yma.data.model.item.Item;

/**
 * Created by namhyun on 2016-02-04.
 */
public class SubHeaderItem implements Item {
    private String title;

    public SubHeaderItem(@NonNull String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
