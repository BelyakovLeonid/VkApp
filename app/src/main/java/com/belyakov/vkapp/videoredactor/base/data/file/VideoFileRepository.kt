package com.belyakov.vkapp.videoredactor.base.data.file

import android.net.Uri
import kotlinx.coroutines.flow.Flow

interface VideoFileRepository {
    fun getCurrentFileUriAsFlow(): Flow<Uri?>
    suspend fun saveUriAsFile(uri: Uri)
}