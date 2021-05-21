package kz.aura.merp.employee.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.widget.Toolbar
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kz.aura.merp.employee.R
import kz.aura.merp.employee.model.*
import kz.aura.merp.employee.databinding.ActivityPlanBinding
import kz.aura.merp.employee.ui.fragment.finance.*
import kz.aura.merp.employee.util.PagerAdapter

@AndroidEntryPoint
class PlanActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlanBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.plan)

        // Turn off screenshot
        window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)

//        val fragments = arrayListOf(
//            ContractFragment.newInstance(plan),
//            PlanContributionsFragment.newInstance(plan),
//            PlanPaymentScheduleFragment.newInstance(plan.contractId, plan.contractCurrencyName!!),
//            PlanCallsFragment.newInstance(plan.contractId)
//        )
//
//        binding.viewPager.adapter = PagerAdapter(this, fragments)
//        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
//            tab.text = when (position) {
//                0 -> getString(R.string.contract)
//                1 -> getString(R.string.сontributions)
//                2 -> getString(R.string.paymentSchedule)
//                3 -> getString(R.string.calls)
//                4 -> getString(R.string.scheduled_calls)
//                else -> null
//            }
//        }.attach()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}