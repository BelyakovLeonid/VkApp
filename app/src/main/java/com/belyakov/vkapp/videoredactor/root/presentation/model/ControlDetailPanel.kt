package com.belyakov.vkapp.videoredactor.root.presentation.model

import android.net.Uri

sealed class ControlDetailPanel {
    data class CropPanel(
        val uri: Uri?,
        val progress: Long
    ): ControlDetailPanel()
}