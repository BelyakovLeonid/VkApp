package com.belyakov.vkapp.base

import android.content.Context
import android.os.Environment
import org.koin.core.qualifier.named
import org.koin.dsl.module

const val CACHE_DIR_QUALIFIER = "cache_dir"

internal fun baseModule() = module {
    factory { get<Context>().contentResolver }
    factory(named(CACHE_DIR_QUALIFIER)) { get<Context>().getExternalFilesDir( Environment.DIRECTORY_MOVIES) }
}