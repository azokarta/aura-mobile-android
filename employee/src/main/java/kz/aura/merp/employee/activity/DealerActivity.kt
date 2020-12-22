package kz.aura.merp.employee.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import kz.aura.merp.employee.R
import kz.aura.merp.employee.adapter.DemoAdapter
import kz.aura.merp.employee.data.SharedViewModel
import kz.aura.merp.employee.data.model.Demo
import kz.aura.merp.employee.data.viewmodel.DemoViewModel
import kz.aura.merp.employee.databinding.ActivityDealerBinding
import kz.aura.merp.employee.util.GpsPermission
import kz.aura.merp.employee.util.GpsPermission.requestPermission
import kz.aura.merp.employee.util.Helpers.exceptionHandler
import kz.aura.merp.employee.util.Helpers.getStaffId
import kz.aura.merp.employee.util.LanguageHelper
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_dealer.*


class DealerActivity : AppCompatActivity() {

    private val mDemoViewModel: DemoViewModel by viewModels()
    private val mSharedViewModel: SharedViewModel by viewModels()
    private val adapter: DemoAdapter by lazy { DemoAdapter() }
    private lateinit var binding: ActivityDealerBinding
    private var dealerId: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LanguageHelper.updateLanguage(this)

        // Data binding
        binding = ActivityDealerBinding.inflate(layoutInflater)
        binding.lifecycleOwner = this
        binding.mSharedViewModel = mSharedViewModel
        val view = binding.root
        setContentView(view)

        setSupportActionBar(toolbar as Toolbar)
        supportActionBar?.title = getString(R.string.dealer)

        // Get dealer id
        dealerId = getStaffId(this)

        // Check if gps is on
        if (!GpsPermission.checkPermission(this)) {
            requestPermission(this)
        }

        // Observe MutableLiveData
        mDemoViewModel.demoList.observe(this, Observer { data ->
            mSharedViewModel.checkData(data)
            adapter.setData(data)
        })

        // Setup RecyclerView
        setupRecyclerview()

        // Observe MutableLiveData
        mDemoViewModel.error.observe(this, Observer { error ->
            exceptionHandler(error, this)
        })

        // Fetch demoList
        mDemoViewModel.fetchAll(dealerId!!)
    }

    override fun onResume() {
        super.onResume()
        // Get changed Object from Storage
        val data = getData()
        if (data != null) {
            val list = mDemoViewModel.changeData(data)
            adapter.setData(list)
            removeData()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.menu, menu)

        return super.onCreateOptionsMenu(menu)
    }

    private fun setupRecyclerview() {
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_settings -> toActivity(SettingsActivity())
        }

        return super.onOptionsItemSelected(item)
    }

    private fun toActivity(activity: Activity) {
        val intent = Intent(applicationContext, activity::class.java)
        startActivity(intent)
    }

    private fun getData(): Demo? {
        val pref = PreferenceManager.getDefaultSharedPreferences(this)
        val demo = pref.getString("data", "")
        return if (demo != "") {
            val obj = Gson().fromJson<Demo>(demo, Demo::class.java)
            if (obj.demoId != 0L) obj else null
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