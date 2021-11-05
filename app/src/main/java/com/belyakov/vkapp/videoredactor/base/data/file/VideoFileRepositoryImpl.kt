package com.belyakov.vkapp.videoredactor.base.data.file

import android.content.ContentResolver
import android.net.Uri
import androidx.core.net.toUri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import java.io.File

class VideoFileRepositoryImpl(
    private val cacheDir: File,
    private val contentResolver: ContentResolver
) : VideoFileRepository {

    private val currentUriStateFlow = MutableStateFlow<Uri?>(null)

    override suspend fun saveUriAsFile(uri: Uri): Unit = withContext(Dispatchers.IO) {
        val tempFile = File(cacheDir, TEMP_FILE_NAME)
        val tempFileUri = tempFile.toUri()

        contentResolver.openInputStream(uri)?.use { inputStream ->
            contentResolver.openOutputStream(tempFileUri)?.use { outputStream ->
                inputStream.copyTo(outputStream)
                currentUriStateFlow.value = uri
            }
        }
    }

    override fun getCurrentFileUriAsFlow(): Flow<Uri?> {
        return currentUriStateFlow.asStateFlow()
    }

    companion object {
        private const val TEMP_FILE_NAME = "temp_file.mp4"
    }
}