package kz.aura.merp.employee.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kz.aura.merp.employee.R
import kz.aura.merp.employee.model.*
import kz.aura.merp.employee.databinding.ActivityPlanBinding
import kz.aura.merp.employee.ui.fragment.finance.*
import kz.aura.merp.employee.util.PagerAdapter
import kz.aura.merp.employee.viewmodel.FinanceViewModel

@AndroidEntryPoint
class PlanActivity : AppCompatActivity() {

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

        // Turn off screenshot
        window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)

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
                2 -> getString(R.string.paymentSchedule)
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