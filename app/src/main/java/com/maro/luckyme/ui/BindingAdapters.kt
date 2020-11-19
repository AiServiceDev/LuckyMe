package com.maro.luckyme.ui

import android.view.View
import androidx.databinding.BindingAdapter

/**
 * 뷰 공통 visible <-> gone 처리
 */
@BindingAdapter("goneUnless")
fun goneUnless(view: View, visible: Boolean?) {
    view.visibility = if (visible == true) View.VISIBLE else View.GONE
}

/**
 * 제비뽑기 visible <-> gone <-> invisible 처리
 */
@BindingAdapter(value = ["jebiGoneUnless", "jebiInvisibleUnless"], requireAll = false)
fun jebiGoneInvisibleUnless(view: View, goneUnless: Boolean?, invisibleUnless: Boolean?) {
    view.visibility = if (goneUnless == false) {
        View.GONE
    } else if (invisibleUnless == false) {
        View.INVISIBLE
    } else {
        View.VISIBLE
    }
}