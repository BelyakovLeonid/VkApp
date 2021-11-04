package com.belyakov.vkapp.base.utils

import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.marginBottom
import androidx.core.view.marginEnd
import androidx.core.view.marginLeft
import androidx.core.view.marginRight
import androidx.core.view.marginTop

fun View.registerSystemInsetsListener(listener: (View, Insets, Rect, Rect) -> Unit) {
    this.registerInsetsListener(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.ime(), listener)
}

fun View.registerInsetsListener(insetsType: Int, listener: (View, Insets, Rect, Rect) -> Unit) {
    val defaultPaddings = Rect(paddingLeft, paddingTop, paddingRight, paddingEnd)
    val defaultMargins = Rect(marginLeft, marginTop, marginRight, marginEnd)
    ViewCompat.setOnApplyWindowInsetsListener(this) { v, insets ->
        val tappableInsets = insets.getInsets(insetsType)
        listener.invoke(v, tappableInsets, defaultMargins, defaultPaddings)
        insets
    }
    ViewCompat.requestApplyInsets(this)
}


fun View.updateMargins(
    left: Int? = null,
    top: Int? = null,
    right: Int? = null,
    bottom: Int? = null
) {
    (layoutParams as ViewGroup.MarginLayoutParams).setMargins(
        left ?: marginLeft,
        top ?: marginTop,
        right ?: marginRight,
        bottom ?: marginBottom
    )
}