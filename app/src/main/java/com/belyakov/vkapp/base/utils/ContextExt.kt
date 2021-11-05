package com.belyakov.vkapp.base.utils

import android.content.Context
import android.util.TypedValue
import androidx.annotation.DimenRes

fun Context.dpToPx(dp: Int): Float {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dp.toFloat(),
        resources.displayMetrics
    )
}

fun Context.getDimensionKtx(@DimenRes id: Int): Float {
    return resources.getDimension(id)
}

fun Context.getDimensionPixelSizeKtx(@DimenRes id: Int): Int {
    return resources.getDimensionPixelSize(id)
}