package com.belyakov.vkapp.videoredactor.presentation

import androidx.lifecycle.ViewModel
import com.belyakov.vkapp.videoredactor.presentation.model.WelcomeNavigationCommand
import kotlinx.coroutines.channels.Channel

class VideoRedactorViewModel : ViewModel() {
    val navigationCommands = Channel<WelcomeNavigationCommand>(Channel.BUFFERED)

}