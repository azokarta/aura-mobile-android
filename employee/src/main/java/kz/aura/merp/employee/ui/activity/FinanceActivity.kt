package kz.aura.merp.employee.ui.activity

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kz.aura.merp.employee.R
import kz.aura.merp.employee.databinding.ActivityFinanceBinding
import kz.aura.merp.employee.ui.fragment.finance.*
import kz.aura.merp.employee.util.*
import kz.aura.merp.employee.viewmodel.FinanceViewModel

@AndroidEntryPoint
class FinanceActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFinanceBinding
    private val mFinanceViewModel: FinanceViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LanguageHelper.updateLanguage(this)

        // Data binding
        binding = ActivityFinanceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = getString(R.string.finAgent)

        // Turn off screenshot
        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )

        Permissions(this, this).enableLocation()

        val fragments = arrayListOf(
            MonthlyPlanFragment(),
            ContributionsFragment(),
            CallsFragment(),
            ScheduledCallsFragment()
        )

        binding.viewPager.adapter = PagerAdapter(this, fragments)
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> getString(R.string.monthlyPlan)
                1 -> getString(R.string.сontributions)
                2 -> getString(R.string.calls)
                3 -> getString(R.string.scheduled_calls)
                else -> null
            }
        }.attach()

        mFinanceViewModel.staffUsername.observe(this, { username -> supportActionBar?.subtitle = username })

        mFinanceViewModel.getStaffUsername()

//        Intent(this, BackgroundService::class.java).also { intent ->
//            intent.putExtra("link", Link.FINANCE)
//            startService(intent)
//        }
    }
}