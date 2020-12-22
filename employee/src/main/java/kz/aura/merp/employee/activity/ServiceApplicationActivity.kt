package kz.aura.merp.employee.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import kz.aura.merp.employee.R
import kz.aura.merp.employee.fragment.ServiceApplicationBusinessFragment
import kz.aura.merp.employee.fragment.ServiceApplicationDataFragment
import kz.aura.merp.employee.data.model.ServiceApplication
import kz.aura.merp.employee.util.TabLayoutFragmentAdapter
import kotlinx.android.synthetic.main.activity_service_application.*

class ServiceApplicationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_service_application)
        setSupportActionBar(toolbar as Toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.service)

        val serviceApplication = intent.getSerializableExtra("serviceApplication") as ServiceApplication

        val titles = ArrayList<String>()
        val fragments = ArrayList<Fragment>()

        titles.add(getString(R.string.service))
        titles.add(getString(R.string.businessProcesses))

        fragments.add(ServiceApplicationDataFragment.newInstance(serviceApplication))
        fragments.add(ServiceApplicationBusinessFragment.newInstance(serviceApplication))

        val fragmentAdapter = TabLayoutFragmentAdapter(supportFragmentManager, fragments, titles)
        service_application_view_pager.adapter = fragmentAdapter
        service_application_tab_layout.setupWithViewPager(service_application_view_pager)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

}