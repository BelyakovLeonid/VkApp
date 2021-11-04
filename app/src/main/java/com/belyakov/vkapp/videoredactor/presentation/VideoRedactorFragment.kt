package com.belyakov.vkapp.videoredactor.presentation

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.belyakov.vkapp.R
import com.belyakov.vkapp.base.utils.collectWhileStarted
import com.belyakov.vkapp.base.utils.registerSystemInsetsListener
import com.belyakov.vkapp.databinding.FragmentVideoRedactorBinding
import kotlinx.coroutines.flow.receiveAsFlow
import org.koin.androidx.viewmodel.ext.android.viewModel

class VideoRedactorFragment : Fragment(R.layout.fragment_video_redactor) {

    private val viewModel: VideoRedactorViewModel by viewModel()
    private val binding by viewBinding(FragmentVideoRedactorBinding::bind)

    override fun onAttach(context: Context) {
        super.onAttach(context)
        collectWhileStarted(viewModel.navigationCommands.receiveAsFlow()) { command ->
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupInsets()
    }

    private fun setupInsets() {
        binding.contentGuidelineTop.registerSystemInsetsListener { v, insets, _, _ ->
            binding.contentGuidelineTop.setGuidelineBegin(insets.top)
        }
    }
}