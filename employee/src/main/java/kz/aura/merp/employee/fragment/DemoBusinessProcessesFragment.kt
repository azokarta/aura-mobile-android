package kz.aura.merp.employee.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import im.delight.android.location.SimpleLocation
import kotlinx.android.synthetic.main.fragment_demo_business_processes.*
import kz.aura.merp.employee.R
import kz.aura.merp.employee.data.model.*
import kz.aura.merp.employee.data.viewmodel.DemoViewModel
import kz.aura.merp.employee.data.viewmodel.ReferenceViewModel
import kz.aura.merp.employee.databinding.FragmentDemoBusinessProcessesBinding
import kz.aura.merp.employee.util.Helpers
import kz.aura.merp.employee.util.Helpers.saveData
import kz.aura.merp.employee.util.ProgressDialog


private const val bpId = 1
private const val ARG_PARAM1 = "demo"
private const val soldId = 4

class DemoBusinessProcessesFragment : Fragment() {

    var step = 0;
    private var demo: Demo? = null
    private val demoResults = arrayListOf<DemoResult>()
    private var trackStepOrdersBusinessProcesses = arrayListOf<TrackStepOrdersBusinessProcess>()
    private lateinit var location: SimpleLocation
    private var _binding: FragmentDemoBusinessProcessesBinding? = null
    private val binding get() = _binding!!
    private var dealerId: Long? = null
    private val mDemoViewModel: DemoViewModel by activityViewModels()
    private val mReferenceViewModel: ReferenceViewModel by activityViewModels()
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            demo = it.getSerializable(ARG_PARAM1) as Demo?
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Data binding
        _binding = FragmentDemoBusinessProcessesBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.demo = demo

        // Observe MutableLiveData
        setObserve()

        mReferenceViewModel.fetchDemoResults()
        mReferenceViewModel.fetchTrackStepOrdersBusinessProcesses(bpId)
        mDemoViewModel.fetchTrackEmpProcessDemo(demo!!.demoId)

        // construct a new instance of SimpleLocation
        location = SimpleLocation(this.requireContext());

        // if we can't access the location yet
        if (!location.hasLocationEnabled()) {
            // ask the user to enable location access
            SimpleLocation.openSettings(this.requireContext());
        }

        // Set dealer id
        dealerId = Helpers.getStaffId(this.requireContext())

        // PhoneNumber Formatter
        binding.ccp.registerCarrierNumberEditText(binding.phoneNumberEditText)

        // Initialize Loading Dialog
        progressDialog = ProgressDialog(this.requireContext())

        checkDemoStatusIsSold()

        return binding.root
    }

    private fun setObserve() {
        mReferenceViewModel.demoResults.observe(viewLifecycleOwner, Observer { data ->
            binding.demoResultBtn.text = data[demo!!.resultId!!].nameRu
            demoResults.addAll(data)
        })
        mReferenceViewModel.trackStepOrdersBusinessProcesses.observe(
            viewLifecycleOwner,
            Observer { data ->
                trackStepOrdersBusinessProcesses.addAll(data)
                initBtnListeners()
                initStepView(trackStepOrdersBusinessProcesses.map { it.trackStepNameRu } as ArrayList)
            })
        mDemoViewModel.updatedDemo.observe(viewLifecycleOwner, Observer { data ->
            demo = data
            binding.demo = data
            binding.executePendingBindings() // Update view
            checkDemoStatusIsSold()
            binding.demoResultBtn.text = demoResults[data!!.resultId!!].nameRu
            progressDialog.hideLoading()
        })
        mDemoViewModel.trackEmpProcessDemo.observe(viewLifecycleOwner, Observer { data ->
            step = data.size
            initStepView(trackStepOrdersBusinessProcesses.map { it.trackStepNameRu } as ArrayList)
            showButtonsByStep()
        })
        mDemoViewModel.smsSent.observe(viewLifecycleOwner, Observer { data ->
            demo!!.ocrDemoStatus = "SENT_SMS"
            mDemoViewModel.updatedDemo.postValue(demo!!)
            checkDemoStatusIsSold()
            saveData(demo!!, this.requireContext())
            progressDialog.hideLoading()
        })
        mDemoViewModel.error.observe(viewLifecycleOwner, Observer { data ->
            progressDialog.hideLoading()
        })
    }

    private fun checkDemoStatusIsSold() {
        if (demo!!.resultId == soldId) {
            when (demo!!.ocrDemoStatus) {
                "CONFIRMED" -> {
                    binding.demoStatus.visibility = View.VISIBLE
                    binding.demoStatus.text = getString(R.string.userConfirmed)
                    binding.receiveCustomerConfirmation.visibility = View.GONE
                    hideSmsForm()
                }
                "SENT_SMS" -> {
                    binding.demoStatus.visibility = View.VISIBLE
                    binding.demoStatus.text = getString(R.string.smsSent)
                    hideSmsForm()
                    binding.receiveCustomerConfirmation.visibility = View.VISIBLE
                }
                else -> {
                    binding.demoStatus.text = null
                    binding.phoneNumberEditText.visibility = View.VISIBLE
                    binding.ccp.visibility = View.VISIBLE
                    binding.sendSms.visibility = View.VISIBLE
                    binding.receiveCustomerConfirmation.visibility = View.VISIBLE
                }
            }

        } else {
            hideSmsForm()
            binding.receiveCustomerConfirmation.visibility = View.GONE
            binding.demoStatus.visibility = View.GONE
        }
    }

    private fun hideSmsForm() {
        binding.phoneNumberEditText.visibility = View.GONE
        binding.ccp.visibility = View.GONE
        binding.sendSms.visibility = View.GONE
    }

    private fun stepIncrement(steps: ArrayList<TrackStepOrdersBusinessProcess>) {
        if (steps.size-1 >= step) {
            step += 1
            binding.stepView.setStepsViewIndicatorComplectingPosition(step)
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

    private fun showDemoResultsAlertDialog() {
        val demoResultsByLang = demoResults.map { it.nameRu } as ArrayList
        val builder = AlertDialog.Builder(this.requireContext())

        builder.setTitle("Результат демо")

        builder.setItems(demoResultsByLang.toArray(arrayOfNulls<String>(0))) { _, i ->
            demo!!.resultId = i

            binding.demoResultBtn.text = demoResults[i].nameRu
            checkDemoStatusIsSold()
        }

        builder.setPositiveButton("Отмена") { dialog, _ ->
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
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

        binding.demoResultBtn.setOnClickListener {
            showDemoResultsAlertDialog()
        }

        binding.demoBusinessProcessesSaveBtn.setOnClickListener {
            demo!!.note = demo_business_processes_cause.text.toString()
            mDemoViewModel.updateDemo(demo!!)
        }

        binding.sendSms.setOnClickListener {
            if (ccp.isValidFullNumber) {
                mDemoViewModel.sendSms(
                    demo!!.demoId, ccp.selectedCountryCode, ccp.fullNumberWithPlus.replace(
                        ccp.selectedCountryCodeWithPlus,
                        ""
                    )
                )
                progressDialog.showLoading()
            }
        }

        binding.receiveCustomerConfirmation.setOnClickListener {
            mDemoViewModel.updateStatus(demo!!.demoId)
            progressDialog.showLoading()
        }
    }


    private fun showButtonsByStep() {
        when (step) {
            0 -> binding.completedBtn1.visibility = View.VISIBLE
            1 -> binding.completedBtn2.visibility = View.VISIBLE
            2 -> binding.completedBtn3.visibility = View.VISIBLE
            3 -> binding.completedBtn4.visibility = View.VISIBLE
        }
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun completeBusinessProcess() {
        val longitude = location.longitude.toString()
        val latitude = location.latitude.toString()
        val completed = TrackEmpProcess(null, longitude, latitude, step + 1, null, demo!!.demoId)
        mDemoViewModel.updateStepBusinessProcess(completed)
        stepIncrement(trackStepOrdersBusinessProcesses)
        mReferenceViewModel.createStaffLocation(
            StaffLocation(
                dealerId!!,
                maTbpId = bpId,
                longitude = longitude,
                latitude = latitude,
                maTrackStepId = step + 1
            )
        )
    }

    companion object {
        @JvmStatic
        fun newInstance(demo: Demo) =
            DemoBusinessProcessesFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_PARAM1, demo)
                }
            }
    }
}