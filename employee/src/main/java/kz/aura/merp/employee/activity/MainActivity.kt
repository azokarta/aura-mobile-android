package kz.aura.merp.employee.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toolbar
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import kotlinx.android.synthetic.main.activity_main.*
import kz.aura.merp.employee.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        setSupportActionBar(findViewById(R.id.toolbar))

        setupActionBarWithNavController(findNavController(R.id.navHostFragment))
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.navHostFragment)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}