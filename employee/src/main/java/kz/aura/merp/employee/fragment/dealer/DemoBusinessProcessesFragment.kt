package kz.aura.merp.employee.fragment.dealer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import im.delight.android.location.SimpleLocation
import kotlinx.android.synthetic.main.fragment_demo_business_processes.*
import kz.aura.merp.employee.R
import kz.aura.merp.employee.adapter.StepsAdapter
import kz.aura.merp.employee.data.model.*
import kz.aura.merp.employee.data.viewmodel.DealerViewModel
import kz.aura.merp.employee.data.viewmodel.ReferenceViewModel
import kz.aura.merp.employee.databinding.FragmentDemoBusinessProcessesBinding
import kz.aura.merp.employee.fragment.finance.FinanceClientInfoFragment
import kz.aura.merp.employee.util.Helpers
import kz.aura.merp.employee.util.Helpers.saveData
import kz.aura.merp.employee.util.ProgressDialog


private const val bpId = 1
private const val ARG_PARAM1 = "demo"
private const val soldId = 4

class DemoBusinessProcessesFragment : Fragment(), StepsAdapter.Companion.CompletedStepListener {

    var step = 0;
    private var demo: Demo? = null
    private val demoResults = arrayListOf<DemoResult>()
    private var trackStepOrdersBusinessProcesses = arrayListOf<String>()
    private val stepsAdapter: StepsAdapter by lazy { StepsAdapter(this) }
    private lateinit var location: SimpleLocation
    private var _binding: FragmentDemoBusinessProcessesBinding? = null
    private val binding get() = _binding!!
    private var dealerId: Long? = null
    private val mDealerViewModel: DealerViewModel by viewModels()
    private val mReferenceViewModel: ReferenceViewModel by viewModels()
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
        mDealerViewModel.fetchTrackEmpProcessDemo(demo!!.demoId)

        // construct a new instance of SimpleLocation
        location = SimpleLocation(this.requireContext());

        // Set dealer id
        dealerId = Helpers.getStaffId(this.requireContext())

        // PhoneNumber Formatter
        binding.ccp.registerCarrierNumberEditText(binding.phoneNumberEditText)

        // Initialize Loading Dialog
        progressDialog = ProgressDialog(this.requireContext())

        checkDemoStatusIsSold()

        initStepView()

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
                trackStepOrdersBusinessProcesses.addAll(data.map { it.trackStepNameRu } as ArrayList)
                stepsAdapter.setData(trackStepOrdersBusinessProcesses)
            })
        mDealerViewModel.updatedDemo.observe(viewLifecycleOwner, Observer { data ->
            demo = data
            binding.demo = data
            binding.executePendingBindings() // Update view
            checkDemoStatusIsSold()
            binding.demoResultBtn.text = demoResults[data!!.resultId!!].nameRu
            progressDialog.hideLoading()
        })
        mDealerViewModel.trackEmpProcessDemo.observe(viewLifecycleOwner, Observer { data ->
            step = data.size
            initBtnListeners()
            initStepView()
            stepsAdapter.setStep(step)
        })
        mDealerViewModel.smsSent.observe(viewLifecycleOwner, Observer { data ->
            demo!!.ocrDemoStatus = "SENT_SMS"
            mDealerViewModel.updatedDemo.postValue(demo!!)
            checkDemoStatusIsSold()
            saveData(demo!!, this.requireContext())
            progressDialog.hideLoading()
        })
        mDealerViewModel.error.observe(viewLifecycleOwner, Observer { data ->
            progressDialog.hideLoading()
        })
    }

    private fun initStepView() {
        binding.stepsRecyclerView.layoutManager = LinearLayoutManager(this.requireContext())
        binding.stepsRecyclerView.adapter = stepsAdapter
        binding.stepsRecyclerView.isNestedScrollingEnabled = false
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
        binding.demoResultBtn.setOnClickListener {
            showDemoResultsAlertDialog()
        }

        binding.demoBusinessProcessesSaveBtn.setOnClickListener {
            demo!!.note = demo_business_processes_cause.text.toString()
            mDealerViewModel.updateDemo(demo!!)
        }

        binding.sendSms.setOnClickListener {
            if (ccp.isValidFullNumber) {
                mDealerViewModel.sendSms(
                    demo!!.demoId, ccp.selectedCountryCode, ccp.fullNumberWithPlus.replace(
                        ccp.selectedCountryCodeWithPlus,
                        ""
                    )
                )
                progressDialog.showLoading()
            }
        }

        binding.receiveCustomerConfirmation.setOnClickListener {
            mDealerViewModel.updateStatus(demo!!.demoId)
            progressDialog.showLoading()
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

    override fun stepCompleted(position: Int) {
        step+=1
        val longitude = location.longitude.toString()
        val latitude = location.latitude.toString()
        val completed = TrackEmpProcess(null, longitude, latitude, step, null, demo!!.demoId)
        mDealerViewModel.updateStepBusinessProcess(completed)
        mReferenceViewModel.createStaffLocation(
            StaffLocation(
                dealerId!!,
                maTbpId = bpId,
                longitude = longitude,
                latitude = latitude,
                maTrackStepId = step
            )
        )
    }

    companion object {
        @JvmStatic
        fun newInstance(demo: Demo) =
            FinanceClientInfoFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_PARAM1, demo)
                }
            }
    }
}