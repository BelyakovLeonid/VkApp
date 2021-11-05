package com.belyakov.vkapp.videoredactor.root.presentation.model

data class VideoredactorContent(
    val isControlsVisible: Boolean,
    val controlDetailPanel: ControlDetailPanel?
) {
    companion object {
        val DEFAULT = VideoredactorContent(
            isControlsVisible = false,
            controlDetailPanel = null,
        )
    }
}