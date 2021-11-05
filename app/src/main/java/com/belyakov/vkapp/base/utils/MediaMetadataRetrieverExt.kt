package com.belyakov.vkapp.base.utils

import android.graphics.Bitmap
import android.media.MediaMetadataRetriever

private const val MS_IN_SECOND = 1000L

fun MediaMetadataRetriever.getDurationMs(): Long {
    val durationSeconds = extractMetadata(
        MediaMetadataRetriever.METADATA_KEY_DURATION
    )?.toLong() ?: 0L
    return durationSeconds * MS_IN_SECOND
}

fun MediaMetadataRetriever.getBitmapFrameAt(time: Long): Bitmap? {
    return getFrameAtTime(time, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
}