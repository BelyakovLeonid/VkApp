package com.belyakov.vkapp.videoredactor.root.presentation.view.videocontrol

import android.graphics.Canvas
import android.graphics.RectF

interface VideoControlDelegate {
    var isVisible: Boolean

    fun draw(canvas: Canvas)
    fun setBounds(bounds: RectF)
}