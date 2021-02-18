package kz.aura.merp.employee.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import kz.aura.merp.employee.R
import kz.aura.merp.employee.data.model.Client
import kz.aura.merp.employee.databinding.ActivityPlanBinding
import kz.aura.merp.employee.util.TabLayoutFragmentAdapter
import kz.aura.merp.employee.fragment.finance.FinanceBusinessProcessFragment
import kz.aura.merp.employee.fragment.finance.FinanceClientInfoFragment

class PlanActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlanBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlanBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.client)

        val plan = intent.getParcelableExtra<Client>("client")!!

        val titles = ArrayList<String>()
        val fragments = ArrayList<Fragment>()

        titles.add(getString(R.string.info))
        titles.add(getString(R.string.businessProcesses))

        fragments.add(FinanceClientInfoFragment.newInstance(plan))
        fragments.add(FinanceBusinessProcessFragment.newInstance(plan))

        val fragmentAdapter = TabLayoutFragmentAdapter(supportFragmentManager, fragments, titles)
        binding.viewPager.adapter = fragmentAdapter
        binding.tabLayout.setupWithViewPager(binding.viewPager)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

}