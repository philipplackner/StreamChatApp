package com.plcoding.streamchatapp.ui.channel

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.plcoding.streamchatapp.R
import com.plcoding.streamchatapp.databinding.FragmentChannelBinding
import com.plcoding.streamchatapp.ui.BindingFragment
import com.plcoding.streamchatapp.util.navigateSafely
import dagger.hilt.android.AndroidEntryPoint
import io.getstream.chat.android.client.models.Filters
import io.getstream.chat.android.ui.channel.list.header.viewmodel.ChannelListHeaderViewModel
import io.getstream.chat.android.ui.channel.list.header.viewmodel.bindView
import io.getstream.chat.android.ui.channel.list.viewmodel.ChannelListViewModel
import io.getstream.chat.android.ui.channel.list.viewmodel.bindView
import io.getstream.chat.android.ui.channel.list.viewmodel.factory.ChannelListViewModelFactory
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class ChannelFragment : BindingFragment<FragmentChannelBinding>() {

    override val bindingInflater: (LayoutInflater) -> ViewBinding
        get() = FragmentChannelBinding::inflate

    private val viewModel: ChannelViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val user = viewModel.getUser()
        if(user == null) {
            findNavController().popBackStack()
            return
        }

        val factory = ChannelListViewModelFactory(
            filter = Filters.and(
                Filters.eq("type", "messaging"),
            ),
            sort = ChannelListViewModel.DEFAULT_SORT,
            limit = 30
        )
        val channelListViewModel: ChannelListViewModel by viewModels { factory }
        val channelListHeaderViewModel: ChannelListHeaderViewModel by viewModels()
        channelListViewModel.bindView(binding.channelListView, viewLifecycleOwner)
        channelListHeaderViewModel.bindView(binding.channelListHeaderView, viewLifecycleOwner)

        binding.channelListHeaderView.setOnUserAvatarClickListener {
            viewModel.logout()
            findNavController().popBackStack()
        }

        binding.channelListHeaderView.setOnActionButtonClickListener {
            findNavController().navigateSafely(
                R.id.action_channelFragment_to_createChannelDialog
            )
        }

        binding.channelListView.setChannelItemClickListener { channel ->
            findNavController().navigateSafely(
                R.id.action_channelFragment_to_chatFragment,
                Bundle().apply { putString("channelId", channel.cid) }
            )
        }

        lifecycleScope.launchWhenStarted {
            viewModel.createChannelEvent.collect { event ->
                when(event) {
                    is ChannelViewModel.CreateChannelEvent.Error -> {
                        Toast.makeText(
                            requireContext(),
                            event.error,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    is ChannelViewModel.CreateChannelEvent.Success -> {
                        Toast.makeText(
                            requireContext(),
                            R.string.channel_created,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
    }
}