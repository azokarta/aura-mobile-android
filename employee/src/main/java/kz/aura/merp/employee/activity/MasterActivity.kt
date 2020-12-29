package kz.aura.merp.employee.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import kz.aura.merp.employee.R
import kz.aura.merp.employee.adapter.MasterAdapter
import kz.aura.merp.employee.data.SharedViewModel
import kz.aura.merp.employee.data.model.ServiceApplication
import kz.aura.merp.employee.data.viewmodel.MasterViewModel
import kz.aura.merp.employee.databinding.ActivityMasterBinding
import kz.aura.merp.employee.util.Helpers
import kz.aura.merp.employee.util.LanguageHelper
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_fin_agent.*
import kotlinx.android.synthetic.main.activity_master.*
import kotlinx.android.synthetic.main.activity_master.network_disconnected
import kotlinx.android.synthetic.main.activity_master.progress_bar
import kotlinx.android.synthetic.main.activity_master.recyclerView
import kotlinx.android.synthetic.main.activity_master.toolbar
import kotlinx.android.synthetic.main.network_disconnected.*
import kz.aura.merp.employee.data.viewmodel.ReferenceViewModel
import kz.aura.merp.employee.util.Permissions

class MasterActivity : AppCompatActivity() {

    private val mMasterViewModel: MasterViewModel by viewModels()
    private val mSharedViewModel: SharedViewModel by viewModels()
    private val mReferenceViewModel: ReferenceViewModel by viewModels()
    private val serviceApplicationAdapter: MasterAdapter by lazy { MasterAdapter() }
    private var masterId: Long? = null
    private lateinit var binding: ActivityMasterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LanguageHelper.updateLanguage(this)

        // Data binding
        binding = ActivityMasterBinding.inflate(layoutInflater)
        binding.lifecycleOwner = this
        binding.mSharedViewModel = mSharedViewModel
        val view = binding.root
        setContentView(view)

        setSupportActionBar(toolbar as Toolbar)
        supportActionBar?.title = getString(R.string.master)

        Permissions(this, this).requestGpsPermission()

        // Get master id
        masterId = Helpers.getStaffId(this)

        // Setup RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(applicationContext)
        recyclerView.adapter = serviceApplicationAdapter

        // Observe MutableLiveData
        mMasterViewModel.applications.observe(this, Observer { data ->
            mSharedViewModel.checkData(data)
            serviceApplicationAdapter.setData(data)
        })

        // Observe errors
        mMasterViewModel.error.observe(this, Observer { error ->
            checkError(error)
        })
        mReferenceViewModel.error.observe(this, Observer { error ->
            checkError(error)
        })

        // If network is disconnected and user clicks restart, get data again
        restart.setOnClickListener {
            if (Helpers.verifyAvailableNetwork(this)) {
                mMasterViewModel.fetchServiceApplications(masterId!!) // fetch serviceApplications
                progress_bar.visibility = View.VISIBLE
                recyclerView.visibility = View.VISIBLE
                network_disconnected.visibility = View.GONE
            }
        }

        // Fetch serviceApplications
        mMasterViewModel.fetchServiceApplications(masterId!!)
    }

    override fun onResume() {
        super.onResume()
        val data = getData()
        if (data != null) {
            val list = mMasterViewModel.changeData(data)
            serviceApplicationAdapter.setData(list)
            removeData()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_settings -> {
                val intent = Intent(applicationContext, SettingsActivity::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun checkError(error: Any) {
        progress_bar.visibility = View.INVISIBLE // hide progress bar
        if (!Helpers.verifyAvailableNetwork(this)) {
            network_disconnected.visibility = View.VISIBLE
            recyclerView.visibility = View.INVISIBLE
        } else {
            Helpers.exceptionHandler(error, this) // Show error
        }
    }

    private fun getData(): ServiceApplication? {
        val pref = PreferenceManager.getDefaultSharedPreferences(this)
        val serviceApplication = pref.getString("data", "")
        return if (serviceApplication != "") {
            val obj = Gson().fromJson<ServiceApplication>(serviceApplication, ServiceApplication::class.java)
            if (obj.id != 0L) obj else null
        } else {
            null
        }
    }

    private fun removeData() {
        val pref = PreferenceManager.getDefaultSharedPreferences(this)
        val editor = pref.edit()
        editor.remove("data")
        editor.apply()
    }
}