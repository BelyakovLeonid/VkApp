package com.belyakov.vkapp.videoredactor.root.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.belyakov.vkapp.videoredactor.base.data.VideoRepository
import com.belyakov.vkapp.videoredactor.root.presentation.model.VideoredactorNavigationCommand
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class VideoRedactorViewModel(
    private val repository: VideoRepository
) : ViewModel() {
    val navigationCommands = Channel<VideoredactorNavigationCommand>(Channel.BUFFERED)

    init {
        subscribeToCurrentVideoUri()
    }

    private fun subscribeToCurrentVideoUri() {
        repository.getCurrentFileUriAsFlow()
            .filterNotNull()
            .onEach { navigationCommands.send(VideoredactorNavigationCommand.OpenVideo()) }
            .launchIn(viewModelScope)
    }
}