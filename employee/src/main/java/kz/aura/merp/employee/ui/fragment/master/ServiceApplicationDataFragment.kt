package kz.aura.merp.employee.ui.fragment.master

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import kz.aura.merp.employee.data.model.ServiceApplication
import kz.aura.merp.employee.viewmodel.MasterViewModel
import kz.aura.merp.employee.databinding.FragmentServiceApplicationDataBinding
import kz.aura.merp.employee.R
import kz.aura.merp.employee.adapter.PhoneNumbersAdapter
import kz.aura.merp.employee.util.NetworkResult
import kz.aura.merp.employee.util.declareErrorByStatus

private const val ARG_PARAM1 = "serviceApplication"

class ServiceApplicationDataFragment : Fragment() {

    private var serviceApplication: ServiceApplication? = null
    private var _binding: FragmentServiceApplicationDataBinding? = null
    private val binding get() = _binding!!
    private val mMasterViewModel: MasterViewModel by viewModels()
    private val phoneNumbersAdapter: PhoneNumbersAdapter by lazy { PhoneNumbersAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            serviceApplication = it.getParcelable(ARG_PARAM1) as ServiceApplication?
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

//        // Observe MutableLiveData
//        mMasterViewModel.updatedServiceApplication.observe(viewLifecycleOwner, { res ->
//            when (res) {
//                is NetworkResult.Success -> {
//
//                }
//                is NetworkResult.Loading -> mSharedViewModel.hideLoading()
//                is NetworkResult.Error -> {
//                    mSharedViewModel.hideLoading()
//                    declareErrorByStatus(res.message, res.status, this)
//                }
//            }
//            serviceApplication = data
//            binding.serviceApplication = data
//            binding.executePendingBindings()
//            Snackbar.make(binding.root, getString(R.string.successfullySaved), Snackbar.LENGTH_LONG).show()
//        })

        initPhoneNumbers()

        mMasterViewModel.filtersResponse.observe(viewLifecycleOwner, { res ->
            when (res) {
                is NetworkResult.Success -> {
                    binding.filters = res.data!!
                }
                is NetworkResult.Loading -> {}
                is NetworkResult.Error -> {
                    declareErrorByStatus(res.message, res.status, requireContext())
                }
            }
        })

        // Fetch filters
        serviceApplication?.applicationNumber?.let { mMasterViewModel.fetchFilters(it) }

        return binding.root
    }

    private fun initPhoneNumbers() {
        binding.phoneNumbers.layoutManager = LinearLayoutManager(requireContext())
        binding.phoneNumbers.adapter = phoneNumbersAdapter
        phoneNumbersAdapter.setData(serviceApplication!!.customerPhoneNumbers)
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
                    putParcelable(ARG_PARAM1, serviceApplication)
                }
            }
    }
}