package kz.aura.merp.employee.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import kz.aura.merp.employee.R
import kz.aura.merp.employee.model.ServiceApplication
//import kz.aura.merp.employee.util.TabLayoutFragmentAdapter
import kz.aura.merp.employee.viewmodel.MasterViewModel
import kz.aura.merp.employee.viewmodel.ReferenceViewModel
import kz.aura.merp.employee.databinding.ActivityServiceApplicationBinding
import kz.aura.merp.employee.ui.fragment.master.ServiceApplicationBusinessFragment
import kz.aura.merp.employee.ui.fragment.master.ServiceApplicationDataFragment

class ServiceApplicationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityServiceApplicationBinding
    private val mMasterViewModel: MasterViewModel by viewModels()
    private val mReferenceViewModel: ReferenceViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityServiceApplicationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.service)

        // Turn off screenshot
        window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)

        val serviceApplication = intent.getParcelableExtra<ServiceApplication>("serviceApplication")!!

        val titles = ArrayList<String>()
        val fragments = ArrayList<Fragment>()

        titles.add(getString(R.string.service))
        titles.add(getString(R.string.businessProcesses))

        fragments.add(ServiceApplicationDataFragment.newInstance(serviceApplication))
        fragments.add(ServiceApplicationBusinessFragment.newInstance(serviceApplication))

//        val fragmentAdapter = TabLayoutFragmentAdapter(supportFragmentManager, fragments, titles)
//        binding.serviceApplicationViewPager.adapter = fragmentAdapter
        binding.serviceApplicationTabLayout.setupWithViewPager(binding.serviceApplicationViewPager)

        // Errors
//        mMasterViewModel.error.observe(this, Observer { error ->
//            exceptionHandler(error, this)
//        })
//        mReferenceViewModel.error.observe(this, Observer { error ->
//            exceptionHandler(error, this)
//        })
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

}