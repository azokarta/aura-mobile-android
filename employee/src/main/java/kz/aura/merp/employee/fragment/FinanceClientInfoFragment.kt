package kz.aura.merp.employee.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import kz.aura.merp.employee.data.model.*
import kz.aura.merp.employee.data.viewmodel.FinanceViewModel
import kz.aura.merp.employee.databinding.FragmentFinanceAgentClientInfoBinding
import kz.aura.merp.employee.util.Helpers
import kotlinx.android.synthetic.main.fragment_finance_agent_client_info.*
import kotlinx.android.synthetic.main.fragment_finance_agent_client_info.view.*

private const val ARG_PARAM1 = "plan"

class FinanceClientInfoFragment : Fragment() {

    private var client: Client? = null
    private var binding: FragmentFinanceAgentClientInfoBinding? = null
    private val mFinanceViewModel: FinanceViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            client = it.getSerializable(ARG_PARAM1) as Client
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Data binding
        binding = FragmentFinanceAgentClientInfoBinding.inflate(inflater, container, false)
        binding!!.lifecycleOwner = this
        binding!!.client = client

        // Observe MutableLiveData
        mFinanceViewModel.updatedClient.observe(viewLifecycleOwner, Observer { data ->
            client = data
            binding!!.client = data
            binding!!.executePendingBindings()
        })
        mFinanceViewModel.error.observe(viewLifecycleOwner, Observer { error ->
            Helpers.exceptionHandler(error, this.requireContext())
        })


        // Initialize Listeners
        binding!!.root.info_save_btn.setOnClickListener {
            client!!.taxiExpenseAmount = info_taxi_taxi_expences.text.toString().toDouble()
            mFinanceViewModel.updateClient(client!!)
        }

        return binding!!.root
    }

    companion object {
        @JvmStatic
        fun newInstance(client: Client) =
            FinanceClientInfoFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_PARAM1, client)
                }
            }
    }
}