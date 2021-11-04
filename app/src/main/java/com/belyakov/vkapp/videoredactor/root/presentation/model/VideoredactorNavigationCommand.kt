package com.belyakov.vkapp.videoredactor.root.presentation.model

import android.net.Uri

sealed class VideoredactorNavigationCommand {
    class OpenInitial : VideoredactorNavigationCommand()
    class OpenVideo : VideoredactorNavigationCommand()
}