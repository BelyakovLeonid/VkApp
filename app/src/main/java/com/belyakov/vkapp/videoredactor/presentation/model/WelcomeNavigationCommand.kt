package com.belyakov.vkapp.videoredactor.presentation.model

sealed class WelcomeNavigationCommand {
    class OpenLogin : WelcomeNavigationCommand()
    class OpenContent : WelcomeNavigationCommand()
}