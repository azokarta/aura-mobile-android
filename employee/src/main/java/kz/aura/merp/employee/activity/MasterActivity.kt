package kz.aura.merp.employee.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Observer
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import kz.aura.merp.employee.R
import kz.aura.merp.employee.adapter.ServiceApplicationAdapter
import kz.aura.merp.employee.data.SharedViewModel
import kz.aura.merp.employee.data.model.ServiceApplication
import kz.aura.merp.employee.data.viewmodel.MasterViewModel
import kz.aura.merp.employee.databinding.ActivityMasterBinding
import kz.aura.merp.employee.util.Helpers
import kz.aura.merp.employee.util.LanguageHelper
import com.google.gson.Gson
import kz.aura.merp.employee.data.viewmodel.ReferenceViewModel
import kz.aura.merp.employee.util.Permissions

class MasterActivity : AppCompatActivity() {

    private val mMasterViewModel: MasterViewModel by viewModels()
    private val mSharedViewModel: SharedViewModel by viewModels()
    private val mReferenceViewModel: ReferenceViewModel by viewModels()
    private val serviceApplicationAdapter: ServiceApplicationAdapter by lazy { ServiceApplicationAdapter() }
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

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = getString(R.string.master)
        supportActionBar?.subtitle = Helpers.getStaff(this)?.username

        Permissions(this, this).enableLocation()

        // Get master id
        masterId = Helpers.getStaffId(this)

        // Setup RecyclerView
        binding.recyclerView.layoutManager = LinearLayoutManager(applicationContext)
        binding.recyclerView.adapter = serviceApplicationAdapter

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
        findViewById<Button>(R.id.restart).setOnClickListener {
            if (Helpers.verifyAvailableNetwork(this)) {
                mMasterViewModel.fetchServiceApplications(masterId!!) // fetch serviceApplications
                binding.progressBar.visibility = View.VISIBLE
                binding.recyclerView.visibility = View.VISIBLE
                findViewById<ConstraintLayout>(R.id.networkDisconnected).visibility = View.GONE
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
            list?.let { serviceApplicationAdapter.setData(it) }
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
        binding.progressBar.visibility = View.INVISIBLE // hide progress bar
        if (!Helpers.verifyAvailableNetwork(this)) {
            findViewById<ConstraintLayout>(R.id.networkDisconnected).visibility = View.VISIBLE
            binding.recyclerView.visibility = View.INVISIBLE
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