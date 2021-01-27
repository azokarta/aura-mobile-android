package kz.aura.merp.employee.fragment.dealer

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import kotlinx.android.synthetic.main.network_disconnected.*
import kotlinx.android.synthetic.main.network_disconnected.view.*
import kz.aura.merp.employee.R
import kz.aura.merp.employee.activity.SettingsActivity
import kz.aura.merp.employee.adapter.DemoAdapter
import kz.aura.merp.employee.data.SharedViewModel
import kz.aura.merp.employee.data.model.Demo
import kz.aura.merp.employee.data.viewmodel.DealerViewModel
import kz.aura.merp.employee.data.viewmodel.ReferenceViewModel
import kz.aura.merp.employee.databinding.FragmentDealerBinding
import kz.aura.merp.employee.util.Helpers
import kz.aura.merp.employee.util.Permissions

class DealerFragment : Fragment() {

    private val mDealerViewModel: DealerViewModel by viewModels()
    private val mSharedViewModel: SharedViewModel by viewModels()
    private val mReferenceViewModel: ReferenceViewModel by viewModels()
    private val adapter: DemoAdapter by lazy { DemoAdapter() }
    private lateinit var _binding: FragmentDealerBinding
    private val binding get() = _binding
    private var dealerId: Long? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Data binding
        _binding = FragmentDealerBinding.inflate(layoutInflater)
        binding.lifecycleOwner = this
        binding.mSharedViewModel = mSharedViewModel

        Permissions(requireContext(), requireActivity()).enableLocation()

        // Get dealer id
        dealerId = Helpers.getStaffId(requireContext())

        // Observe MutableLiveData
        mDealerViewModel.demoList.observe(viewLifecycleOwner, Observer { data ->
            mSharedViewModel.checkData(data)
            adapter.setData(data)
        })

        // Setup RecyclerView
        setupRecyclerview()

        // Observe errors
        mDealerViewModel.error.observe(viewLifecycleOwner, Observer { error ->
            checkError(error)
        })
        mReferenceViewModel.error.observe(viewLifecycleOwner, Observer { error ->
            checkError(error)
        })

        // If network is disconnected and user clicks restart, get data again
        binding.networkDisconnected.restart.setOnClickListener {
            if (Helpers.verifyAvailableNetwork(requireContext())) {
                mDealerViewModel.fetchAll(dealerId!!) // fetch demo list
                binding.progressBar.visibility = View.VISIBLE
                binding.recyclerView.visibility = View.VISIBLE
                binding.networkDisconnected.visibility = View.GONE
            }
        }

        // Fetch demoList
        mDealerViewModel.fetchAll(dealerId!!)

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        // Get changed Object from Storage
        val data = getData()
        if (data != null) {
            val list = mDealerViewModel.changeData(data)
            list?.let { adapter.setData(it) }
            removeData()
        }
    }

    private fun checkError(error: Any) {
        binding.progressBar.visibility = View.INVISIBLE // hide progress bar
        if (!Helpers.verifyAvailableNetwork(requireContext())) {
            binding.networkDisconnected.visibility = View.VISIBLE
            binding.recyclerView.visibility = View.INVISIBLE
        } else {
            Helpers.exceptionHandler(error, requireContext()) // Show error
        }
    }

//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        val menuInflater = menuInflater
//        menuInflater.inflate(R.menu.menu, menu)
//
//        return super.onCreateOptionsMenu(menu)
//    }

    private fun setupRecyclerview() {
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_settings -> toActivity(SettingsActivity())
        }

        return super.onOptionsItemSelected(item)
    }

    private fun toActivity(activity: Activity) {
        val intent = Intent(requireContext(), activity::class.java)
        startActivity(intent)
    }

    private fun getData(): Demo? {
        val pref = PreferenceManager.getDefaultSharedPreferences(requireContext())
        val demo = pref.getString("data", "")
        return if (demo != "") {
            val obj = Gson().fromJson<Demo>(demo, Demo::class.java)
            if (obj.demoId != 0L) obj else null
        } else {
            null
        }
    }

    private fun removeData() {
        val pref = PreferenceManager.getDefaultSharedPreferences(requireContext())
        val editor = pref.edit()
        editor.remove("data")
        editor.apply()
    }
}