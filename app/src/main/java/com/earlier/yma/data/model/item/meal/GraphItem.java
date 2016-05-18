package com.earlier.yma.data.model.item.meal;

import android.support.annotation.NonNull;

import com.earlier.yma.data.model.item.Item;

/**
 * Created by namhyun on 2015-11-27.
 */
public class GraphItem implements Item {
    private double[] summarys;

    public GraphItem(@NonNull double... summarys) {
        this.summarys = summarys;
    }

    public double[] getSummarys() {
        return summarys;
    }
}
