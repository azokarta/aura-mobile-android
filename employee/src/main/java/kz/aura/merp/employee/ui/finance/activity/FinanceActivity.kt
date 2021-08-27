package kz.aura.merp.employee.ui.finance.activity

import android.os.Bundle
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import dagger.hilt.android.AndroidEntryPoint
import kz.aura.merp.employee.R
import kz.aura.merp.employee.base.BaseActivity
import kz.aura.merp.employee.databinding.ActivityFinanceBinding
import kz.aura.merp.employee.ui.common.SignOutDialogFragment
import kz.aura.merp.employee.viewmodel.FinanceViewModel

@AndroidEntryPoint
class FinanceActivity : BaseActivity() {

    private lateinit var binding: ActivityFinanceBinding
    private val financeViewModel: FinanceViewModel by viewModels()
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFinanceBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        val headerView = navView.getHeaderView(0)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(setOf(
            R.id.nav_monthly_plan,
            R.id.nav_daily_plan,
            R.id.nav_contributions,
            R.id.nav_calls,
            R.id.nav_scheduled_calls,
            R.id.nav_profile,
            R.id.nav_messages,
            R.id.nav_settings
        ), drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        navView.menu.findItem(R.id.nav_sign_out).setOnMenuItemClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
            openSignOutDialog()
            false
        }

        financeViewModel.salary.observe(this, { salary ->
            salary?.let {
                (headerView.findViewById<TextView>(R.id.username)).text = salary.username
                (headerView.findViewById<TextView>(R.id.phone)).text = salary.phoneNumber
            }
        })

        financeViewModel.getSalary()

//        Intent(this, BackgroundService::class.java).also { intent ->
//            intent.putExtra("link", Link.FINANCE)
//            startService(intent)
//        }
    }

    private fun openSignOutDialog() {
        val dialog = SignOutDialogFragment()
        dialog.show(supportFragmentManager, "FinanceActivity")
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}