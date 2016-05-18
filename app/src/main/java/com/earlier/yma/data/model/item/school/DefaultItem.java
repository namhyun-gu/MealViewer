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

package com.earlier.yma.data.model.item.school;

import android.support.annotation.NonNull;

import com.earlier.yma.data.model.SearchResultObject;
import com.earlier.yma.data.model.item.Item;

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
