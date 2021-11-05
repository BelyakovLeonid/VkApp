package com.belyakov.vkapp.videoredactor.base.data.player

import com.belyakov.vkapp.videoredactor.base.data.player.model.PlayerCommand
import com.belyakov.vkapp.videoredactor.base.data.player.model.PlayerState
import com.google.android.exoplayer2.ExoPlayer
import kotlinx.coroutines.flow.Flow

interface VideoPlayerRepository {
    fun getPlayerStateAsFlow(): Flow<PlayerState>
    fun setPlayerCommand(command: PlayerCommand)
    fun getExoPLayer(): ExoPlayer
}