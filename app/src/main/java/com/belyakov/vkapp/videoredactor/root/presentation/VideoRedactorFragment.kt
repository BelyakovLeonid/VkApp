package com.belyakov.vkapp.videoredactor.root.presentation

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import by.kirich1409.viewbindingdelegate.viewBinding
import com.belyakov.vkapp.R
import com.belyakov.vkapp.base.utils.collectWhileStarted
import com.belyakov.vkapp.databinding.FragmentVideoRedactorBinding
import com.belyakov.vkapp.videoredactor.root.presentation.model.VideoredactorNavigationCommand
import kotlinx.coroutines.flow.receiveAsFlow
import org.koin.androidx.viewmodel.ext.android.viewModel

class VideoRedactorFragment : Fragment(R.layout.fragment_video_redactor) {

    private val viewModel: VideoRedactorViewModel by viewModel()
    private val binding by viewBinding(FragmentVideoRedactorBinding::bind)

    private val videoredactorNavHost
        get() = Navigation.findNavController(requireActivity(), R.id.videoredactorContent)

    override fun onAttach(context: Context) {
        super.onAttach(context)
        collectWhileStarted(viewModel.navigationCommands.receiveAsFlow()) { command ->
            when (command) {
                is VideoredactorNavigationCommand.OpenVideo -> videoredactorNavHost.navigate(R.id.playerFragment)
            }
        }
    }
}