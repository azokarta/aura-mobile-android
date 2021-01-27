package kz.aura.merp.employee.fragment.master

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import kz.aura.merp.employee.data.model.ServiceApplication
import kz.aura.merp.employee.data.viewmodel.MasterViewModel
import kz.aura.merp.employee.databinding.FragmentServiceApplicationDataBinding
import kotlinx.android.synthetic.main.fragment_service_application_data.*
import kz.aura.merp.employee.R

private const val ARG_PARAM1 = "serviceApplication"

class ServiceApplicationDataFragment : Fragment() {

    private var serviceApplication: ServiceApplication? = null
    private var _binding: FragmentServiceApplicationDataBinding? = null
    private val binding get() = _binding!!
    private val mMasterViewModel: MasterViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            serviceApplication = it.getSerializable(ARG_PARAM1) as ServiceApplication?
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Data binding
        _binding = FragmentServiceApplicationDataBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.serviceApplication = serviceApplication

        // Observe MutableLiveData
        mMasterViewModel.updatedServiceApplication.observe(viewLifecycleOwner, Observer { data ->
            serviceApplication = data
            binding.serviceApplication = data
            binding.executePendingBindings()
            Snackbar.make(binding.serviceApplicationDataSaveBtn, getString(R.string.successfullySaved), Snackbar.LENGTH_LONG).show()
        })

        // Initialize Listeners
        binding.serviceApplicationDataSaveBtn.setOnClickListener {
            serviceApplication!!.taxiExpenseAmount = demo_data_taxi_expences.text.toString().toDouble()
            mMasterViewModel.updateServiceApplication(serviceApplication!!)
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance(serviceApplication: ServiceApplication) =
            ServiceApplicationDataFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_PARAM1, serviceApplication)
                }
            }
    }
}