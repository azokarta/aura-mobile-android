package kz.aura.merp.employee.ui.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.Button
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import kz.aura.merp.employee.R
import kz.aura.merp.employee.adapter.DemoAdapter
import kz.aura.merp.employee.viewmodel.SharedViewModel
import kz.aura.merp.employee.model.Demo
import kz.aura.merp.employee.viewmodel.DealerViewModel
import kz.aura.merp.employee.databinding.ActivityDealerBinding
import kz.aura.merp.employee.util.LanguageHelper
import com.google.gson.Gson
import kz.aura.merp.employee.viewmodel.ReferenceViewModel
import kz.aura.merp.employee.util.verifyAvailableNetwork
import kz.aura.merp.employee.util.Permissions

class DealerActivity : AppCompatActivity() {

    private val mDealerViewModel: DealerViewModel by viewModels()
    private val mSharedViewModel: SharedViewModel by viewModels()
    private val mReferenceViewModel: ReferenceViewModel by viewModels()
    private val adapter: DemoAdapter by lazy { DemoAdapter() }
    private lateinit var binding: ActivityDealerBinding
    private var dealerId: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LanguageHelper.updateLanguage(this)

        // Turn off screenshot
        window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)

        // Data binding
        binding = ActivityDealerBinding.inflate(layoutInflater)
        binding.lifecycleOwner = this
        binding.mSharedViewModel = mSharedViewModel
        val view = binding.root
        setContentView(view)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = getString(R.string.dealer)
//        supportActionBar?.subtitle = getStaff(this)?.username

        Permissions(this, this).enableLocation()

        // Get dealer id
//        dealerId = getStaffId(this)

        // Observe MutableLiveData
        mDealerViewModel.demoList.observe(this, { data ->
            adapter.setData(data)
        })

        // Setup RecyclerView
        setupRecyclerview()

        // Observe errors
        mDealerViewModel.error.observe(this, { error ->
            checkError(error)
        })
        mReferenceViewModel.error.observe(this, { error ->
            checkError(error)
        })

        // If network is disconnected and user clicks restart, get data again
//        findViewById<Button>(R.id.restart).setOnClickListener {
//            if (verifyAvailableNetwork(this)) {
//                mDealerViewModel.fetchAll(dealerId!!) // fetch demo list
//                binding.progressBar.visibility = View.VISIBLE
//                binding.recyclerView.visibility = View.VISIBLE
//                findViewById<ConstraintLayout>(R.id.networkDisconnected).visibility = View.GONE
//            }
//        }

        // Fetch demoList
        mDealerViewModel.fetchAll(dealerId!!)
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
        if (!verifyAvailableNetwork(this)) {
            findViewById<ConstraintLayout>(R.id.networkDisconnected).visibility = View.VISIBLE
            binding.recyclerView.visibility = View.INVISIBLE
        } else {
//            exceptionHandler(error, this) // Show error
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.menu, menu)

        return super.onCreateOptionsMenu(menu)
    }

    private fun setupRecyclerview() {
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
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