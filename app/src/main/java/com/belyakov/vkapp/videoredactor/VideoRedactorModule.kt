package com.belyakov.vkapp.videoredactor

import com.belyakov.vkapp.base.CACHE_DIR_QUALIFIER
import com.belyakov.vkapp.videoredactor.base.data.VideoRepository
import com.belyakov.vkapp.videoredactor.base.data.VideoRepositoryImpl
import com.belyakov.vkapp.videoredactor.loader.presentation.LoaderViewModel
import com.belyakov.vkapp.videoredactor.player.presentation.PlayerViewModel
import com.belyakov.vkapp.videoredactor.root.presentation.VideoRedactorViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

internal fun videoRedactorModule() = module {
    single<VideoRepository> {
        VideoRepositoryImpl(
            get(named(CACHE_DIR_QUALIFIER)),
            get()
        )
    }

    viewModel { LoaderViewModel(get()) }
    viewModel { PlayerViewModel(get()) }
    viewModel { VideoRedactorViewModel(get()) }
}