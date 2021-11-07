package com.belyakov.vkapp.videoredactor.root.presentation.view.videocontrol.progress

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import com.belyakov.vkapp.R
import com.belyakov.vkapp.videoredactor.root.presentation.view.videocontrol.VideoControlDelegate

class VideoProgressDelegate(
    private val context: Context,
    private val progressWidth: Float,
    private val corners: Float
) : VideoControlDelegate {

    override var isVisible = false

    private val progressBounds = RectF()
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = context.getColor(R.color.white)
    }

    override fun draw(canvas: Canvas) {
        if (!isVisible) return
        canvas.drawRoundRect(progressBounds, corners, corners, paint)
    }

    override fun setBounds(bounds: RectF) {
        progressBounds.set(
            bounds.left,
            bounds.top,
            bounds.left + progressWidth,
            bounds.bottom
        )
    }

    fun setProgressPosition(positionX: Float) {
        progressBounds.left = positionX
        progressBounds.right = positionX + progressWidth
    }
}