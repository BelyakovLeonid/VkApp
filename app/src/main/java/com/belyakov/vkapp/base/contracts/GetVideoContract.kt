package com.belyakov.vkapp.base.contracts

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContract
import com.belyakov.vkapp.R

class GetVideoContract : ActivityResultContract<Unit, Uri?>() {

    override fun createIntent(context: Context, input: Unit): Intent {
        val intent = Intent().apply {
            type = "video/*"
            action = Intent.ACTION_PICK
        }
        return Intent.createChooser(intent, context.getString(R.string.video_redactor_title))
    }

    override fun getSynchronousResult(
        context: Context,
        input: Unit
    ): SynchronousResult<Uri?>? = null

    override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
        return intent.takeIf { resultCode == Activity.RESULT_OK }?.data
    }
}