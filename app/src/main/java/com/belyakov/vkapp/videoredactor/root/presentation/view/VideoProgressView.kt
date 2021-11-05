package com.belyakov.vkapp.videoredactor.root.presentation.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import android.graphics.RectF
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import com.belyakov.vkapp.R
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

    private var startTrimPosition = 0
    private var endTrimPosition = 0

    private val cornerRadius = context.dpToPx(CORNERS_RADIUS_DP)
    private val trimStrokeHorizontalWidth = context.dpToPx(TRIM_WIDTH_HORIZONTAL_DP)
    private val trimStrokeVerticalWidth = context.dpToPx(TRIM_WIDTH_VERTICAL_DP)
    private val trimStartIcRect = Rect()
    private val trimEndIcRect = Rect()
    private val trimRectBig = RectF()
    private val trimRectSmall = RectF()
    private val clipOutPath = Path()

    private val trimStartIc = AppCompatResources.getDrawable(context, R.drawable.crop_back_ic)
    private val trimEndIc = AppCompatResources.getDrawable(context, R.drawable.crop_forward_ic)

    private val progressOverHeight = context.dpToPx(PROGRESS_OVER_HEIGHT_DP)
    private val trimIconOffsetOuter = context.dpToPx(TRIM_ICON_OFFSET_OUTER_DP)
    private val trimIconOffsetInner = context.dpToPx(TRIM_ICON_OFFSET_INNER_DP)

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
        previewAvailableWidth = w - (2 * trimStrokeHorizontalWidth.toInt() + paddingEnd + paddingStart)
        previewItemHeight = h - (2 * progressOverHeight.toInt() + paddingTop + paddingBottom)
        previewItemWidth = currentVideoFrameWidth * previewItemHeight / currentVideoFrameHeight
        previewItemsCount = previewAvailableWidth / previewItemWidth
        videoOffset = (measuredWidth - previewAvailableWidth) / 2

        startTrimPosition = videoOffset
        endTrimPosition = measuredWidth - videoOffset
        trimRectBig.set(
            startTrimPosition.toFloat(),
            progressOverHeight - trimStrokeVerticalWidth,
            endTrimPosition.toFloat(),
            measuredHeight.toFloat() - progressOverHeight + trimStrokeVerticalWidth
        )
        trimRectSmall.set(
            startTrimPosition.toFloat() + trimStrokeHorizontalWidth,
            progressOverHeight,
            endTrimPosition.toFloat() - trimStrokeHorizontalWidth,
            measuredHeight.toFloat() - progressOverHeight
        )
        trimStartIcRect.set(
            (startTrimPosition + trimIconOffsetOuter).toInt(),
            (measuredHeight / 2F - (trimStartIc?.intrinsicHeight ?: 0) / 2F).toInt(),
            (startTrimPosition + trimStrokeHorizontalWidth - trimIconOffsetInner).toInt(),
            (measuredHeight / 2F + (trimStartIc?.intrinsicHeight ?: 0) / 2F).toInt()
        )
        trimEndIcRect.set(
            (endTrimPosition - trimStrokeHorizontalWidth + trimIconOffsetInner).toInt(),
            (measuredHeight / 2F - (trimEndIc?.intrinsicHeight ?: 0) / 2F).toInt(),
            (endTrimPosition - trimIconOffsetOuter).toInt(),
            (measuredHeight / 2F + (trimEndIc?.intrinsicHeight ?: 0) / 2F).toInt()
        )

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
        color = context.getColor(R.color.vk_main)
    }

    override fun dispatchDraw(canvas: Canvas) {
        super.dispatchDraw(canvas)
        drawTrimmers(canvas)
    }

    private fun drawTrimmers(canvas: Canvas) {
        canvas.save()
        clipOutPath.addRoundRect(trimRectSmall, cornerRadius, cornerRadius, Path.Direction.CW)
        canvas.clipOutPath(clipOutPath)
        canvas.drawRoundRect(trimRectBig, cornerRadius, cornerRadius, selectedPaint)
        clipOutPath.reset()
        canvas.restore()

        trimStartIc?.bounds = trimStartIcRect
        trimStartIc?.draw(canvas)

        trimEndIc?.bounds = trimEndIcRect
        trimEndIc?.draw(canvas)
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
        const val CONTROLS_HEIGHT_DP = 4
        const val PROGRESS_WIDTH_DP = 4
        const val PROGRESS_OVER_HEIGHT_DP = 12
        const val CORNERS_RADIUS_DP = 10
        const val TRIM_WIDTH_VERTICAL_DP = 4
        const val TRIM_ICON_OFFSET_INNER_DP = 3
        const val TRIM_ICON_OFFSET_OUTER_DP = 2
        const val TRIM_WIDTH_HORIZONTAL_DP = 12
    }
}