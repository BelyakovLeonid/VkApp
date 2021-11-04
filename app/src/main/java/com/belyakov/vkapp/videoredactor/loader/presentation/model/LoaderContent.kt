package com.belyakov.vkapp.videoredactor.loader.presentation.model

data class LoaderContent(
    val isLoading: Boolean
) {

    companion object{
        val DEFAULT = LoaderContent(
            isLoading = false
        )
    }
}