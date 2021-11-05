package com.belyakov.vkapp.videoredactor.root.presentation

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.belyakov.vkapp.videoredactor.base.data.file.VideoFileRepository
import com.belyakov.vkapp.videoredactor.base.data.player.VideoPlayerRepository
import com.belyakov.vkapp.videoredactor.base.data.player.model.PlayerCommand
import com.belyakov.vkapp.videoredactor.base.data.player.model.PlayerState
import com.belyakov.vkapp.videoredactor.root.presentation.model.ControlDetailPanel
import com.belyakov.vkapp.videoredactor.root.presentation.model.VideoredactorContent
import com.belyakov.vkapp.videoredactor.root.presentation.model.VideoredactorNavigationCommand
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class VideoRedactorViewModel(
    private val fileRepository: VideoFileRepository,
    private val playerRepository: VideoPlayerRepository
) : ViewModel() {

    val navigationCommands = Channel<VideoredactorNavigationCommand>(Channel.BUFFERED)
    val content = MutableStateFlow(VideoredactorContent.DEFAULT)
    private var currentUri: Uri? = null
    private var currentProgress: Long = 0

    init {
        subscribeToCurrentVideoUri()
        subscribeToCurrentPlayerState()
    }

    private fun subscribeToCurrentPlayerState() {
        playerRepository.getPlayerStateAsFlow()
            .onEach { state ->
                val currentDetailPanel = content.value.controlDetailPanel
                when (state) {
                    is PlayerState.Paused -> {
                        currentProgress = state.progress
                        if (currentDetailPanel is ControlDetailPanel.CropPanel) {
                            content.value = content.value.copy(
                                controlDetailPanel = currentDetailPanel.copy(
                                    progress = currentProgress
                                )
                            )
                        }
                    }
                    is PlayerState.Playing -> {
                        currentProgress = state.progress
                        if (currentDetailPanel is ControlDetailPanel.CropPanel) {
                            content.value = content.value.copy(
                                controlDetailPanel = currentDetailPanel.copy(
                                    progress = currentProgress
                                )
                            )
                        }
                    }
                }
            }.launchIn(viewModelScope)
    }

    private fun subscribeToCurrentVideoUri() {
        fileRepository.getCurrentFileUriAsFlow()
            .onEach { uri ->
                currentUri = uri
                content.value = content.value.copy(
                    isControlsVisible = uri != null,
                )
                if (uri != null) {
                    navigationCommands.send(VideoredactorNavigationCommand.OpenVideo())
                }
            }.launchIn(viewModelScope)
    }

    fun onControlCropClick() {
        val isAlreadyOpened = content.value.controlDetailPanel is ControlDetailPanel.CropPanel
        content.value = content.value.copy(
            controlDetailPanel = if (isAlreadyOpened) {
                null
            } else {
                ControlDetailPanel.CropPanel(
                    uri = currentUri,
                    progress = currentProgress
                )
            },
        )
    }

    fun onControlEffectsClick() {

    }

    fun onControlStickersClick() {

    }

    fun onControlMusicClick() {

    }

    fun onVideoCloseClick() {
        fileRepository.clearCurrentUri()
        navigationCommands.trySend(VideoredactorNavigationCommand.CloseVideo())
        content.value = content.value.copy(
            isControlsVisible = false,
            controlDetailPanel = null
        )
    }

    fun onDoneClick() {

    }

    fun onVideoSeek(progress: Long) {
        playerRepository.setPlayerCommand(PlayerCommand.Seek(progress))
    }
}