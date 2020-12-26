
package kz.aura.merp.employee.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import kz.aura.merp.employee.R
import kz.aura.merp.employee.fragment.*
import kz.aura.merp.employee.data.model.Client
import kz.aura.merp.employee.util.TabLayoutFragmentAdapter
import kotlinx.android.synthetic.main.activity_client.*

class ClientActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_client)
        setSupportActionBar(toolbar as Toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.client)

        val plan = intent.getSerializableExtra("client") as Client

        val titles = ArrayList<String>()
        val fragments = ArrayList<Fragment>()

        titles.add(getString(R.string.info))
        titles.add(getString(R.string.businessProcesses))

        fragments.add(FinanceClientInfoFragment.newInstance(plan))
        fragments.add(FinanceBusinessProcessFragment.newInstance(plan))

        val fragmentAdapter = TabLayoutFragmentAdapter(supportFragmentManager, fragments, titles)
        fin_view_pager.adapter = fragmentAdapter
        fin_agent_tabLayout.setupWithViewPager(fin_view_pager)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

}