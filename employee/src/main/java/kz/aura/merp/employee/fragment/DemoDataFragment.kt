package kz.aura.merp.employee.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import kz.aura.merp.employee.R
import kz.aura.merp.employee.adapter.PhoneNumberAdapter
import kz.aura.merp.employee.data.model.Demo
import kz.aura.merp.employee.data.viewmodel.DemoViewModel
import kz.aura.merp.employee.databinding.FragmentDemoDataBinding
import kz.aura.merp.employee.activity.MapActivity
import kz.aura.merp.employee.adapter.StepsAdapter

private const val ARG_PARAM1 = "demo"

class DemoDataFragment : Fragment() {
    private var demo: Demo? = null
    private val demoDataPhoneNumberAdapter: PhoneNumberAdapter by lazy { PhoneNumberAdapter() }
    private var _binding: FragmentDemoDataBinding? = null
    private val binding get() = _binding!!
    private val mDemoViewModel: DemoViewModel by activityViewModels()

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
        _binding = FragmentDemoDataBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.demo = demo

        // Setup ReccyclerView
        setupRecyclerView()

        // Observe MutableLiveData
        mDemoViewModel.updatedDemo.observe(viewLifecycleOwner, Observer { data ->
            demo = data
            binding.demo = data
            binding.executePendingBindings()
            demoDataPhoneNumberAdapter.setData(demo!!.crmPhoneDtoList)
        })

        // Initialize Listeners
        binding.demoDataSaveBtn.setOnClickListener {
            demo!!.price = binding.demoDataTaxiExpences.text.toString().toDouble()
            mDemoViewModel.updateDemo(demo!!)
        }
        binding.mapBtn.setOnClickListener {
            val intent = Intent(this.context, MapActivity::class.java)
            startActivity(intent)
        }

        // Set phone list
        demoDataPhoneNumberAdapter.setData(demo!!.crmPhoneDtoList)

        return binding.root
    }

    private fun setupRecyclerView() {
        binding.demoDataPhoneNumbersRecyclerView.layoutManager = LinearLayoutManager(this.requireContext())
        binding.demoDataPhoneNumbersRecyclerView.adapter = demoDataPhoneNumberAdapter
        binding.demoDataPhoneNumbersRecyclerView.isNestedScrollingEnabled = false
    }

    companion object {
        @JvmStatic
        fun newInstance(demo: Demo) =
            DemoDataFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_PARAM1, demo)
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}