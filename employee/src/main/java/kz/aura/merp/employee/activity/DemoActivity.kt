package kz.aura.merp.employee.activity

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import kz.aura.merp.employee.R
import kz.aura.merp.employee.data.model.Demo
import kz.aura.merp.employee.util.TabLayoutFragmentAdapter
import kz.aura.merp.employee.data.viewmodel.DealerViewModel
import kz.aura.merp.employee.data.viewmodel.ReferenceViewModel
import kz.aura.merp.employee.databinding.ActivityDemoBinding
import kz.aura.merp.employee.fragment.dealer.DemoBusinessProcessesFragment
import kz.aura.merp.employee.fragment.dealer.DemoDataFragment
import kz.aura.merp.employee.fragment.dealer.DemoRecommendationFragment
import kz.aura.merp.employee.util.Helpers

class DemoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDemoBinding

    private val mDealerViewModel: DealerViewModel by viewModels()
    private val mReferenceViewModel: ReferenceViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDemoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.demo)

        val demo = intent.getParcelableExtra<Demo>("demo")!!

        val titles = ArrayList<String>()
        val fragments = ArrayList<Fragment>()

        titles.add(getString(R.string.demoData))
        titles.add(getString(R.string.businessProcesses))
        titles.add(getString(R.string.recommendation))

        fragments.add(DemoDataFragment.newInstance(demo))
        fragments.add(DemoBusinessProcessesFragment.newInstance(demo))
        fragments.add(DemoRecommendationFragment())

        val fragmentAdapter = TabLayoutFragmentAdapter(supportFragmentManager, fragments, titles)
        binding.demoViewPager.adapter = fragmentAdapter
        binding.demoTabLayout.setupWithViewPager(binding.demoViewPager)

        // Errors
        mDealerViewModel.error.observe(this, Observer { error ->
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