package kz.aura.merp.employee.fragment.finance

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import kz.aura.merp.employee.R
import kz.aura.merp.employee.data.model.*
import kz.aura.merp.employee.data.viewmodel.FinanceViewModel
import kz.aura.merp.employee.data.viewmodel.ReferenceViewModel
import kz.aura.merp.employee.databinding.FragmentFinanceBusinessProcessesBinding
import kz.aura.merp.employee.util.Helpers
import io.nlopez.smartlocation.SmartLocation
import kz.aura.merp.employee.adapter.StepsAdapter

private const val bpId = 4
private const val ARG_PARAM1 = "plan"

class FinanceBusinessProcessFragment : Fragment(), StepsAdapter.Companion.CompletedStepListener {
    private var client: Client? = null
    var step = 0;
    private val results = arrayListOf<FinanceResult>()
    private var trackStepOrdersBusinessProcesses = arrayListOf<String>()
    private val stepsAdapter: StepsAdapter by lazy { StepsAdapter(this) }
    private lateinit var location: Location
    private var _binding: FragmentFinanceBusinessProcessesBinding? = null
    private val binding get() = _binding!!
    private var collectorId: Long? = null
    private val mFinanceViewModel: FinanceViewModel by viewModels()
    private val mReferenceViewModel: ReferenceViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            client = it.getParcelable(ARG_PARAM1) as Client?
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

        setHasOptionsMenu(true)

        // Observe MutableLiveData
        setObserve()

        mReferenceViewModel.fetchMaCollectResults()
        mReferenceViewModel.fetchTrackStepOrdersBusinessProcesses(bpId)
        mFinanceViewModel.fetchTrackEmpProcessCollectMoney(client!!.maCollectMoneyId)

        // Getting a location
        SmartLocation.with(requireContext()).location()
            .start {
                println("Latitude: ${it.latitude}, longitude: ${it.longitude}")
                location = Location(it.latitude, it.longitude)
            };

        // Set collector id
        collectorId = Helpers.getStaffId(this.requireContext())

        initStepView()

        return binding.root
    }

    private fun setObserve() {
        mReferenceViewModel.maCollectResults.observe(viewLifecycleOwner, Observer { data ->
            binding.resultBtn.text = data[client!!.maCollectResultId!!].nameRu
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
            Snackbar.make(binding.root, getString(R.string.successfullySaved), Snackbar.LENGTH_LONG).show()
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.save_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.save -> {
                client!!.description = binding.finBusinessProcessesCause.text.toString()
                mFinanceViewModel.updateClient(client!!)
            }
        }
        return super.onOptionsItemSelected(item)
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
    }

    override fun onDestroy() {
        SmartLocation.with(requireContext()).location().stop()
        super.onDestroy()
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
                    putParcelable(ARG_PARAM1, client)
                }
            }
    }
}