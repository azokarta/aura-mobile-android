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
import kz.aura.merp.employee.R
import kz.aura.merp.employee.data.model.*
import kz.aura.merp.employee.data.viewmodel.MasterViewModel
import kz.aura.merp.employee.data.viewmodel.ReferenceViewModel
import kz.aura.merp.employee.databinding.FragmentServiceApplicationBusinessProcessesBinding
import kz.aura.merp.employee.util.Helpers
import kz.aura.merp.employee.util.Helpers.getStaffId
import im.delight.android.location.SimpleLocation
import kotlinx.android.synthetic.main.fragment_service_application_business_processes.*
import kotlinx.android.synthetic.main.fragment_service_application_business_processes.completed_btn_1
import kotlinx.android.synthetic.main.fragment_service_application_business_processes.completed_btn_2
import kotlinx.android.synthetic.main.fragment_service_application_business_processes.completed_btn_3
import kotlinx.android.synthetic.main.fragment_service_application_business_processes.completed_btn_4
import kotlinx.android.synthetic.main.fragment_service_application_business_processes.step_view

private const val ARG_PARAM1 = "serviceApplication"
private const val bpId = 3

class ServiceApplicationBusinessFragment : Fragment() {

    private var serviceApplication: ServiceApplication? = null
    var step = 0;
    private val results = arrayListOf<ServiceApplicationStatus>()
    private var trackStepOrdersBusinessProcesses = arrayListOf<TrackStepOrdersBusinessProcess>()
    private lateinit var location: SimpleLocation
    private var binding: FragmentServiceApplicationBusinessProcessesBinding? = null
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
        binding = FragmentServiceApplicationBusinessProcessesBinding.inflate(inflater, container, false)
        binding!!.lifecycleOwner = this
        binding!!.serviceApplication = serviceApplication

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

        return binding!!.root
    }

    private fun setObserve() {
        mReferenceViewModel.serviceApplicationStatus.observe(viewLifecycleOwner, Observer { data ->
            service_result_btn.text = data[serviceApplication!!.appStatus].nameRu
            results.addAll(data)
        })
        mReferenceViewModel.trackStepOrdersBusinessProcesses.observe(viewLifecycleOwner, Observer { data ->
            trackStepOrdersBusinessProcesses.addAll(data)
            initBtnListeners()
            initStepView(trackStepOrdersBusinessProcesses.map { it.trackStepNameRu } as ArrayList)
        })
        mMasterViewModel.updatedServiceApplication.observe(viewLifecycleOwner, Observer { data ->
            serviceApplication = data
            binding!!.serviceApplication = data
            binding!!.executePendingBindings() // Update view
        })
        mMasterViewModel.trackEmpProcessServiceApplication.observe(viewLifecycleOwner, Observer { data ->
            step = data.size
            initStepView(trackStepOrdersBusinessProcesses.map { it.trackStepNameRu } as ArrayList)
            showButtonsByStep()
        })

        // Errors
        mMasterViewModel.error.observe(viewLifecycleOwner, Observer { error ->
            Helpers.exceptionHandler(error, this.requireContext())
        })
        mReferenceViewModel.error.observe(viewLifecycleOwner, Observer { error ->
            Helpers.exceptionHandler(error, this.requireContext())
        })
    }

    private fun showResultsAlertDialog() {
        val demoResultsByLang = results.map { it.nameRu } as ArrayList
        val builder = AlertDialog.Builder(this.requireContext())

        builder.setTitle("Результат")

        builder.setItems(demoResultsByLang.toArray(arrayOfNulls<String>(0))) { _, i ->
            serviceApplication!!.appStatus = i
            service_result_btn.text = results[i].nameRu
        }

        builder.setPositiveButton("Отмена") { dialog, _ ->
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
    }

    private fun showButtonsByStep() {
        when (step) {
            0 -> completed_btn_1.visibility = View.VISIBLE
            1 -> completed_btn_2.visibility = View.VISIBLE
            2 -> completed_btn_3.visibility = View.VISIBLE
            3 -> completed_btn_4.visibility = View.VISIBLE
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
        completed_btn_1.setOnClickListener {
            completeBusinessProcess()
            completed_btn_1.visibility = View.INVISIBLE
            completed_btn_2.visibility = View.VISIBLE
        }
        completed_btn_2.setOnClickListener {
            completeBusinessProcess()
            completed_btn_2.visibility = View.INVISIBLE
            completed_btn_3.visibility = View.VISIBLE
        }
        completed_btn_3.setOnClickListener {
            completeBusinessProcess()
            completed_btn_3.visibility = View.INVISIBLE
            completed_btn_4.visibility = View.VISIBLE
        }
        completed_btn_4.setOnClickListener {
            completeBusinessProcess()
            completed_btn_4.visibility = View.INVISIBLE
        }

        service_result_btn.setOnClickListener {
            showResultsAlertDialog()
        }

        service_business_processes_save_btn.setOnClickListener {
            serviceApplication!!.description = service_business_processes_cause.text.toString()
            mMasterViewModel.updateServiceApplication(serviceApplication!!)
        }
    }

    private fun initStepView(steps: ArrayList<String>) {
        step_view.setStepsViewIndicatorComplectingPosition(step)
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
            step_view.setStepsViewIndicatorComplectingPosition(step)
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