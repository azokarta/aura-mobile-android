package kz.aura.merp.employee.fragment.master

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
//import kotlinx.android.synthetic.main.activity_master.*
import kotlinx.android.synthetic.main.network_disconnected.*
import kotlinx.android.synthetic.main.network_disconnected.view.*
import kz.aura.merp.employee.R
import kz.aura.merp.employee.activity.SettingsActivity
import kz.aura.merp.employee.adapter.ServiceApplicationAdapter
import kz.aura.merp.employee.data.SharedViewModel
import kz.aura.merp.employee.data.model.ServiceApplication
import kz.aura.merp.employee.data.viewmodel.MasterViewModel
import kz.aura.merp.employee.data.viewmodel.ReferenceViewModel
import kz.aura.merp.employee.databinding.FragmentMasterBinding
//import kz.aura.merp.employee.databinding.ActivityMasterBinding
import kz.aura.merp.employee.util.Helpers
import kz.aura.merp.employee.util.Permissions

class MasterFragment : Fragment() {

    private val mMasterViewModel: MasterViewModel by viewModels()
    private val mSharedViewModel: SharedViewModel by viewModels()
    private val mReferenceViewModel: ReferenceViewModel by viewModels()
    private val serviceApplicationAdapter: ServiceApplicationAdapter by lazy { ServiceApplicationAdapter() }
    private var masterId: Long? = null
    private lateinit var binding: FragmentMasterBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Data binding
        binding = FragmentMasterBinding.inflate(layoutInflater)
        binding.lifecycleOwner = this
        binding.mSharedViewModel = mSharedViewModel

        Permissions(requireContext(), requireActivity()).enableLocation()

        // Get master id
        masterId = Helpers.getStaffId(requireContext())

        // Setup RecyclerView
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = serviceApplicationAdapter

        // Observe MutableLiveData
        mMasterViewModel.applications.observe(viewLifecycleOwner, Observer { data ->
            mSharedViewModel.checkData(data)
            serviceApplicationAdapter.setData(data)
        })

        // Observe errors
        mMasterViewModel.error.observe(viewLifecycleOwner, Observer { error ->
            checkError(error)
        })
        mReferenceViewModel.error.observe(viewLifecycleOwner, Observer { error ->
            checkError(error)
        })

        // If network is disconnected and user clicks restart, get data again
        binding.networkDisconnected.restart.setOnClickListener {
            if (Helpers.verifyAvailableNetwork(requireContext())) {
                mMasterViewModel.fetchServiceApplications(masterId!!) // fetch serviceApplications
                binding.progressBar.visibility = View.VISIBLE
                binding.recyclerView.visibility = View.VISIBLE
                binding.networkDisconnected.visibility = View.GONE
            }
        }

        // Fetch serviceApplications
        mMasterViewModel.fetchServiceApplications(masterId!!)

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        val data = getData()
        if (data != null) {
            val list = mMasterViewModel.changeData(data)
            list?.let { serviceApplicationAdapter.setData(it) }
            removeData()
        }
    }

//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        val menuInflater = menuInflater
//        menuInflater.inflate(R.menu.menu, menu)
//        return super.onCreateOptionsMenu(menu)
//    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_settings -> {
                val intent = Intent(requireContext(), SettingsActivity::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
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

    private fun getData(): ServiceApplication? {
        val pref = PreferenceManager.getDefaultSharedPreferences(requireContext())
        val serviceApplication = pref.getString("data", "")
        return if (serviceApplication != "") {
            val obj = Gson().fromJson<ServiceApplication>(serviceApplication, ServiceApplication::class.java)
            if (obj.id != 0L) obj else null
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