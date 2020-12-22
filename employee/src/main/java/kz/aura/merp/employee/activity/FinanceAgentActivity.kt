package kz.aura.merp.employee.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
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
import kotlinx.android.synthetic.main.activity_fin_agent.*

class FinanceAgentActivity : AppCompatActivity() {

    private val mFinanceViewModel: FinanceViewModel by viewModels()
    private val finAdapter: FinanceAdapter by lazy { FinanceAdapter() }
    private val mSharedViewModel: SharedViewModel by viewModels()
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
        supportActionBar?.title = "Финансовый агент"

        // Get collector id
        collectorId = Helpers.getStaffId(this)

        // Setup RecyclerView
        fin_agent_recycler_view.layoutManager = LinearLayoutManager(applicationContext)
        fin_agent_recycler_view.adapter = finAdapter

        // Observe MutableLiveData
        mFinanceViewModel.clients.observe(this, Observer { data ->
            mSharedViewModel.checkData(data)
            finAdapter.setData(data)
        })

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