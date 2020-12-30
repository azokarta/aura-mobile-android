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
import kz.aura.merp.employee.adapter.FinanceAdapter
import kz.aura.merp.employee.data.SharedViewModel
import kz.aura.merp.employee.data.model.Client
import kz.aura.merp.employee.data.viewmodel.FinanceViewModel
import kz.aura.merp.employee.databinding.ActivityFinAgentBinding
import kz.aura.merp.employee.util.Helpers
import kz.aura.merp.employee.util.LanguageHelper
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_dealer.*
import kotlinx.android.synthetic.main.activity_fin_agent.*
import kotlinx.android.synthetic.main.activity_fin_agent.network_disconnected
import kotlinx.android.synthetic.main.activity_fin_agent.progress_bar
import kotlinx.android.synthetic.main.activity_fin_agent.recyclerView
import kotlinx.android.synthetic.main.activity_fin_agent.toolbar
import kotlinx.android.synthetic.main.network_disconnected.*
import kz.aura.merp.employee.data.viewmodel.ReferenceViewModel
import kz.aura.merp.employee.util.Helpers.exceptionHandler
import kz.aura.merp.employee.util.Helpers.verifyAvailableNetwork
import kz.aura.merp.employee.util.Permissions

class FinanceAgentActivity : AppCompatActivity() {

    private val mFinanceViewModel: FinanceViewModel by viewModels()
    private val mSharedViewModel: SharedViewModel by viewModels()
    private val mReferenceViewModel: ReferenceViewModel by viewModels()
    private val finAdapter: FinanceAdapter by lazy { FinanceAdapter() }
    private var collectorId: Long? = null
    private lateinit var binding: ActivityFinAgentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LanguageHelper.updateLanguage(this)

        // Data binding
        binding = ActivityFinAgentBinding.inflate(layoutInflater)
        binding.lifecycleOwner = this
        binding.mSharedViewModel = mSharedViewModel
        val view = binding.root
        setContentView(view)

        setSupportActionBar(toolbar as Toolbar)
        supportActionBar?.title = getString(R.string.finAgent)

        // Get collector id
        collectorId = Helpers.getStaffId(this)

        // Setup RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(applicationContext)
        recyclerView.adapter = finAdapter

        // Observe MutableLiveData
        mFinanceViewModel.clients.observe(this, Observer { data ->
            mSharedViewModel.checkData(data)
            finAdapter.setData(data)
        })

        Permissions(this, this).enableLocation()

        // Observe errors
        mFinanceViewModel.error.observe(this, Observer { error ->
            checkError(error)
        })
        mReferenceViewModel.error.observe(this, Observer { error ->
            checkError(error)
        })

        // If network is disconnected and user clicks restart, get data again
        restart.setOnClickListener {
            if (verifyAvailableNetwork(this)) {
                mFinanceViewModel.fetchClients(collectorId!!) // fetch clients
                progress_bar.visibility = View.VISIBLE
                recyclerView.visibility = View.VISIBLE
                network_disconnected.visibility = View.GONE
            }
        }

        // Fetch clients
        mFinanceViewModel.fetchClients(collectorId!!)

    }

    override fun onResume() {
        super.onResume()
        val data = getData()
        if (data != null) {
            val list = mFinanceViewModel.changeData(data)
            finAdapter.setData(list)
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
        if (!verifyAvailableNetwork(this)) {
            network_disconnected.visibility = View.VISIBLE
            recyclerView.visibility = View.INVISIBLE
        } else {
            exceptionHandler(error, this) // Show error
        }
    }

    private fun getData(): Client? {
        val pref = PreferenceManager.getDefaultSharedPreferences(this)
        val client = pref.getString("data", "")
        return if (client != "") {
            val obj = Gson().fromJson<Client>(client, Client::class.java)
            if (obj.maCollectMoneyId != 0L) obj else null
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