package kz.aura.merp.employee.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import kz.aura.merp.employee.R
import kz.aura.merp.employee.fragment.*
import kz.aura.merp.employee.data.model.Demo
import kz.aura.merp.employee.util.TabLayoutFragmentAdapter
import kotlinx.android.synthetic.main.activity_demo.*


class DemoActivity : AppCompatActivity() {
    private lateinit var demo: Demo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_demo)
        setSupportActionBar(toolbar as Toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.demo)

        demo = intent.getSerializableExtra("demo") as Demo

        val titles = ArrayList<String>()
        val fragments = ArrayList<Fragment>()

        titles.add(getString(R.string.demoData))
        titles.add(getString(R.string.businessProcesses))

        fragments.add(DemoDataFragment.newInstance(demo))
        fragments.add(DemoBusinessProcessesFragment.newInstance(demo))

        val fragmentAdapter = TabLayoutFragmentAdapter(supportFragmentManager, fragments, titles)
        demo_view_pager.adapter = fragmentAdapter
        demo_tab_layout.setupWithViewPager(demo_view_pager)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}