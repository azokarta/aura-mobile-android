package kz.aura.merp.employee.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import kz.aura.merp.employee.adapter.PhoneNumberAdapter
import kz.aura.merp.employee.data.model.ContractType
import kz.aura.merp.employee.data.model.Demo
import kz.aura.merp.employee.data.model.DemoModify
import kz.aura.merp.employee.data.model.PriceList
import kz.aura.merp.employee.data.viewmodel.DemoViewModel
import kz.aura.merp.employee.databinding.FragmentDemoDataBinding
import kz.aura.merp.employee.util.Helpers.exceptionHandler
import kotlinx.android.synthetic.main.fragment_demo_data.*
import kotlinx.android.synthetic.main.fragment_demo_data.view.*

private const val ARG_PARAM1 = "demo"

class DemoDataFragment : Fragment() {
    private var demo: Demo? = null
    private val demoDataPhoneNumberAdapter: PhoneNumberAdapter by lazy { PhoneNumberAdapter() }
    private var binding: FragmentDemoDataBinding? = null
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
        binding = FragmentDemoDataBinding.inflate(inflater, container, false)
        binding!!.lifecycleOwner = this
        binding!!.demo = demo

        // Setup ReccyclerView
        setupRecyclerView(binding!!.root)

        // Observe MutableLiveData
        mDemoViewModel.updatedDemo.observe(viewLifecycleOwner, Observer { data ->
            demo = data
            binding!!.demo = data
            binding!!.executePendingBindings()
            demoDataPhoneNumberAdapter.setData(demo!!.crmPhoneDtoList)
        })
        mDemoViewModel.error.observe(viewLifecycleOwner, Observer { error ->
            exceptionHandler(error, this.requireContext())
        })


        // Initialize Listeners
        binding!!.root.demo_data_save_btn.setOnClickListener {
            demo!!.price = demo_data_taxi_expences.text.toString().toDouble()
            mDemoViewModel.updateDemo(DemoModify(demo!!, ContractType(), PriceList()))
        }

        demoDataPhoneNumberAdapter.setData(demo!!.crmPhoneDtoList)

        return binding!!.root
    }

    private fun setupRecyclerView(view: View) {
        view.demo_data_phone_numbers_recycler_view.layoutManager = LinearLayoutManager(this.requireContext())
        view.demo_data_phone_numbers_recycler_view.adapter = demoDataPhoneNumberAdapter
        view.demo_data_phone_numbers_recycler_view.isNestedScrollingEnabled = false
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
}