package com.belyakov.vkapp.videoredactor.base.data.player.model

sealed class PlayerState {
    data class Playing(val progress: Long): PlayerState()
    data class Paused(val progress: Long): PlayerState()
    class TurnedOff: PlayerState()
}