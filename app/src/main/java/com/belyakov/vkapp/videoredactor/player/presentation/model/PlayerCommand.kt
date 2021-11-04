package com.belyakov.vkapp.videoredactor.player.presentation.model

sealed class PlayerCommand {
    class Play : PlayerCommand()
    class Pause : PlayerCommand()
    class ToStart : PlayerCommand()
    class Release : PlayerCommand()
}