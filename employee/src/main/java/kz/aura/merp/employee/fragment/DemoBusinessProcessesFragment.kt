package kz.aura.merp.employee.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.google.gson.Gson
import kz.aura.merp.employee.R
import kz.aura.merp.employee.data.model.*
import kz.aura.merp.employee.data.viewmodel.DemoViewModel
import kz.aura.merp.employee.data.viewmodel.ReferenceViewModel
import kz.aura.merp.employee.databinding.FragmentDemoBusinessProcessesBinding
import kz.aura.merp.employee.util.Helpers
import im.delight.android.location.SimpleLocation
import kotlinx.android.synthetic.main.fragment_demo_business_processes.*
import kz.aura.merp.employee.activity.DemoActivity
import okhttp3.ResponseBody
import kotlin.collections.ArrayList


private const val bpId = 1
private const val ARG_PARAM1 = "demo"

class DemoBusinessProcessesFragment : Fragment() {

    var step = 0;
    private var demo: Demo? = null
    private val demoResults = arrayListOf<DemoResult>()
    private var trackStepOrdersBusinessProcesses = arrayListOf<TrackStepOrdersBusinessProcess>()
    private lateinit var location: SimpleLocation
    private var binding: FragmentDemoBusinessProcessesBinding? = null
    private var dealerId: Long? = null
    private val mDemoViewModel: DemoViewModel by activityViewModels()
    private val mReferenceViewModel: ReferenceViewModel by activityViewModels()

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
        binding = FragmentDemoBusinessProcessesBinding.inflate(inflater, container, false)
        binding!!.lifecycleOwner = this
        binding!!.demo = demo

        // Observe MutableLiveData
        setObserve()

        mReferenceViewModel.fetchDemoResults()
        mReferenceViewModel.fetchTrackStepOrdersBusinessProcesses(bpId)
        mDemoViewModel.fetchTrackEmpProcessDemo(demo!!.demoId)

        location = SimpleLocation(this.requireContext());

        // if we can't access the location yet
        if (!location.hasLocationEnabled()) {
            // ask the user to enable location access
            SimpleLocation.openSettings(this.requireContext());
        }

        // Set dealer id
        dealerId = Helpers.getStaffId(this.requireContext())

        return binding!!.root
    }

    private fun setObserve() {
        mReferenceViewModel.demoResults.observe(viewLifecycleOwner, Observer { data ->
            demo_result_btn.text = data[demo!!.resultId!!].nameRu
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
            binding!!.demo = data
            binding!!.executePendingBindings() // Update view
            (activity as DemoActivity).onBackPressed()
        })
        mDemoViewModel.trackEmpProcessDemo.observe(viewLifecycleOwner, Observer { data ->
            step = data.size
            initStepView(trackStepOrdersBusinessProcesses.map { it.trackStepNameRu } as ArrayList)
            showButtonsByStep()
        })
    }

    private fun stepIncrement(steps: ArrayList<TrackStepOrdersBusinessProcess>) {
        if (steps.size-1 >= step) {
            step += 1
            step_view.setStepsViewIndicatorComplectingPosition(step)
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

    private fun showDemoResultsAlertDialog() {
        val demoResultsByLang = demoResults.map { it.nameRu } as ArrayList
        val builder = AlertDialog.Builder(this.requireContext())

        builder.setTitle("Результат демо")

        builder.setItems(demoResultsByLang.toArray(arrayOfNulls<String>(0))) { _, i ->
            demo!!.resultId = i

            demo_result_btn.text = demoResults[i].nameRu
        }

        builder.setPositiveButton("Отмена") { dialog, _ ->
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
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

        demo_result_btn.setOnClickListener {
            showDemoResultsAlertDialog()
        }

        demo_business_processes_save_btn.setOnClickListener {
            demo!!.note = demo_business_processes_cause.text.toString()
            mDemoViewModel.updateDemo(demo!!)
        }
    }


    private fun showButtonsByStep() {
        when (step) {
            0 -> completed_btn_1.visibility = View.VISIBLE
            1 -> completed_btn_2.visibility = View.VISIBLE
            2 -> completed_btn_3.visibility = View.VISIBLE
            3 -> completed_btn_4.visibility = View.VISIBLE
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

    private fun completeBusinessProcess() {
        val longitude = location.longitude.toString()
        val latitude = location.latitude.toString()
        val completed = TrackEmpProcess(null, longitude, latitude, step+1, null, demo!!.demoId)
        mDemoViewModel.updateStepBusinessProcess(completed)
        stepIncrement(trackStepOrdersBusinessProcesses)
        mReferenceViewModel.createStaffLocation(StaffLocation(dealerId!!, maTbpId = bpId, longitude = longitude, latitude = latitude, maTrackStepId = step+1))
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