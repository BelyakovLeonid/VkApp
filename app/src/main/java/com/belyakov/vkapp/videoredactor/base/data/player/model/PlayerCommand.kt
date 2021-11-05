package com.belyakov.vkapp.videoredactor.base.data.player.model

import android.net.Uri

sealed class PlayerCommand {
    class Play : PlayerCommand()
    class Pause : PlayerCommand()
    class ToStart : PlayerCommand()
    class Release : PlayerCommand()
    data class Prepare(
        val uri: Uri
    ) : PlayerCommand()
    data class Seek(
        val progress: Long
    ) : PlayerCommand()
}