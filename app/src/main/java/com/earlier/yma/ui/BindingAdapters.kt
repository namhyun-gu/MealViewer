/*
 * Copyright 2021 Namhyun, Gu
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.earlier.yma.ui

import android.view.inputmethod.EditorInfo
import android.widget.EditText
import androidx.databinding.BindingAdapter
import androidx.databinding.adapters.ListenerUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.DividerItemDecoration.VERTICAL
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.earlier.yma.R

@BindingAdapter("setOnActionSearch")
fun EditText.setOnActionSearch(listener: () -> Unit) {
    setOnEditorActionListener { _, actionId, _ ->
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            listener.invoke()
            true
        } else {
            false
        }
    }
}

@BindingAdapter("showDivider")
fun RecyclerView.showDivider(visible: Boolean) {
    if (visible) {
        this.addItemDecoration(DividerItemDecoration(context, VERTICAL))
    }
}

@BindingAdapter("setOnPageChange")
fun ViewPager2.setOnPageChange(listener: () -> Unit) {
    val newListener = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            listener.invoke()
        }
    }

    val oldListener = ListenerUtil.trackListener(this, newListener, R.id.page_change_listener)
    if (oldListener != null) {
        unregisterOnPageChangeCallback(oldListener)
    }
    registerOnPageChangeCallback(newListener)
}
