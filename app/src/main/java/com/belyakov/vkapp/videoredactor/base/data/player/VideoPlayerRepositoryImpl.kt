package com.belyakov.vkapp.videoredactor.base.data.player

import com.belyakov.vkapp.videoredactor.base.data.player.model.PlayerState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class VideoPlayerRepositoryImpl : VideoPlayerRepository {
    private val state = MutableStateFlow<PlayerState>(PlayerState.TurnedOff())

    override fun getPlayerStateAsFlow(): Flow<PlayerState> {
        return state.asStateFlow()
    }

    override fun setPlayerEvent(newState: Int) {
    }

    override fun setIsPlayingState(isPlaying: Boolean) {
        val currentProgress = when (val currentState = state.value) {
            is PlayerState.Playing -> currentState.progress
            is PlayerState.Paused -> currentState.progress
            else -> 0

        }

        if (isPlaying) {
            state.value = PlayerState.Playing(currentProgress)
        } else {
            state.value = PlayerState.Paused(currentProgress)
        }
    }

    override fun setPlayerProgress(ms: Long) {
        when (val currentState = state.value) {
            is PlayerState.Playing -> {
                state.value = currentState.copy(progress = ms)
            }
            is PlayerState.Paused -> {
                state.value = currentState.copy(progress = ms)
            }
        }
    }
}