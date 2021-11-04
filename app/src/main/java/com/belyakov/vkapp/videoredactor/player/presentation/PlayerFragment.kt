package com.belyakov.vkapp.videoredactor.player.presentation

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.belyakov.vkapp.R
import com.belyakov.vkapp.base.utils.collectWhileCreated
import com.belyakov.vkapp.base.utils.collectWhileStarted
import com.belyakov.vkapp.base.utils.registerSystemInsetsListener
import com.belyakov.vkapp.base.utils.updateMargins
import com.belyakov.vkapp.databinding.FragmentVideoPlayerBinding
import com.belyakov.vkapp.videoredactor.player.presentation.model.PlayerCommand
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import kotlinx.coroutines.flow.receiveAsFlow
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlayerFragment : Fragment(R.layout.fragment_video_player), View.OnClickListener {

    private val viewModel: PlayerViewModel by viewModel()
    private val binding by viewBinding(FragmentVideoPlayerBinding::bind)

    private val exoPlayer by lazy(LazyThreadSafetyMode.NONE) {
        ExoPlayer.Builder(requireContext())
            .build()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        collectWhileCreated(viewModel.videoUri) { uri ->
            uri?.let {
                exoPlayer.setMediaItem(MediaItem.fromUri(it))
                exoPlayer.prepare()
            }
        }
        collectWhileStarted(viewModel.content) { model ->
            binding.videoControl.setImageResource(model.controlResId)
        }
        collectWhileStarted(viewModel.playerCommands.receiveAsFlow()) { command ->
            when (command) {
                is PlayerCommand.Release -> {
                    exoPlayer.release()
                }
                is PlayerCommand.Play -> {
                    exoPlayer.play()
                }
                is PlayerCommand.Pause -> {
                    exoPlayer.pause()
                }
                is PlayerCommand.ToStart -> {
                    exoPlayer.pause()
                    exoPlayer.seekToDefaultPosition()
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.exoPlayerView.player = exoPlayer
        binding.toolbarClose.setOnClickListener(this)
        binding.toolbarDone.setOnClickListener(this)
        binding.videoControl.setOnClickListener(this)
        exoPlayer.addListener(viewModel)
        setupInsets()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            binding.toolbarClose.id -> findNavController().popBackStack()
            binding.toolbarDone.id -> requireActivity().finish()
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

    private fun setupInsets() {
        binding.toolbarDone.registerSystemInsetsListener { v, insets, margins, _ ->
            v.updateMargins(top = insets.top + margins.top)
        }
        binding.toolbarClose.registerSystemInsetsListener { v, insets, margins, _ ->
            v.updateMargins(top = insets.top + margins.top)
        }
    }
}