package com.belyakov.vkapp.base.utils

import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

inline fun <reified T> Fragment.collectWhileStarted(
    flow: Flow<T>,
    noinline action: suspend (T) -> Unit
) {
    object : DefaultLifecycleObserver {
        private var job: Job? = null

        init {
            lifecycle.addObserver(this)
        }

        override fun onStart(owner: LifecycleOwner) {
            job = owner.lifecycleScope.launch {
                flow.collect { action(it) }
            }
        }

        override fun onStop(owner: LifecycleOwner) {
            job?.cancel()
            job = null
        }
    }
}