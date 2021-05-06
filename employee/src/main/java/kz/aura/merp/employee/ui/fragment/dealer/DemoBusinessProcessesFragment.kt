package kz.aura.merp.employee.ui.fragment.dealer

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import io.nlopez.smartlocation.SmartLocation
import kz.aura.merp.employee.R
import kz.aura.merp.employee.adapter.StepsAdapter
import kz.aura.merp.employee.model.*
import kz.aura.merp.employee.viewmodel.DealerViewModel
import kz.aura.merp.employee.viewmodel.ReferenceViewModel
import kz.aura.merp.employee.databinding.FragmentDemoBusinessProcessesBinding
import kz.aura.merp.employee.util.saveData
import kz.aura.merp.employee.util.ProgressDialog


private const val bpId = 1
private const val ARG_PARAM1 = "demo"
private const val soldId = 4

class DemoBusinessProcessesFragment : Fragment(), StepsAdapter.Companion.CompletedStepListener {

    var step = 0
    private var demo: Demo? = null
    private val demoResults = arrayListOf<DemoResult>()
    private var trackStepOrdersBusinessProcesses = arrayListOf<String>()
    private val stepsAdapter: StepsAdapter by lazy { StepsAdapter(this) }
    private lateinit var location: Location
    private var _binding: FragmentDemoBusinessProcessesBinding? = null
    private val binding get() = _binding!!
    private var dealerId: Long? = null
    private val mDealerViewModel: DealerViewModel by viewModels()
    private val mReferenceViewModel: ReferenceViewModel by viewModels()
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            demo = it.getParcelable(ARG_PARAM1) as Demo?
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

        setHasOptionsMenu(true)

        // Set dealer id
//        dealerId = getStaffId(this.requireContext())

        // Observe MutableLiveData
        setObserve()

        mReferenceViewModel.fetchDemoResults()
//        mReferenceViewModel.fetchTrackStepOrdersBusinessProcesses(bpId)
        mReferenceViewModel.fetchContractTypes(dealerId!!)
        mDealerViewModel.fetchTrackEmpProcessDemo(demo!!.demoId)

        // Getting a location
        SmartLocation.with(requireContext()).location()
            .start {
                println("Latitude: ${it.latitude}, longitude: ${it.longitude}")
                location = Location(it.latitude, it.longitude)
            };


        // PhoneNumber Formatter
        binding.ccp.registerCarrierNumberEditText(binding.phoneNumber)

        // Initialize Loading Dialog
        progressDialog = ProgressDialog(this.requireContext())

        checkDemoStatusIsSold()

        initStepView()

        return binding.root
    }

    private fun setObserve() {
        // Observe demo results
        mReferenceViewModel.demoResults.observe(viewLifecycleOwner, { data ->
            binding.demoResultBtn.text = data[demo!!.resultId!!].nameRu
            demoResults.addAll(data)
        })

        // Observe titles of steps
        mReferenceViewModel.trackStepOrdersBusinessProcesses.observe(
            viewLifecycleOwner,
            { data ->
//                trackStepOrdersBusinessProcesses.addAll(data.map { it.trackStepNameRu } as ArrayList)
//                stepsAdapter.setData(trackStepOrdersBusinessProcesses)
            })

        // Observe updated demo
        mDealerViewModel.updatedDemo.observe(viewLifecycleOwner, { data ->
            demo = data
            binding.demo = data
            binding.executePendingBindings() // Update view
            checkDemoStatusIsSold()
            binding.demoResultBtn.text = demoResults[data!!.resultId!!].nameRu
            progressDialog.hideLoading()
        })

        mDealerViewModel.trackEmpProcessDemo.observe(viewLifecycleOwner, { data ->
            step = data.size
            initBtnListeners()
            initStepView()
//            stepsAdapter.setStep(step)
        })

        // Observe if sms has been sent
        mDealerViewModel.smsSent.observe(viewLifecycleOwner, {
            demo!!.ocrDemoStatus = "SENT_SMS"
            mDealerViewModel.updatedDemo.postValue(demo!!)
            checkDemoStatusIsSold()
            saveData(demo!!, this.requireContext())
            progressDialog.hideLoading()
        })

        // Observe error
        mDealerViewModel.error.observe(viewLifecycleOwner, {
            progressDialog.hideLoading()
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.save_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.save -> {
                demo!!.note = binding.demoBusinessProcessesCause.text.toString()
                mDealerViewModel.updateDemo(demo!!)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initStepView() {
        binding.stepsRecyclerView.layoutManager = LinearLayoutManager(this.requireContext())
        binding.stepsRecyclerView.adapter = stepsAdapter
        binding.stepsRecyclerView.isNestedScrollingEnabled = false
    }

    private fun showContractTypesDialog(contractTypes: ArrayList<ContractType>) {
        val builder = AlertDialog.Builder(this.requireContext())

        builder.setTitle("Тип контракта")

        builder.setItems((contractTypes.map { it.name } as ArrayList).toArray(arrayOfNulls<String>(0))) { _, i ->
            binding.contractTypeBtn.text = contractTypes[i].name
        }

        builder.setPositiveButton("Отмена") { dialog, _ ->
            dialog.dismiss()
        }

        builder.create().show()
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
                    binding.phoneNumber.visibility = View.VISIBLE
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
        binding.phoneNumber.visibility = View.GONE
        binding.ccp.visibility = View.GONE
        binding.sendSms.visibility = View.GONE
    }

    private fun showDemoResultsAlertDialog() {
        val demoResultsByLang = demoResults.map { it.nameRu } as ArrayList
        val builder = AlertDialog.Builder(this.requireContext())

        builder.setTitle("Результат демо")

        builder.setItems(demoResultsByLang.toArray(arrayOfNulls<String>(0))) { _, i ->
            demo!!.resultId = i
//            contractTypeBtn.visibility = View.VISIBLE
            binding.demoResultBtn.text = demoResults[i].nameRu
            checkDemoStatusIsSold()
        }

        builder.setPositiveButton("Отмена") { dialog, _ ->
            dialog.dismiss()
        }

        builder.create().show()
    }

    private fun initBtnListeners() {
        binding.demoResultBtn.setOnClickListener {
            showDemoResultsAlertDialog()
        }

        binding.sendSms.setOnClickListener {
            if (binding.ccp.isValidFullNumber) {
                mDealerViewModel.sendSms(
                    demo!!.demoId, binding.ccp.selectedCountryCode, binding.ccp.fullNumberWithPlus.replace(
                        binding.ccp.selectedCountryCodeWithPlus,
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

        binding.contractTypeBtn.setOnClickListener {
            mReferenceViewModel.contractTypes.value?.let { it1 -> showContractTypesDialog(it1) }
        }
    }

    override fun onDestroy() {
        SmartLocation.with(requireContext()).location().stop()
        super.onDestroy()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



    companion object {
        @JvmStatic
        fun newInstance(demo: Demo) =
            DemoBusinessProcessesFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_PARAM1, demo)
                }
            }
    }

    override fun stepCompleted(businessProcessStatus: BusinessProcessStatus) {
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
}