package com.belyakov.vkapp.videoredactor.player.presentation

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import com.belyakov.vkapp.R
import com.belyakov.vkapp.base.utils.collectWhileCreated
import com.belyakov.vkapp.base.utils.collectWhileStarted
import com.belyakov.vkapp.databinding.FragmentVideoPlayerBinding
import com.belyakov.vkapp.videoredactor.base.data.player.model.PlayerCommand
import com.google.android.exoplayer2.MediaItem
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.receiveAsFlow
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlayerFragment : Fragment(R.layout.fragment_video_player), View.OnClickListener {
    private val viewModel: PlayerViewModel by viewModel()
    private val binding by viewBinding(FragmentVideoPlayerBinding::bind)

    override fun onAttach(context: Context) {
        super.onAttach(context)
        collectWhileStarted(viewModel.content) { model ->
            binding.videoControl.setImageResource(model.controlResId)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val exoPlayer = viewModel.getExoPLayer()
        Log.d("MyTag", "onViewCreated eo = ${exoPlayer.hashCode()}")
        binding.exoPlayerView.player = exoPlayer
        binding.videoControl.setOnClickListener(this)
        exoPlayer.addListener(viewModel)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            binding.videoControl.id -> viewModel.onVideoControlClick()
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.onScreenResumed()
    }

    override fun onPause() {
        viewModel.onScreenPaused()
        super.onPause()
    }

    override fun onDestroyView() {
        viewModel.onScreenDestroyed()
        super.onDestroyView()
    }
}