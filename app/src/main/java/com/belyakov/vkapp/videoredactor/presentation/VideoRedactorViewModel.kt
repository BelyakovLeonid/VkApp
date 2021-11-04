package com.belyakov.vkapp.videoredactor.presentation

import android.content.ContentResolver
import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.belyakov.vkapp.videoredactor.presentation.model.WelcomeNavigationCommand
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import java.io.File

class VideoRedactorViewModel(
    private val cacheDir: File,
    private val contentResolver: ContentResolver
) : ViewModel() {
    val navigationCommands = Channel<WelcomeNavigationCommand>(Channel.BUFFERED)

    fun onVideoUriReceived(uri: Uri?) {
        if (uri == null) return

        viewModelScope.launch(Dispatchers.IO) {
            val tempFile = File(cacheDir, TEMP_FILE_NAME)
            val tempFileUri = tempFile.toUri()

            contentResolver.openInputStream(uri)?.use { inputStream ->
                contentResolver.openOutputStream(tempFileUri)?.use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
        }
    }

    companion object {
        private const val TEMP_FILE_NAME = "temp_file.mp4"
    }
}