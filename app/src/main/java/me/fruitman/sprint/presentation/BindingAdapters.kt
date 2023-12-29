package me.fruitman.sprint.presentation

import android.view.View
import androidx.databinding.BindingAdapter

@BindingAdapter("app:visibleIf")
fun View.visibleIf(condition: Boolean) {
    visibility = if (condition) View.VISIBLE else View.GONE
}
