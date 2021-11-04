package com.belyakov.vkapp.videoredactor.loader.presentation

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.belyakov.vkapp.videoredactor.base.data.VideoRepository
import kotlinx.coroutines.launch

class LoaderViewModel(
    private val repository: VideoRepository
) : ViewModel() {

    fun onVideoUriReceived(uri: Uri?) {
        if (uri == null) return
        viewModelScope.launch {
            repository.saveUriAsFile(uri)
        }
    }
}