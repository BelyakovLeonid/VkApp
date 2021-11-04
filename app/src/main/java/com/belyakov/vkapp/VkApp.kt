package com.belyakov.vkapp

import android.app.Application
import com.belyakov.vkapp.videoredactor.welcomeModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class VkApp : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin()
    }

    private fun initKoin() {
        startKoin {
            androidLogger()
            androidContext(this@VkApp)
            modules(
                welcomeModule()
            )
        }
    }
}