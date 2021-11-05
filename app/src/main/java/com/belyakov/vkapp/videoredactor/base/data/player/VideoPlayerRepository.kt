package com.belyakov.vkapp.videoredactor.base.data.player

import com.belyakov.vkapp.videoredactor.base.data.player.model.PlayerState
import kotlinx.coroutines.flow.Flow

interface VideoPlayerRepository {
    fun getPlayerStateAsFlow(): Flow<PlayerState>
    fun setPlayerEvent(newState: Int)
    fun setPlayerProgress(ms: Long)
    fun setIsPlayingState(isPlaying: Boolean)
}