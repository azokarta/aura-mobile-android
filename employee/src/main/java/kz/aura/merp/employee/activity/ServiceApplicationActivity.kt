package kz.aura.merp.employee.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import kz.aura.merp.employee.R
import kz.aura.merp.employee.data.model.ServiceApplication
import kz.aura.merp.employee.util.TabLayoutFragmentAdapter
import kz.aura.merp.employee.data.viewmodel.MasterViewModel
import kz.aura.merp.employee.data.viewmodel.ReferenceViewModel
import kz.aura.merp.employee.databinding.ActivityServiceApplicationBinding
import kz.aura.merp.employee.fragment.master.ServiceApplicationBusinessFragment
import kz.aura.merp.employee.fragment.master.ServiceApplicationDataFragment
import kz.aura.merp.employee.util.Helpers

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

        val serviceApplication = intent.getParcelableExtra<ServiceApplication>("serviceApplication")!!

        val titles = ArrayList<String>()
        val fragments = ArrayList<Fragment>()

        titles.add(getString(R.string.service))
        titles.add(getString(R.string.businessProcesses))

        fragments.add(ServiceApplicationDataFragment.newInstance(serviceApplication))
        fragments.add(ServiceApplicationBusinessFragment.newInstance(serviceApplication))

        val fragmentAdapter = TabLayoutFragmentAdapter(supportFragmentManager, fragments, titles)
        binding.serviceApplicationViewPager.adapter = fragmentAdapter
        binding.serviceApplicationTabLayout.setupWithViewPager(binding.serviceApplicationViewPager)

        // Errors
        mMasterViewModel.error.observe(this, Observer { error ->
            Helpers.exceptionHandler(error, this)
        })
        mReferenceViewModel.error.observe(this, Observer { error ->
            Helpers.exceptionHandler(error, this)
        })
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

}