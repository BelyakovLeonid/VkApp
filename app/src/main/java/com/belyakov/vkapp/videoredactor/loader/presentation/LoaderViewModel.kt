package com.belyakov.vkapp.videoredactor.loader.presentation

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.belyakov.vkapp.videoredactor.base.data.VideoRepository
import com.belyakov.vkapp.videoredactor.loader.presentation.model.LoaderContent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class LoaderViewModel(
    private val repository: VideoRepository
) : ViewModel() {

    val content = MutableStateFlow(LoaderContent.DEFAULT)

    fun onVideoUriReceived(uri: Uri?) {
        if (uri == null) return
        viewModelScope.launch {
            content.value = content.value.copy(isLoading = true)
            repository.saveUriAsFile(uri)
        }
    }
}