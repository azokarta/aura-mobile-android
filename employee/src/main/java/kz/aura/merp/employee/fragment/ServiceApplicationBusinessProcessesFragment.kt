package kz.aura.merp.employee.fragment

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import kz.aura.merp.employee.R
import kz.aura.merp.employee.data.model.*
import kz.aura.merp.employee.data.viewmodel.MasterViewModel
import kz.aura.merp.employee.data.viewmodel.ReferenceViewModel
import kz.aura.merp.employee.databinding.FragmentServiceApplicationBusinessProcessesBinding
import kz.aura.merp.employee.util.Helpers.getStaffId
import im.delight.android.location.SimpleLocation
import kotlinx.android.synthetic.main.fragment_service_application_business_processes.*
import kotlinx.android.synthetic.main.fragment_service_application_business_processes.stepView

private const val ARG_PARAM1 = "serviceApplication"
private const val bpId = 3

class ServiceApplicationBusinessFragment : Fragment() {

    private var serviceApplication: ServiceApplication? = null
    var step = 0;
    private val results = arrayListOf<ServiceApplicationStatus>()
    private var trackStepOrdersBusinessProcesses = arrayListOf<TrackStepOrdersBusinessProcess>()
    private lateinit var location: SimpleLocation
    private var _binding: FragmentServiceApplicationBusinessProcessesBinding? = null
    private val binding get() = _binding!!
    private var masterId: Long? = null
    private val mMasterViewModel: MasterViewModel by activityViewModels()
    private val mReferenceViewModel: ReferenceViewModel by activityViewModels()

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

        location = SimpleLocation(this.requireContext());

        // if we can't access the location yet
        if (!location.hasLocationEnabled()) {
            // ask the user to enable location access
            SimpleLocation.openSettings(this.requireContext());
        }

        // Set master id
        masterId = getStaffId(this.requireContext())

        return binding.root
    }

    private fun setObserve() {
        mReferenceViewModel.serviceApplicationStatus.observe(viewLifecycleOwner, Observer { data ->
            binding.resultBtn.text = data[serviceApplication!!.appStatus].nameRu
            results.addAll(data)
        })
        mReferenceViewModel.trackStepOrdersBusinessProcesses.observe(viewLifecycleOwner, Observer { data ->
            trackStepOrdersBusinessProcesses.addAll(data)
            initBtnListeners()
            initStepView(trackStepOrdersBusinessProcesses.map { it.trackStepNameRu } as ArrayList)
        })
        mMasterViewModel.updatedServiceApplication.observe(viewLifecycleOwner, Observer { data ->
            serviceApplication = data
            binding.serviceApplication = data
            binding.executePendingBindings() // Update view
            Snackbar.make(binding.businessProcessesSaveBtn, getString(R.string.successfullySaved), Snackbar.LENGTH_LONG).show()
        })
        mMasterViewModel.trackEmpProcessServiceApplication.observe(viewLifecycleOwner, Observer { data ->
            step = data.size
            initStepView(trackStepOrdersBusinessProcesses.map { it.trackStepNameRu } as ArrayList)
            showButtonsByStep()
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

    private fun showButtonsByStep() {
        when (step) {
            0 -> binding.completedBtn1.visibility = View.VISIBLE
            1 -> binding.completedBtn2.visibility = View.VISIBLE
            2 -> binding.completedBtn3.visibility = View.VISIBLE
            3 -> binding.completedBtn4.visibility = View.VISIBLE
        }
    }

    private fun completeBusinessProcess() {
        val longitude = location.longitude.toString()
        val latitude = location.latitude.toString()
        val completed = TrackEmpProcess(null, longitude, latitude, step+1, null, serviceApplication!!.id)
        mMasterViewModel.updateStepBusinessProcess(completed)
        mReferenceViewModel.createStaffLocation(StaffLocation(masterId!!, maTbpId = bpId, longitude = longitude, latitude = latitude, maTrackStepId = step+1))
        stepIncrement(trackStepOrdersBusinessProcesses)
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
        binding.completedBtn1.setOnClickListener {
            completeBusinessProcess()
            binding.completedBtn1.visibility = View.INVISIBLE
            binding.completedBtn2.visibility = View.VISIBLE
        }
        binding.completedBtn2.setOnClickListener {
            completeBusinessProcess()
            binding.completedBtn2.visibility = View.INVISIBLE
            binding.completedBtn3.visibility = View.VISIBLE
        }
        binding.completedBtn3.setOnClickListener {
            completeBusinessProcess()
            binding.completedBtn3.visibility = View.INVISIBLE
            binding.completedBtn4.visibility = View.VISIBLE
        }
        binding.completedBtn4.setOnClickListener {
            completeBusinessProcess()
            binding.completedBtn4.visibility = View.INVISIBLE
        }

        binding.resultBtn.setOnClickListener {
            showResultsAlertDialog()
        }

        binding.businessProcessesSaveBtn.setOnClickListener {
            serviceApplication!!.description = service_business_processes_cause.text.toString()
            mMasterViewModel.updateServiceApplication(serviceApplication!!)
        }
    }

    private fun initStepView(steps: ArrayList<String>) {
        binding.stepView.setStepsViewIndicatorComplectingPosition(step)
            .reverseDraw(false)//default is true
            .setStepViewTexts(steps)
            .setLinePaddingProportion(0.85f)
            .setStepsViewIndicatorCompletedLineColor(
                ContextCompat.getColor(
                    this.requireContext(),
                    android.R.color.black
                )
            ) //StepsViewIndicator
            .setStepsViewIndicatorUnCompletedLineColor(
                ContextCompat.getColor(
                    this.requireContext(),
                    android.R.color.black
                )
            ) //StepsViewIndicator
            .setStepViewComplectedTextColor(
                ContextCompat.getColor(
                    this.requireContext(),
                    android.R.color.black
                )
            ) //StepsView text
            .setStepViewUnComplectedTextColor(
                ContextCompat.getColor(
                    this.requireContext(),
                    android.R.color.darker_gray
                )
            ) //StepsView text
            .setStepsViewIndicatorCompleteIcon(
                ContextCompat.getDrawable(
                    this.requireContext(),
                    R.drawable.ic_baseline_check_circle_24
                )
            ) //StepsViewIndicator CompleteIcon
            .setStepsViewIndicatorDefaultIcon(
                ContextCompat.getDrawable(
                    this.requireContext(),
                    R.drawable.default_icon
                )
            ) //StepsViewIndicator DefaultIcon
            .setStepsViewIndicatorAttentionIcon(
                ContextCompat.getDrawable(
                    this.requireContext(),
                    R.drawable.ic_baseline_radio_button_checked_24
                )
            ) //StepsViewIndicator AttentionIcon
    }

    private fun stepIncrement(steps: ArrayList<TrackStepOrdersBusinessProcess>) {
        if (steps.size-1 >= step) {
            step += 1
            binding.stepView.setStepsViewIndicatorComplectingPosition(step)
        }
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