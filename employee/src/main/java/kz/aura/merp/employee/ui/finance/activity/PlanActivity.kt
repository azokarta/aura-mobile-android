package kz.aura.merp.employee.ui.finance.activity

import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kz.aura.merp.employee.R
import kz.aura.merp.employee.base.BaseActivity
import kz.aura.merp.employee.databinding.ActivityPlanBinding
import kz.aura.merp.employee.ui.finance.fragment.ContractFragment
import kz.aura.merp.employee.ui.finance.fragment.PlanCallsFragment
import kz.aura.merp.employee.ui.finance.fragment.PlanContributionsFragment
import kz.aura.merp.employee.ui.finance.fragment.PlanPaymentScheduleFragment
import kz.aura.merp.employee.ui.fragment.finance.*
import kz.aura.merp.employee.util.PagerAdapter

@AndroidEntryPoint
class PlanActivity : BaseActivity() {

    private lateinit var binding: ActivityPlanBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val receivedContractId = intent.getLongExtra("contractId", 0)

        // Toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.plan)

        val fragments = arrayListOf(
            ContractFragment(),
            PlanContributionsFragment(),
            PlanPaymentScheduleFragment(),
            PlanCallsFragment()
        )

        val bundle = Bundle()
        bundle.putLong("contractId", receivedContractId)


        binding.viewPager.adapter = PagerAdapter(bundle, this, fragments)
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> getString(R.string.contract)
                1 -> getString(R.string.contributions)
                2 -> getString(R.string.payment_schedule)
                3 -> getString(R.string.calls)
                else -> null
            }
        }.attach()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}