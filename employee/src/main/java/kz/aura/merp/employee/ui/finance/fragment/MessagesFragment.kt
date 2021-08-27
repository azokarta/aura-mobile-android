package kz.aura.merp.employee.ui.finance.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kz.aura.merp.employee.adapter.MessagesAdapter
import kz.aura.merp.employee.databinding.FragmentMessagesBinding
import kz.aura.merp.employee.base.NetworkResult
import kz.aura.merp.employee.viewmodel.FinanceViewModel
import kz.aura.merp.employee.viewmodel.SharedViewModel

@AndroidEntryPoint
class MessagesFragment : Fragment() {

    private var _binding: FragmentMessagesBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var financeViewModel: FinanceViewModel
    private lateinit var sharedViewModel: SharedViewModel
    private val messagesAdapter: MessagesAdapter by lazy { MessagesAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        financeViewModel = ViewModelProvider(this).get(FinanceViewModel::class.java)
        sharedViewModel = ViewModelProvider(this).get(SharedViewModel::class.java)

        _binding = FragmentMessagesBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.sharedViewModel = sharedViewModel
        val root: View = binding.root

        setupRecyclerView()

        financeViewModel.messagesResponse.observe(viewLifecycleOwner, { res ->
            when (res) {
                is NetworkResult.Success -> {
                    sharedViewModel.setResponse(res)
                    res.data?.let { messagesAdapter.setData(it) }
                }
                else -> sharedViewModel.setResponse(res)
            }
        })

        financeViewModel.salary.observe(viewLifecycleOwner, { salary ->
            salary?.staffId?.let { financeViewModel.fetchMessages(it) }
        })

        financeViewModel.getSalary()

        return root
    }

    private fun setupRecyclerView() {
        binding.messagesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.messagesRecyclerView.adapter = messagesAdapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}