package com.earlier.yma.data.model.item.meal;

import com.earlier.yma.data.model.item.Item;

/**
 * Created by namhyun on 2015-11-27.
 */
public class KcalItem implements Item {
    private double summary;

    public KcalItem(double summary) {
        this.summary = summary;
    }

    public double getSummary() {
        return summary;
    }
}
