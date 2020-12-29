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
import kz.aura.merp.employee.data.viewmodel.FinanceViewModel
import kz.aura.merp.employee.data.viewmodel.ReferenceViewModel
import kz.aura.merp.employee.databinding.FragmentFinanceBusinessProcessesBinding
import kz.aura.merp.employee.util.Helpers
import im.delight.android.location.SimpleLocation
import kotlinx.android.synthetic.main.fragment_finance_business_processes.*

private const val bpId = 4
private const val ARG_PARAM1 = "plan"

class FinanceBusinessProcessFragment : Fragment() {
    private var client: Client? = null
    var step = 0;
    private val results = arrayListOf<FinanceResult>()
    private var trackStepOrdersBusinessProcesses = arrayListOf<TrackStepOrdersBusinessProcess>()
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

        location = SimpleLocation(this.requireContext());

        // if we can't access the location yet
        if (!location.hasLocationEnabled()) {
            // ask the user to enable location access
            SimpleLocation.openSettings(this.requireContext());
        }

        // Set collector id
        collectorId = Helpers.getStaffId(this.requireContext())

        return binding.root
    }

    private fun setObserve() {
        mReferenceViewModel.maCollectResults.observe(viewLifecycleOwner, Observer { data ->
            binding.resultBtn.text = data[client!!.maCollectResultId].nameRu
            results.addAll(data)
        })
        mReferenceViewModel.trackStepOrdersBusinessProcesses.observe(viewLifecycleOwner, Observer { data ->
            trackStepOrdersBusinessProcesses.addAll(data)
            initBtnListeners()
            initStepView(trackStepOrdersBusinessProcesses.map { it.trackStepNameRu } as ArrayList)
        })
        mFinanceViewModel.updatedClient.observe(viewLifecycleOwner, Observer { data ->
            client = data
            binding.client = data
            binding.executePendingBindings() // Update view
            Snackbar.make(binding.businessProcessesSaveBtn, getString(R.string.successfullySaved), Snackbar.LENGTH_LONG).show()
        })
        mFinanceViewModel.trackEmpProcessCollectMoney.observe(viewLifecycleOwner, Observer { data ->
            step = data.size
            initStepView(trackStepOrdersBusinessProcesses.map { it.trackStepNameRu } as ArrayList)
            showButtonsByStep()
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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

    private fun stepIncrement(steps: ArrayList<TrackStepOrdersBusinessProcess>) {
        if (steps.size-1 >= step) {
            step += 1
            stepView.setStepsViewIndicatorComplectingPosition(step)
        }
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
        }

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

    private fun completeBusinessProcess() {
        val longitude = location.longitude.toString()
        val latitude = location.latitude.toString()
        val completed = TrackEmpProcess(null, longitude, latitude, step+1, null, client!!.maCollectMoneyId)
        mFinanceViewModel.updateBusinessProcessStep(completed)
        mReferenceViewModel.createStaffLocation(StaffLocation(collectorId!!, maTbpId = bpId, longitude = longitude, latitude = latitude, maTrackStepId = step+1))
        stepIncrement(trackStepOrdersBusinessProcesses)
    }

    private fun showButtonsByStep() {
        when (step) {
            0 -> binding.completedBtn1.visibility = View.VISIBLE
            1 -> binding.completedBtn2.visibility = View.VISIBLE
            2 -> binding.completedBtn3.visibility = View.VISIBLE
        }
    }

    override fun onPause() {
        // stop location updates (saves battery)
        location.endUpdates()

        // ...
        super.onPause()
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