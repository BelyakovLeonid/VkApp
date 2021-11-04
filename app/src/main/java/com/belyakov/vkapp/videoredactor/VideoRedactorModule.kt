package com.belyakov.vkapp.videoredactor

import com.belyakov.vkapp.base.CACHE_DIR_QUALIFIER
import com.belyakov.vkapp.videoredactor.presentation.VideoRedactorViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

internal fun videoRedactorModule() = module {

    viewModel {
        VideoRedactorViewModel(
            get(named(CACHE_DIR_QUALIFIER)),
            get()
        )
    }
}