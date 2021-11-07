package com.belyakov.vkapp.videoredactor.root.presentation.view.videocontrol.trim

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.toRect
import com.belyakov.vkapp.R
import com.belyakov.vkapp.videoredactor.root.presentation.view.videocontrol.VideoControlDelegate

class VideoTrimDelegate(
    private val context: Context,
    private val trimCornersRadius: Float,
    private val trimStrokeHorizontalWidth: Float,
    private val trimStrokeVerticalWidth: Float,
) : VideoControlDelegate {

    override var isVisible = true

    private val selectedPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = context.getColor(R.color.vk_main)
    }

    private var startTrimPosition = 0F
    private var endTrimPosition = 0F

    private val trimStartIcRect = RectF()
    private val trimEndIcRect = RectF()
    private val trimRectBig = RectF()
    private val trimRectSmall = RectF()
    private val clipOutPath = Path()

    private val trimStartIc = AppCompatResources.getDrawable(context, R.drawable.crop_back_ic)
    private val trimEndIc = AppCompatResources.getDrawable(context, R.drawable.crop_forward_ic)

    override fun draw(canvas: Canvas) {
        drawTrimStroke(canvas)
        drawTrimIcons(canvas)
    }

    private fun drawTrimStroke(canvas: Canvas) {
        canvas.save()
        clipOutPath.addRoundRect(trimRectSmall, trimCornersRadius, trimCornersRadius, Path.Direction.CW)
        canvas.clipOutPath(clipOutPath)
        canvas.drawRoundRect(trimRectBig, trimCornersRadius, trimCornersRadius, selectedPaint)
        clipOutPath.reset()
        canvas.restore()
    }

    private fun drawTrimIcons(canvas: Canvas) {
        trimStartIc?.bounds = trimStartIcRect.toRect()
        trimEndIc?.bounds = trimEndIcRect.toRect()
        trimStartIc?.draw(canvas)
        trimEndIc?.draw(canvas)
    }

    override fun setBounds(bounds: RectF) {
        startTrimPosition = bounds.left
        endTrimPosition = bounds.right
        setTrimStrokeBounds(bounds)
        setTrimIconsBounds(bounds)
    }

    private fun setTrimStrokeBounds(bounds: RectF) {
        trimRectBig.set(bounds)
        trimRectSmall.set(
            startTrimPosition + trimStrokeHorizontalWidth,
            bounds.top + trimStrokeVerticalWidth,
            endTrimPosition - trimStrokeHorizontalWidth,
            bounds.bottom - trimStrokeVerticalWidth
        )
    }

    private fun setTrimIconsBounds(bounds: RectF) {
        val iconTop = bounds.top + bounds.height() / 2F - (trimStartIc?.intrinsicHeight ?: 0) / 2F
        val iconBottom = bounds.top + bounds.height() / 2F + (trimStartIc?.intrinsicHeight ?: 0) / 2F
        val iconOffset = (trimStrokeHorizontalWidth - (trimStartIc?.intrinsicWidth ?: 0)) / 2F
        trimStartIcRect.set(
            startTrimPosition + iconOffset,
            iconTop,
            startTrimPosition + trimStrokeHorizontalWidth - iconOffset,
            iconBottom
        )
        trimEndIcRect.set(
            endTrimPosition - trimStrokeHorizontalWidth + iconOffset,
            iconTop,
            endTrimPosition - iconOffset,
            iconBottom
        )
    }
}