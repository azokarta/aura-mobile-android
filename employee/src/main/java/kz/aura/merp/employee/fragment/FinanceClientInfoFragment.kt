package kz.aura.merp.employee.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import kz.aura.merp.employee.data.model.*
import kz.aura.merp.employee.data.viewmodel.FinanceViewModel
import kz.aura.merp.employee.databinding.FragmentFinanceClientInfoBinding
import kotlinx.android.synthetic.main.fragment_finance_client_info.*
import kotlinx.android.synthetic.main.fragment_finance_client_info.view.*
import kz.aura.merp.employee.R
import kz.aura.merp.employee.activity.MapActivity

private const val ARG_PARAM1 = "plan"

class FinanceClientInfoFragment : Fragment() {

    private var client: Client? = null
    private var _binding: FragmentFinanceClientInfoBinding? = null
    private val binding get() = _binding!!
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
        _binding = FragmentFinanceClientInfoBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.client = client

        // Observe MutableLiveData
        mFinanceViewModel.updatedClient.observe(viewLifecycleOwner, Observer { data ->
            client = data
            binding.client = data
            binding.executePendingBindings()
            Snackbar.make(binding.infoSaveBtn, getString(R.string.successfullySaved), Snackbar.LENGTH_LONG).show()
        })

        // Initialize Listeners
        binding.infoSaveBtn.setOnClickListener {
            client!!.taxiExpenseAmount = info_taxi_taxi_expences.text.toString().toDouble()
            mFinanceViewModel.updateClient(client!!)
        }
        binding.mapBtn.setOnClickListener {
            val intent = Intent(this.context, MapActivity::class.java)
            startActivity(intent)
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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