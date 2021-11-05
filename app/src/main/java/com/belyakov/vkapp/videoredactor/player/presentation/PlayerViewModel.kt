package com.belyakov.vkapp.videoredactor.player.presentation

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.belyakov.vkapp.R
import com.belyakov.vkapp.videoredactor.base.data.file.VideoFileRepository
import com.belyakov.vkapp.videoredactor.base.data.player.VideoPlayerRepository
import com.belyakov.vkapp.videoredactor.base.data.player.model.PlayerCommand
import com.belyakov.vkapp.videoredactor.player.presentation.model.PlayerContent
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Player
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@SuppressLint("StaticFieldLeak")
class PlayerViewModel(
    private val fileRepository: VideoFileRepository,
    private val playerRepository: VideoPlayerRepository
) : ViewModel(), Player.Listener {
    val content = MutableStateFlow(PlayerContent.DEFAULT)

    private val isVideoPlayingNow
        get() = content.value.isPlaying

    init {
        subscribeToCurrentVideoUri()
        playerRepository.getExoPLayer().addListener(this)
    }

    fun onVideoControlClick() {
        val command = if (isVideoPlayingNow) PlayerCommand.Pause() else PlayerCommand.Play()
        playerRepository.setPlayerCommand(command)
        changePlayingState(isVideoPlayingNow.not())
    }

    override fun onPlaybackStateChanged(playbackState: Int) {
        when (playbackState) {
            Player.STATE_ENDED -> {
                changePlayingState(false)
                playerRepository.setPlayerCommand(PlayerCommand.ToStart())
            }
        }
    }

    fun onScreenResumed() {
        if (isVideoPlayingNow) {
            playerRepository.setPlayerCommand(PlayerCommand.Play())
        }
    }

    fun onScreenPaused() {
        if (isVideoPlayingNow) {
            playerRepository.setPlayerCommand(PlayerCommand.Pause())
        }
    }

    private fun subscribeToCurrentVideoUri() {
        fileRepository.getCurrentFileUriAsFlow()
            .filterNotNull()
            .onEach { uri ->
                playerRepository.setPlayerCommand(PlayerCommand.Prepare(uri))
            }.launchIn(viewModelScope)
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

    fun getExoPLayer(): ExoPlayer {
        return playerRepository.getExoPLayer()
    }

    override fun onCleared() {
        playerRepository.getExoPLayer().removeListener(this)
        super.onCleared()
    }
}
