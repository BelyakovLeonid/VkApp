package com.belyakov.vkapp.videoredactor.base.data

import android.net.Uri
import kotlinx.coroutines.flow.Flow

interface VideoRepository {
    fun getCurrentFileUriAsFlow(): Flow<Uri?>
    suspend fun saveUriAsFile(uri: Uri)
}