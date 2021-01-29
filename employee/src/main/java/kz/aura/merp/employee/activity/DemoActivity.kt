package kz.aura.merp.employee.activity

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import kz.aura.merp.employee.R
import kz.aura.merp.employee.fragment.*
import kz.aura.merp.employee.data.model.Demo
import kz.aura.merp.employee.util.TabLayoutFragmentAdapter
import kotlinx.android.synthetic.main.activity_demo.*
import kz.aura.merp.employee.data.viewmodel.DealerViewModel
import kz.aura.merp.employee.data.viewmodel.ReferenceViewModel
import kz.aura.merp.employee.fragment.dealer.DemoBusinessProcessesFragment
import kz.aura.merp.employee.fragment.dealer.DemoDataFragment
import kz.aura.merp.employee.util.Helpers


class DemoActivity : AppCompatActivity() {
    private val mDealerViewModel: DealerViewModel by viewModels()
    private val mReferenceViewModel: ReferenceViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_demo)
        setSupportActionBar(toolbar as Toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.demo)

        val demo = intent.getParcelableExtra<Demo>("demo")!!

        val titles = ArrayList<String>()
        val fragments = ArrayList<Fragment>()

        titles.add(getString(R.string.demoData))
        titles.add(getString(R.string.businessProcesses))

        fragments.add(DemoDataFragment.newInstance(demo))
        fragments.add(DemoBusinessProcessesFragment.newInstance(demo))

        val fragmentAdapter = TabLayoutFragmentAdapter(supportFragmentManager, fragments, titles)
        demo_view_pager.adapter = fragmentAdapter
        demo_tab_layout.setupWithViewPager(demo_view_pager)

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