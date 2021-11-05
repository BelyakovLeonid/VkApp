package com.belyakov.vkapp.videoredactor.base.data.player

import android.util.Log
import com.belyakov.vkapp.videoredactor.base.data.player.model.PlayerCommand
import com.belyakov.vkapp.videoredactor.base.data.player.model.PlayerState
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class VideoPlayerRepositoryImpl(
    private val player: ExoPlayer
) : VideoPlayerRepository {
    private val state = MutableStateFlow<PlayerState>(PlayerState.TurnedOff())
    private var currentProgress: Long = 0

    init {
        startPostingProgress()
    }

    private fun startPostingProgress() {
        GlobalScope.launch {
            while (true) {
                currentProgress = withContext(Dispatchers.Main) {
                    player.currentPosition * 1000L
                }

                delay(10L)
                when (val currentState = state.value) {
                    is PlayerState.Playing -> {
                        state.value = currentState.copy(progress = currentProgress)
                    }
                    is PlayerState.Paused -> {
                        state.value = currentState.copy(progress = currentProgress)
                    }
                }
            }
        }
    }

    override fun getExoPLayer(): ExoPlayer {
        return player
    }

    override fun getPlayerStateAsFlow(): Flow<PlayerState> {
        return state.asStateFlow()
    }

    override fun setPlayerCommand(command: PlayerCommand) {
        Log.d("MyTag", "command = $command")
        when (command) {
            is PlayerCommand.Seek -> player.seekTo(command.progress / 1000)
            is PlayerCommand.Release -> {
                player.release()
                state.value = PlayerState.TurnedOff()
            }
            is PlayerCommand.Play -> {
                player.play()
                state.value = PlayerState.Playing(currentProgress)
            }
            is PlayerCommand.Pause -> {
                player.pause()
                state.value = PlayerState.Paused(currentProgress)
            }
            is PlayerCommand.ToStart -> {
                player.pause()
                player.seekToDefaultPosition()
                state.value = PlayerState.Paused(currentProgress)
            }
            is PlayerCommand.Prepare -> {
                player.setMediaItem(MediaItem.fromUri(command.uri))
                player.prepare()
            }
        }
    }
}