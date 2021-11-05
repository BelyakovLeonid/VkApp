package com.belyakov.vkapp.base.view

import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RoundRectShape
import android.util.AttributeSet
import android.widget.LinearLayout
import com.belyakov.vkapp.base.utils.dpToPx

class RoundedLinearLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs,defStyleAttr) {

    init {
        clipToOutline = true
        background = buildRoundRectDrawable()
    }

    private fun buildRoundRectDrawable(): ShapeDrawable {
        val r = context.dpToPx(10)
        val shape = RoundRectShape(
            floatArrayOf(r, r, r, r, r, r, r, r),
            null,
            null
        )
        return ShapeDrawable(shape).apply {
            colorFilter = PorterDuffColorFilter(Color.TRANSPARENT, PorterDuff.Mode.SRC_IN)
        }
    }
}