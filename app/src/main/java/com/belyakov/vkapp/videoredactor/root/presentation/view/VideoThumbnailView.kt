package com.belyakov.vkapp.videoredactor.root.presentation.view

import android.content.Context
import android.graphics.Bitmap
import android.util.AttributeSet
import android.widget.LinearLayout

class VideoThumbnailView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : androidx.appcompat.widget.AppCompatImageView(context, attrs) {

    init {
        scaleType = ScaleType.CENTER_CROP
        layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply { weight = 1f }
    }
}