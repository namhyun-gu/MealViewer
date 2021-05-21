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