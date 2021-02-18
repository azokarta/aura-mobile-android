package kz.aura.merp.employee.fragment.master

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.snackbar.Snackbar
import kz.aura.merp.employee.data.model.ServiceApplication
import kz.aura.merp.employee.data.viewmodel.MasterViewModel
import kz.aura.merp.employee.databinding.FragmentServiceApplicationDataBinding
import kz.aura.merp.employee.R

private const val ARG_PARAM1 = "serviceApplication"

class ServiceApplicationDataFragment : Fragment() {

    private var serviceApplication: ServiceApplication? = null
    private var _binding: FragmentServiceApplicationDataBinding? = null
    private val binding get() = _binding!!
    private val mMasterViewModel: MasterViewModel by viewModels()

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

        setHasOptionsMenu(true)

        // Observe MutableLiveData
        mMasterViewModel.updatedServiceApplication.observe(viewLifecycleOwner, { data ->
            serviceApplication = data
            binding.serviceApplication = data
            binding.executePendingBindings()
            Snackbar.make(binding.root, getString(R.string.successfullySaved), Snackbar.LENGTH_LONG).show()
        })

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.save_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.save -> {
                serviceApplication!!.taxiExpenseAmount = binding.demoDataTaxiExpences.text.toString().toDouble()
                mMasterViewModel.updateServiceApplication(serviceApplication!!)
            }
        }
        return super.onOptionsItemSelected(item)
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