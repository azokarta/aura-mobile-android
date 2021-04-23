package kz.aura.merp.employee.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kz.aura.merp.employee.R
import kz.aura.merp.employee.databinding.ActivityFinanceBinding
import kz.aura.merp.employee.ui.fragment.finance.*
import kz.aura.merp.employee.util.*

@AndroidEntryPoint
class FinanceActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFinanceBinding

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
        supportActionBar?.subtitle = getStaff(this)?.username

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

//        Intent(this, BackgroundService::class.java).also { intent ->
//            intent.putExtra("link", Link.FINANCE)
//            startService(intent)
//        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.menu, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_settings -> {
                val intent = Intent(applicationContext, SettingsActivity::class.java)
                startActivity(intent)
            }
        }

        return super.onOptionsItemSelected(item)
    }
}