package com.belyakov.vkapp.videoredactor

import com.belyakov.vkapp.videoredactor.presentation.VideoRedactorViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

internal fun welcomeModule() = module {

    viewModel { VideoRedactorViewModel() }
}