package com.belyakov.vkapp.videoredactor.root.presentation.model

sealed class VideoredactorNavigationCommand {
    class OpenInitial : VideoredactorNavigationCommand()
    class OpenVideo : VideoredactorNavigationCommand()
    class CloseVideo : VideoredactorNavigationCommand()
}