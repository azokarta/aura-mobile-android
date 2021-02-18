package kz.aura.merp.employee.fragment.dealer

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import kz.aura.merp.employee.R
import kz.aura.merp.employee.adapter.PhoneNumberAdapter
import kz.aura.merp.employee.data.model.Demo
import kz.aura.merp.employee.data.viewmodel.DealerViewModel
import kz.aura.merp.employee.databinding.FragmentDemoDataBinding
import kz.aura.merp.employee.activity.MapActivity
import kz.aura.merp.employee.data.model.Location

private const val ARG_PARAM1 = "demo"

class DemoDataFragment : Fragment() {
    private var demo: Demo? = null
    private val demoDataPhoneNumberAdapter: PhoneNumberAdapter by lazy { PhoneNumberAdapter() }
    private var _binding: FragmentDemoDataBinding? = null
    private val binding get() = _binding!!
    private val mDealerViewModel: DealerViewModel by viewModels()

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
        _binding = FragmentDemoDataBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.demo = demo

        setHasOptionsMenu(true)

        // Setup RecyclerView
        setupRecyclerView()

        // Observe MutableLiveData
        mDealerViewModel.updatedDemo.observe(viewLifecycleOwner, Observer { data ->
            demo = data
            binding.demo = data
            binding.executePendingBindings()
            demo!!.crmPhoneDtoList?.let { demoDataPhoneNumberAdapter.setData(it) }
            Snackbar.make(binding.root, getString(R.string.successfullySaved), Snackbar.LENGTH_LONG).show()
        })

        binding.mapBtn.setOnClickListener {
            val intent = Intent(this.context, MapActivity::class.java)
            intent.putExtra("location", Location(43.224107, 76.877591, "adawdawdaw"))
            startActivity(intent)
        }

        // Set phone list
        demo!!.crmPhoneDtoList?.let { demoDataPhoneNumberAdapter.setData(it) }

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.save_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.save -> {
                demo!!.price = binding.demoDataTaxiExpences.text.toString().toDouble()
                mDealerViewModel.updateDemo(demo!!)
            }
        }
        return super.onOptionsItemSelected(item)
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
                    putParcelable(ARG_PARAM1, demo)
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}