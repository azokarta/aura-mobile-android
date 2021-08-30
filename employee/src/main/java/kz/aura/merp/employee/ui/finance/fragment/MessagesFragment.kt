package kz.aura.merp.employee.ui.finance.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import kz.aura.merp.employee.R
import kz.aura.merp.employee.adapter.MessagesAdapter
import kz.aura.merp.employee.databinding.FragmentMessagesBinding
import kz.aura.merp.employee.base.NetworkResult
import kz.aura.merp.employee.viewmodel.SharedViewModel
import kz.aura.merp.employee.viewmodel.finance.MessagesViewModel

@AndroidEntryPoint
class MessagesFragment : Fragment(R.layout.fragment_messages) {

    private var _binding: FragmentMessagesBinding? = null
    private val binding get() = _binding!!

    private val messagesViewModel: MessagesViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by viewModels()
    private val messagesAdapter: MessagesAdapter by lazy { MessagesAdapter() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMessagesBinding.bind(view)

        with (binding) {
            lifecycleOwner = this@MessagesFragment
            sharedViewModel = this@MessagesFragment.sharedViewModel
        }

        setupRecyclerView()

        messagesViewModel.messagesResponse.observe(viewLifecycleOwner, { res ->
            when (res) {
                is NetworkResult.Success -> {
                    sharedViewModel.setResponse(res)
                    val messages = res.data
                    messagesAdapter.submitList(messages)
                }
                else -> sharedViewModel.setResponse(res)
            }
        })

        messagesViewModel.preferences.salary?.staffId.let {
            messagesViewModel.fetchMessages(it)
        }
    }

    private fun setupRecyclerView() {
        binding.messagesRecyclerView.adapter = messagesAdapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}