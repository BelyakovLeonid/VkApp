package com.belyakov.vkapp.videoredactor.presentation

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.launch
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.belyakov.vkapp.R
import com.belyakov.vkapp.base.contracts.GetVideoContract
import com.belyakov.vkapp.base.utils.collectWhileStarted
import com.belyakov.vkapp.base.utils.registerSystemInsetsListener
import com.belyakov.vkapp.databinding.FragmentVideoRedactorBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.withContext
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File

class VideoRedactorFragment : Fragment(R.layout.fragment_video_redactor) {

    private val viewModel: VideoRedactorViewModel by viewModel()
    private val binding by viewBinding(FragmentVideoRedactorBinding::bind)

    private val getVideo = registerForActivityResult(GetVideoContract()) { uri ->
        viewModel.onVideoUriReceived(uri)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        collectWhileStarted(viewModel.navigationCommands.receiveAsFlow()) { command ->
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupInsets()
        binding.loadVideoButton.setOnClickListener {
            getVideo.launch()
        }

    }

    private fun setupInsets() {
        binding.contentGuidelineTop.registerSystemInsetsListener { v, insets, _, _ ->
            binding.contentGuidelineTop.setGuidelineBegin(insets.top)
        }
    }
}