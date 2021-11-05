package com.belyakov.vkapp.videoredactor.root.presentation.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import com.belyakov.vkapp.base.utils.dpToPx
import com.belyakov.vkapp.base.utils.getBitmapFrameAt
import com.belyakov.vkapp.base.utils.getDurationMs
import com.belyakov.vkapp.databinding.ViewTimelineBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class VideoProgressView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs) {

    private var frameDimension: Int = 0
    var currentProgress = 0.0
    var currentSeekPosition = 0f

    var seekListener: ((progress: Long) -> Unit)? = null
    private var videoUri: Uri? = null
    private var progressPosition: Float = 0F

    private val controlWidth = context.dpToPx(CONTROLS_WIDTH_DP)
    private val progressOverHeight = context.dpToPx(PROGRESS_OVER_HEIGHT_DP)

    private var currentVideoDuration: Long = 0
    private var currentVideoFrameHeight: Int = 0
    private var currentVideoFrameWidth: Int = 0

    private var previewAvailableWidth: Int = 0
    private var previewItemHeight: Int = 0
    private var previewItemWidth: Int = 0
    private var previewItemsCount: Int = 0
    private var videoOffset: Int = 0

    private var coroutineContext: CoroutineScope? = null
    private val gestureDetector = GestureDetector(context, ProgressGestureDetector())
    private val binding = ViewTimelineBinding.inflate(LayoutInflater.from(context), this)

    init {
        isFocusable = true
        isFocusableInTouchMode = true
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        gestureDetector.onTouchEvent(event)
        return true
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        previewAvailableWidth = w - (2 * controlWidth.toInt() + paddingEnd + paddingStart)
        previewItemHeight = h - (2 * progressOverHeight.toInt() + paddingTop + paddingBottom)
        previewItemWidth = currentVideoFrameWidth * previewItemHeight / currentVideoFrameHeight
        previewItemsCount = previewAvailableWidth / previewItemWidth
        videoOffset = (measuredWidth - previewAvailableWidth) / 2

//        startTrimPosition
//        endTrimPosition

        setProgress(0)
        loadPreviewItems()
    }

    override fun onAttachedToWindow() {
        coroutineContext = CoroutineScope(Dispatchers.Main + Job())
        super.onAttachedToWindow()
    }

    override fun onDetachedFromWindow() {
        coroutineContext?.cancel()
        coroutineContext = null
        super.onDetachedFromWindow()
    }

    fun setProgress(ms: Long?) {
        if (videoUri == null) return

        val progressMs = ms ?: 0
        val progress = progressMs.toFloat() / currentVideoDuration
        progressPosition = videoOffset + previewAvailableWidth * progress

        binding.progressIndicator.translationX = progressPosition
    }

    fun setVideoUri(uri: Uri?) {
        if (uri != null && videoUri != uri) {
            videoUri = uri
            binding.progressIndicator.isVisible = true
            recalculateSize()
        }
    }

    private val selectedPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.RED
    }


    private var startTrimPosition = 0
    private var endTrimPosition = 0

    override fun dispatchDraw(canvas: Canvas) {
        super.dispatchDraw(canvas)
    }

    private fun recalculateSize() {
        val metaDataSource = MediaMetadataRetriever()
        metaDataSource.setDataSource(context, videoUri)

        val bitmap = metaDataSource.getBitmapFrameAt(0) ?: return
        currentVideoDuration = metaDataSource.getDurationMs()
        currentVideoFrameHeight = bitmap.height
        currentVideoFrameWidth = bitmap.width

        metaDataSource.release()
    }

    private fun loadPreviewItems() {
        coroutineContext?.launch {
            val metaDataSource = MediaMetadataRetriever()
            val scaledBitmaps = ArrayList<Bitmap>(previewItemsCount)
            withContext(Dispatchers.Default) {
                metaDataSource.setDataSource(context, videoUri)
                val interval = currentVideoDuration / previewItemsCount

                for (i in 0 until previewItemsCount) {
                    val bitmap = metaDataSource.getBitmapFrameAt(i * interval) ?: return@withContext
                    val scaledBitmap = Bitmap.createScaledBitmap(bitmap, previewItemWidth, previewItemHeight, false)
                    scaledBitmaps.add(scaledBitmap)
                }
            }

            withContext(Dispatchers.Main) {
                scaledBitmaps.forEach { scaledBitmap ->
                    val previewView = VideoThumbnailView(context).apply { setImageBitmap(scaledBitmap) }
                    binding.containerThumbnails.addView(previewView)
                }
            }

            metaDataSource.release()
        }
    }

    inner class ProgressGestureDetector : GestureDetector.SimpleOnGestureListener() {
        override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
            return if (e.x >= videoOffset && e.x <= (measuredWidth - videoOffset)) {
                val videoPosition = e.x - videoOffset
                val videoPositionMs = (videoPosition * currentVideoDuration / previewAvailableWidth).toLong()
                setProgress(videoPositionMs)
                seekListener?.invoke(videoPositionMs)
                true
            } else {
                return super.onSingleTapConfirmed(e)
            }
        }
    }

    companion object {
        const val CONTROLS_WIDTH_DP = 12
        const val CONTROLS_HEIGHT_DP = 4
        const val PROGRESS_WIDTH_DP = 4
        const val PROGRESS_OVER_HEIGHT_DP = 12
    }
}