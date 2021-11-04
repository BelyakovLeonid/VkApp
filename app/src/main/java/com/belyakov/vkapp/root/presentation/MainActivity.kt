package com.belyakov.vkapp.root.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.belyakov.vkapp.R
import com.vk.api.sdk.VK
import com.vk.api.sdk.auth.VKScope

class MainActivity : AppCompatActivity(R.layout.activity_main) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        VK.login(this, arrayListOf(VKScope.WALL, VKScope.PHOTOS))
    }
}