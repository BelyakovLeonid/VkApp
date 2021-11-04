package com.belyakov.vkapp.videoredactor.loader.presentation

import android.os.Bundle
import android.view.View
import androidx.activity.result.launch
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.belyakov.vkapp.R
import com.belyakov.vkapp.base.contracts.GetVideoContract
import com.belyakov.vkapp.base.utils.registerSystemInsetsListener
import com.belyakov.vkapp.databinding.FragmentVideoLoaderBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoaderFragment : Fragment(R.layout.fragment_video_loader), View.OnClickListener {

    private val viewModel: LoaderViewModel by viewModel()
    private val binding by viewBinding(FragmentVideoLoaderBinding::bind)

    private val getVideo = registerForActivityResult(GetVideoContract()) { uri ->
        viewModel.onVideoUriReceived(uri)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.loadVideoButton.setOnClickListener(this)
        setupInsets()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            binding.loadVideoButton.id -> getVideo.launch()
            binding.toolbarBack.id -> findNavController().popBackStack()
        }
    }

    private fun setupInsets() {
        binding.root.registerSystemInsetsListener { v, insets, margins, paddings ->
            v.updatePadding(top = insets.top + paddings.top)
        }
    }
}