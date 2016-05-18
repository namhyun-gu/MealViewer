package com.earlier.yma.data;

import android.content.Context;
import android.support.annotation.Nullable;

import com.earlier.yma.R;
import com.earlier.yma.data.model.MealObject;
import com.earlier.yma.data.model.item.DividerItem;
import com.earlier.yma.data.model.item.Item;
import com.earlier.yma.data.model.item.meal.DefaultItem;
import com.earlier.yma.data.model.item.meal.GraphItem;
import com.earlier.yma.data.model.item.meal.KcalItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by namhyun on 2015-12-02.
 */
public class MealDataUtil {
    @Nullable
    public static List<Item> translateMealToItemList(Context context, MealObject.Meal meal) {
        if (meal.getMeal().isEmpty())
            return null;

        List<Item> items = new ArrayList<>();
        items.add(new GraphItem(meal.getCarbohydrate(), meal.getProtein(), meal.getFat()));
        items.add(new KcalItem(meal.getKcal()));
        items.add(new DividerItem());
        for (String value : meal.getMeal()) {
            items.add(createDefaultItem(context, value));
        }
        return items;
    }

    public static DefaultItem createDefaultItem(Context context, String value) {
        String[] allergy_value = context.getResources().getStringArray(R.array.allergy_info_value);
        String[] allergy_name = context.getResources().getStringArray(R.array.allergy_info_name);
        String filteredValue = value;
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < allergy_value.length; i++) {
            if (filteredValue.contains(allergy_value[i])) {
                filteredValue = filteredValue.replace(allergy_value[i], "");
                builder.append(allergy_name[i]);
                builder.append(", ");
            }
        }
        if (builder.length() > 0) {
            builder.replace(builder.lastIndexOf(","), builder.length() - 1, "");
            return new DefaultItem(filteredValue, builder.toString());
        }
        return new DefaultItem(value, null);
    }
}
