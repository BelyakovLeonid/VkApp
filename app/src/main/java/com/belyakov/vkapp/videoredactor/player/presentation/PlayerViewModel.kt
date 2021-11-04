package com.belyakov.vkapp.videoredactor.player.presentation

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.belyakov.vkapp.R
import com.belyakov.vkapp.videoredactor.base.data.VideoRepository
import com.belyakov.vkapp.videoredactor.player.presentation.model.PlayerCommand
import com.belyakov.vkapp.videoredactor.player.presentation.model.PlayerContent
import com.google.android.exoplayer2.Player
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class PlayerViewModel(
    private val repository: VideoRepository
) : ViewModel(), Player.Listener {

    val videoUri = MutableStateFlow<Uri?>(null)
    val content = MutableStateFlow(PlayerContent.DEFAULT)
    val playerCommands = Channel<PlayerCommand>(Channel.BUFFERED)

    private val isVideoPlayingNow
        get() = content.value.isPlaying

    init {
        subscribeToCurrentVideoUri()
    }

    private fun subscribeToCurrentVideoUri() {
        repository.getCurrentFileUriAsFlow()
            .filterNotNull()
            .onEach { videoUri.value = it }
            .launchIn(viewModelScope)
    }

    fun onVideoControlClick() {

        val command = if (isVideoPlayingNow) PlayerCommand.Pause() else PlayerCommand.Play()
        playerCommands.trySend(command)
        changePlayingState(isVideoPlayingNow.not())
    }

    override fun onPlaybackStateChanged(playbackState: Int) {
        when (playbackState) {
            Player.STATE_ENDED -> {
                changePlayingState(false)
                playerCommands.trySend(PlayerCommand.ToStart())
            }
        }
    }

    private fun changePlayingState(isPlaying: Boolean) {
        content.value = content.value.copy(
            isPlaying = isPlaying,
            controlResId = if (isPlaying) {
                R.drawable.video_pause_ic
            } else {
                R.drawable.video_play_ic
            }
        )
    }

    fun onScreenResumed() {
        if (isVideoPlayingNow) {
            playerCommands.trySend(PlayerCommand.Play())
        }
    }

    fun onScreenPaused() {
        if (isVideoPlayingNow) {
            playerCommands.trySend(PlayerCommand.Pause())
        }
    }

    fun onScreenDestroyed() {
        playerCommands.trySend(PlayerCommand.Release())
    }
}