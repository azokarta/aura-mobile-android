package kz.aura.merp.employee.fragment.finance

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import kotlinx.android.synthetic.main.network_disconnected.*
import kz.aura.merp.employee.R
import kz.aura.merp.employee.activity.SettingsActivity
import kz.aura.merp.employee.adapter.ClientAdapter
import kz.aura.merp.employee.data.SharedViewModel
import kz.aura.merp.employee.data.model.Client
import kz.aura.merp.employee.data.viewmodel.FinanceViewModel
import kz.aura.merp.employee.data.viewmodel.ReferenceViewModel
import kz.aura.merp.employee.databinding.FragmentFinanceBinding
import kz.aura.merp.employee.util.Helpers
import kz.aura.merp.employee.util.Permissions

class FinanceFragment : Fragment() {
    private val mFinanceViewModel: FinanceViewModel by activityViewModels()
    private val mSharedViewModel: SharedViewModel by activityViewModels()
    private val mReferenceViewModel: ReferenceViewModel by activityViewModels()
    private val finAdapter: ClientAdapter by lazy { ClientAdapter() }
    private var collectorId: Long? = null
    private lateinit var _binding: FragmentFinanceBinding
    private val binding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Data binding
        _binding = FragmentFinanceBinding.inflate(layoutInflater)
        binding.lifecycleOwner = this
        binding.mSharedViewModel = mSharedViewModel

        // Get collector id
        collectorId = Helpers.getStaffId(requireContext())

        // Setup RecyclerView
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = finAdapter

        // Observe MutableLiveData
        mFinanceViewModel.clients.observe(viewLifecycleOwner, Observer { data ->
            mSharedViewModel.checkData(data)
            finAdapter.setData(data)
        })

        Permissions(requireContext(), requireActivity()).enableLocation()

        // Observe errors
        mFinanceViewModel.error.observe(viewLifecycleOwner, Observer { error ->
            checkError(error)
        })
        mReferenceViewModel.error.observe(viewLifecycleOwner, Observer { error ->
            checkError(error)
        })

        // If network is disconnected and user clicks restart, get data again
        restart.setOnClickListener {
            if (Helpers.verifyAvailableNetwork(requireContext())) {
                mFinanceViewModel.fetchClients(collectorId!!) // fetch clients
                binding.progressBar.visibility = View.VISIBLE
                binding.recyclerView.visibility = View.VISIBLE
                binding.networkDisconnected.visibility = View.GONE
            }
        }

        // Fetch clients
        mFinanceViewModel.fetchClients(collectorId!!)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        val data = getData()
        if (data != null) {
            val list = mFinanceViewModel.changeData(data)
            list?.let { finAdapter.setData(it) }
            removeData()
        }
    }

//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        val menuInflater = menuInflater
//        menuInflater.inflate(R.menu.menu, menu)
//
//        return super.onCreateOptionsMenu(menu)
//    }

//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        when (item.itemId) {
//            R.id.menu_settings -> {
//                val intent = Intent(requireContext(), SettingsActivity::class.java)
//                startActivity(intent)
//            }
//        }
//
//        return super.onOptionsItemSelected(item)
//    }

    private fun checkError(error: Any) {
        binding.progressBar.visibility = View.INVISIBLE // hide progress bar
        if (!Helpers.verifyAvailableNetwork(requireContext())) {
            binding.networkDisconnected.visibility = View.VISIBLE
            binding.recyclerView.visibility = View.INVISIBLE
        } else {
            Helpers.exceptionHandler(error, requireContext()) // Show error
        }
    }

    private fun getData(): Client? {
        val pref = PreferenceManager.getDefaultSharedPreferences(requireContext())
        val client = pref.getString("data", "")
        return if (client != "") {
            val obj = Gson().fromJson<Client>(client, Client::class.java)
            if (obj.maCollectMoneyId != 0L) obj else null
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