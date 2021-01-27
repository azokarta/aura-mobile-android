package kz.aura.merp.employee.fragment.master

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import kz.aura.merp.employee.R
import kz.aura.merp.employee.data.model.*
import kz.aura.merp.employee.data.viewmodel.MasterViewModel
import kz.aura.merp.employee.data.viewmodel.ReferenceViewModel
import kz.aura.merp.employee.databinding.FragmentServiceApplicationBusinessProcessesBinding
import kz.aura.merp.employee.util.Helpers.getStaffId
import im.delight.android.location.SimpleLocation
import kotlinx.android.synthetic.main.fragment_service_application_business_processes.*
import kz.aura.merp.employee.adapter.StepsAdapter

private const val ARG_PARAM1 = "serviceApplication"
private const val bpId = 3

class ServiceApplicationBusinessFragment : Fragment(), StepsAdapter.Companion.CompletedStepListener {

    private var serviceApplication: ServiceApplication? = null
    var step = 0;
    private val results = arrayListOf<ServiceApplicationStatus>()
    private var trackStepOrdersBusinessProcesses = arrayListOf<String>()
    private val stepsAdapter: StepsAdapter by lazy { StepsAdapter(this) }
    private lateinit var location: SimpleLocation
    private var _binding: FragmentServiceApplicationBusinessProcessesBinding? = null
    private val binding get() = _binding!!
    private var masterId: Long? = null
    private val mMasterViewModel: MasterViewModel by viewModels()
    private val mReferenceViewModel: ReferenceViewModel by viewModels()

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
        _binding = FragmentServiceApplicationBusinessProcessesBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.serviceApplication = serviceApplication

        // Observe MutableLiveData
        setObserve()

        mReferenceViewModel.fetchServiceApplicationStatus()
        mReferenceViewModel.fetchTrackStepOrdersBusinessProcesses(bpId)
        mMasterViewModel.fetchTrackEmpProcessServiceApplication(serviceApplication!!.id)

        location = SimpleLocation(this.requireContext())

        // Set master id
        masterId = getStaffId(this.requireContext())

        initStepView()

        return binding.root
    }

    private fun setObserve() {
        mReferenceViewModel.serviceApplicationStatus.observe(viewLifecycleOwner, Observer { data ->
            binding.resultBtn.text = data[serviceApplication!!.appStatus].nameRu
            results.addAll(data)
        })
        mReferenceViewModel.trackStepOrdersBusinessProcesses.observe(viewLifecycleOwner, Observer { data ->
            trackStepOrdersBusinessProcesses.addAll(data.map { it.trackStepNameRu } as ArrayList)
            stepsAdapter.setData(trackStepOrdersBusinessProcesses)
        })
        mMasterViewModel.updatedServiceApplication.observe(viewLifecycleOwner, Observer { data ->
            serviceApplication = data
            binding.serviceApplication = data
            binding.executePendingBindings() // Update view
            Snackbar.make(binding.businessProcessesSaveBtn, getString(R.string.successfullySaved), Snackbar.LENGTH_LONG).show()
        })
        mMasterViewModel.trackEmpProcessServiceApplication.observe(viewLifecycleOwner, Observer { data ->
            step = data.size
            initBtnListeners()
            initStepView()
            stepsAdapter.setStep(step)
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showResultsAlertDialog() {
        val demoResultsByLang = results.map { it.nameRu } as ArrayList
        val builder = AlertDialog.Builder(this.requireContext())

        builder.setTitle("Результат")

        builder.setItems(demoResultsByLang.toArray(arrayOfNulls<String>(0))) { _, i ->
            serviceApplication!!.appStatus = i
            binding.resultBtn.text = results[i].nameRu
        }

        builder.setPositiveButton("Отмена") { dialog, _ ->
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
    }

    override fun onResume() {
        super.onResume()

        // make the device update its location
        location.beginUpdates()

        // ...
    }

    override fun onPause() {
        // stop location updates (saves battery)
        location.endUpdates()

        // ...
        super.onPause()
    }

    private fun initBtnListeners() {
        binding.resultBtn.setOnClickListener {
            showResultsAlertDialog()
        }

        binding.businessProcessesSaveBtn.setOnClickListener {
            serviceApplication!!.description = service_business_processes_cause.text.toString()
            mMasterViewModel.updateServiceApplication(serviceApplication!!)
        }
    }

    private fun initStepView() {
        binding.stepsRecyclerView.layoutManager = LinearLayoutManager(this.requireContext())
        binding.stepsRecyclerView.adapter = stepsAdapter
        binding.stepsRecyclerView.isNestedScrollingEnabled = false
    }

    override fun stepCompleted(position: Int) {
        step+=1
        val longitude = location.longitude.toString()
        val latitude = location.latitude.toString()
        val completed = TrackEmpProcess(null, longitude, latitude, step, null, serviceApplication!!.id)
        mMasterViewModel.updateStepBusinessProcess(completed)
        mReferenceViewModel.createStaffLocation(StaffLocation(masterId!!, maTbpId = bpId, longitude = longitude, latitude = latitude, maTrackStepId = step))
    }

    companion object {
        @JvmStatic
        fun newInstance(serviceApplication: ServiceApplication) =
            ServiceApplicationBusinessFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_PARAM1, serviceApplication)
                }
            }
    }
}