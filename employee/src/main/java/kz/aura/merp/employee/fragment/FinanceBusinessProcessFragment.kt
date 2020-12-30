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
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import kz.aura.merp.employee.R
import kz.aura.merp.employee.data.model.*
import kz.aura.merp.employee.data.viewmodel.FinanceViewModel
import kz.aura.merp.employee.data.viewmodel.ReferenceViewModel
import kz.aura.merp.employee.databinding.FragmentFinanceBusinessProcessesBinding
import kz.aura.merp.employee.util.Helpers
import im.delight.android.location.SimpleLocation
import kotlinx.android.synthetic.main.fragment_finance_business_processes.*
import kz.aura.merp.employee.adapter.StepsAdapter

private const val bpId = 4
private const val ARG_PARAM1 = "plan"

class FinanceBusinessProcessFragment : Fragment(), StepsAdapter.Companion.CompletedStepListener {
    private var client: Client? = null
    var step = 0;
    private val results = arrayListOf<FinanceResult>()
    private var trackStepOrdersBusinessProcesses = arrayListOf<String>()
    private val stepsAdapter: StepsAdapter by lazy { StepsAdapter(this) }
    private lateinit var location: SimpleLocation
    private var _binding: FragmentFinanceBusinessProcessesBinding? = null
    private val binding get() = _binding!!
    private var collectorId: Long? = null
    private val mFinanceViewModel: FinanceViewModel by activityViewModels()
    private val mReferenceViewModel: ReferenceViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            client = it.getSerializable(ARG_PARAM1) as Client
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Data binding
        _binding = FragmentFinanceBusinessProcessesBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.client = client

        // Observe MutableLiveData
        setObserve()

        mReferenceViewModel.fetchMaCollectResults()
        mReferenceViewModel.fetchTrackStepOrdersBusinessProcesses(bpId)
        mFinanceViewModel.fetchTrackEmpProcessCollectMoney(client!!.maCollectMoneyId)

        location = SimpleLocation(this.requireContext())

        // Set collector id
        collectorId = Helpers.getStaffId(this.requireContext())

        initStepView()

        return binding.root
    }

    private fun setObserve() {
        mReferenceViewModel.maCollectResults.observe(viewLifecycleOwner, Observer { data ->
            binding.resultBtn.text = data[client!!.maCollectResultId].nameRu
            results.addAll(data)
        })
        mReferenceViewModel.trackStepOrdersBusinessProcesses.observe(viewLifecycleOwner, Observer { data ->
            trackStepOrdersBusinessProcesses.addAll(data.map { it.trackStepNameRu } as ArrayList)
            stepsAdapter.setData(trackStepOrdersBusinessProcesses)
        })
        mFinanceViewModel.updatedClient.observe(viewLifecycleOwner, Observer { data ->
            client = data
            binding.client = data
            binding.executePendingBindings() // Update view
            Snackbar.make(binding.businessProcessesSaveBtn, getString(R.string.successfullySaved), Snackbar.LENGTH_LONG).show()
        })
        mFinanceViewModel.trackEmpProcessCollectMoney.observe(viewLifecycleOwner, Observer { data ->
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

    private fun initStepView() {
        binding.stepsRecyclerView.layoutManager = LinearLayoutManager(this.requireContext())
        binding.stepsRecyclerView.adapter = stepsAdapter
        binding.stepsRecyclerView.isNestedScrollingEnabled = false
    }

    private fun showResultsAlertDialog() {
        val demoResultsByLang = results.map { it.nameRu } as ArrayList
        val builder = AlertDialog.Builder(this.requireContext())

        builder.setTitle("Результат демо")

        builder.setItems(demoResultsByLang.toArray(arrayOfNulls<String>(0))) { _, i ->
            client!!.maCollectResultId = i
            binding.resultBtn.text = results[i].nameRu
        }

        builder.setPositiveButton("Отмена") { dialog, _ ->
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
    }

    private fun initBtnListeners() {
        binding.resultBtn.setOnClickListener {
            showResultsAlertDialog()
        }

        binding.businessProcessesSaveBtn.setOnClickListener {
            client!!.description = fin_business_processes_cause.text.toString()
            mFinanceViewModel.updateClient(client!!)
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

    override fun stepCompleted(position: Int) {
        step+=1
        val longitude = location.longitude.toString()
        val latitude = location.latitude.toString()
        val completed = TrackEmpProcess(null, longitude, latitude, step, null, client!!.maCollectMoneyId)
        mFinanceViewModel.updateBusinessProcessStep(completed)
        mReferenceViewModel.createStaffLocation(StaffLocation(collectorId!!, maTbpId = bpId, longitude = longitude, latitude = latitude, maTrackStepId = step))
    }

    companion object {
        @JvmStatic
        fun newInstance(client: Client) =
            FinanceBusinessProcessFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_PARAM1, client)
                }
            }
    }
}