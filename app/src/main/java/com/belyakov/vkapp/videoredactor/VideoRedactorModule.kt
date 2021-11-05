package com.belyakov.vkapp.videoredactor

import com.belyakov.vkapp.base.CACHE_DIR_QUALIFIER
import com.belyakov.vkapp.videoredactor.base.data.file.VideoFileRepository
import com.belyakov.vkapp.videoredactor.base.data.file.VideoFileRepositoryImpl
import com.belyakov.vkapp.videoredactor.base.data.player.VideoPlayerRepository
import com.belyakov.vkapp.videoredactor.base.data.player.VideoPlayerRepositoryImpl
import com.belyakov.vkapp.videoredactor.loader.presentation.LoaderViewModel
import com.belyakov.vkapp.videoredactor.player.presentation.PlayerViewModel
import com.belyakov.vkapp.videoredactor.root.presentation.VideoRedactorViewModel
import com.google.android.exoplayer2.ExoPlayer
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

internal fun videoRedactorModule() = module {
    single<VideoFileRepository> {
        VideoFileRepositoryImpl(
            get(named(CACHE_DIR_QUALIFIER)),
            get()
        )
    }
    single<VideoPlayerRepository> {
        VideoPlayerRepositoryImpl(get())
    }
    single {
        ExoPlayer.Builder(get()).build()
    }

    viewModel { LoaderViewModel(get()) }
    viewModel { PlayerViewModel(get(), get()) }
    viewModel { VideoRedactorViewModel(get(), get()) }
}