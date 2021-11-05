package com.belyakov.vkapp.videoredactor.root.presentation

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.transition.TransitionManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.belyakov.vkapp.R
import com.belyakov.vkapp.base.utils.collectWhileStarted
import com.belyakov.vkapp.base.utils.registerSystemInsetsListener
import com.belyakov.vkapp.base.utils.updateMargins
import com.belyakov.vkapp.databinding.FragmentVideoRedactorBinding
import com.belyakov.vkapp.videoredactor.root.presentation.model.ControlDetailPanel
import com.belyakov.vkapp.videoredactor.root.presentation.model.VideoredactorNavigationCommand
import kotlinx.coroutines.flow.receiveAsFlow
import org.koin.androidx.viewmodel.ext.android.viewModel

class VideoRedactorFragment : Fragment(R.layout.fragment_video_redactor), View.OnClickListener {
    private val viewModel: VideoRedactorViewModel by viewModel()
    private val binding by viewBinding(FragmentVideoRedactorBinding::bind)

    private val videoredactorNavHost
        get() = Navigation.findNavController(requireActivity(), R.id.videoredactorContent)

    override fun onAttach(context: Context) {
        super.onAttach(context)
        collectWhileStarted(viewModel.navigationCommands.receiveAsFlow()) { command ->
            when (command) {
                is VideoredactorNavigationCommand.OpenVideo -> videoredactorNavHost.navigate(R.id.playerFragment)
                is VideoredactorNavigationCommand.CloseVideo -> videoredactorNavHost.popBackStack()
            }
        }
        collectWhileStarted(viewModel.content) { model ->
            TransitionManager.endTransitions(binding.root)
            TransitionManager.beginDelayedTransition(binding.root)
            binding.controlsContainer.isVisible = model.isControlsVisible
            binding.toolbarClose.isVisible = model.isControlsVisible
            binding.toolbarDone.isVisible = model.isControlsVisible
            renderBottomPanel(model.controlDetailPanel)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupInsets()
        binding.controlCrop.setOnClickListener(this)
        binding.controlEffects.setOnClickListener(this)
        binding.controlStickers.setOnClickListener(this)
        binding.controlMusic.setOnClickListener(this)
        binding.toolbarClose.setOnClickListener(this)
        binding.toolbarDone.setOnClickListener(this)
        binding.progressView.seekListener = viewModel::onVideoSeek
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            binding.toolbarClose.id -> viewModel.onVideoCloseClick()
            binding.toolbarDone.id -> viewModel.onDoneClick()
            binding.controlCrop.id -> viewModel.onControlCropClick()
            binding.controlEffects.id -> viewModel.onControlEffectsClick()
            binding.controlStickers.id -> viewModel.onControlStickersClick()
            binding.controlMusic.id -> viewModel.onControlMusicClick()
        }
    }

    private fun setupInsets() {
        binding.root.registerSystemInsetsListener { v, insets, margins, paddings ->
            v.updatePadding(bottom = insets.bottom + paddings.bottom)
        }
        binding.toolbarDone.registerSystemInsetsListener { v, insets, margins, _ ->
            v.updateMargins(top = insets.top + margins.top)
        }
        binding.toolbarClose.registerSystemInsetsListener { v, insets, margins, _ ->
            v.updateMargins(top = insets.top + margins.top)
        }
    }

    private fun renderBottomPanel(controlDetailPanel: ControlDetailPanel?) {
        binding.bottomPanel.isVisible = controlDetailPanel != null
        when (controlDetailPanel) {
            is ControlDetailPanel.CropPanel -> {
                binding.progressView.isVisible = true
                binding.progressView.setProgress(controlDetailPanel.progress)
                binding.progressView.setVideoUri(controlDetailPanel.uri)
            }
        }
    }
}
