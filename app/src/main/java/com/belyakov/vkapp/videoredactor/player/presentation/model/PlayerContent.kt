package com.belyakov.vkapp.videoredactor.player.presentation.model

import androidx.annotation.DrawableRes
import com.belyakov.vkapp.R

data class PlayerContent(
    val isPlaying: Boolean,
    @DrawableRes val controlResId: Int
) {
    companion object {
        val DEFAULT = PlayerContent(
            isPlaying = false,
            controlResId = R.drawable.video_play_ic
        )
    }
}