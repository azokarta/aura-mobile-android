package kz.aura.merp.employee.ui.fragment.master

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import kz.aura.merp.employee.R
import kz.aura.merp.employee.model.*
import kz.aura.merp.employee.viewmodel.MasterViewModel
import kz.aura.merp.employee.viewmodel.ReferenceViewModel
import kz.aura.merp.employee.databinding.FragmentServiceApplicationBusinessProcessesBinding
import io.nlopez.smartlocation.SmartLocation
import kz.aura.merp.employee.adapter.StepsAdapter

private const val ARG_PARAM1 = "serviceApplication"
private const val bpId = 3

class ServiceApplicationBusinessFragment : Fragment(), StepsAdapter.Companion.CompletedStepListener {

    private var serviceApplication: ServiceApplication? = null
    var step = 0;
    private val results = arrayListOf<ServiceApplicationStatus>()
    private var trackStepOrdersBusinessProcesses = arrayListOf<String>()
    private val stepsAdapter: StepsAdapter by lazy { StepsAdapter(this) }
    private lateinit var location: Location
    private var _binding: FragmentServiceApplicationBusinessProcessesBinding? = null
    private val binding get() = _binding!!
    private var masterId: Long? = null
    private val mMasterViewModel: MasterViewModel by viewModels()
    private val mReferenceViewModel: ReferenceViewModel by viewModels()

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
        _binding = FragmentServiceApplicationBusinessProcessesBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.serviceApplication = serviceApplication

        setHasOptionsMenu(true)

        // Observe MutableLiveData
        setObserve()

        mReferenceViewModel.fetchServiceApplicationStatus()
//        mReferenceViewModel.fetchTrackStepOrdersBusinessProcesses(bpId)
//        mMasterViewModel.fetchTrackEmpProcessServiceApplication(serviceApplication!!)

        // Getting a location
        SmartLocation.with(requireContext()).location()
            .start {
                location = Location(it.latitude, it.longitude)
            };

        // Set master id
//        masterId = getStaffId(this.requireContext())

        initStepView()

        return binding.root
    }

    private fun setObserve() {
//        mReferenceViewModel.serviceApplicationStatus.observe(viewLifecycleOwner, Observer { data ->
////            binding.resultBtn.text = data[serviceApplication!!.appStatus].nameRu
//            results.addAll(data)
//        })
//        mReferenceViewModel.trackStepOrdersBusinessProcesses.observe(viewLifecycleOwner, Observer { data ->
////            trackStepOrdersBusinessProcesses.addAll(data.map { it.trackStepNameRu } as ArrayList)
////            stepsAdapter.setData(trackStepOrdersBusinessProcesses)
//        })
//        mMasterViewModel.updatedServiceApplication.observe(viewLifecycleOwner, Observer { data ->
//            serviceApplication = data
//            binding.serviceApplication = data
//            binding.executePendingBindings() // Update view
//            Snackbar.make(binding.root, getString(R.string.successfullySaved), Snackbar.LENGTH_LONG).show()
//        })
//        mMasterViewModel.trackEmpProcessServiceApplication.observe(viewLifecycleOwner, Observer { data ->
//            step = data.size
//            initBtnListeners()
//            initStepView()
////            stepsAdapter.setStep(step)
//        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.save_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.save -> {
//                serviceApplication!!.description = binding.serviceBusinessProcessesCause.text.toString()
                mMasterViewModel.updateServiceApplication(serviceApplication!!)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showResultsAlertDialog() {
        val demoResultsByLang = results.map { it.nameRu } as ArrayList
        val builder = AlertDialog.Builder(this.requireContext())

        builder.setTitle("Результат")

        builder.setItems(demoResultsByLang.toArray(arrayOfNulls<String>(0))) { _, i ->
//            serviceApplication!!.appStatus = i
            binding.resultBtn.text = results[i].nameRu
        }

        builder.setPositiveButton("Отмена") { dialog, _ ->
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
    }

    override fun onDestroy() {
        SmartLocation.with(requireContext()).location().stop()
        super.onDestroy()
    }

    private fun initBtnListeners() {
        binding.resultBtn.setOnClickListener {
            showResultsAlertDialog()
        }
    }

    private fun initStepView() {
        binding.stepsRecyclerView.layoutManager = LinearLayoutManager(this.requireContext())
        binding.stepsRecyclerView.adapter = stepsAdapter
        binding.stepsRecyclerView.isNestedScrollingEnabled = false
    }

    override fun stepCompleted(businessProcessStatus: BusinessProcessStatus) {
        step+=1
        val longitude = location.longitude.toString()
        val latitude = location.latitude.toString()
//        val completed = TrackEmpProcess(null, longitude, latitude, step, null, serviceApplication!!.id)
//        mMasterViewModel.updateStepBusinessProcess(completed)
//        mReferenceViewModel.createStaffLocation(StaffLocation(masterId!!, maTbpId = bpId, longitude = longitude, latitude = latitude, maTrackStepId = step))
    }

    companion object {
        @JvmStatic
        fun newInstance(serviceApplication: ServiceApplication) =
            ServiceApplicationBusinessFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_PARAM1, serviceApplication)
                }
            }
    }
}