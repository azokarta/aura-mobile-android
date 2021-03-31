package kz.aura.merp.employee.ui.activity

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import kz.aura.merp.employee.util.StaffPosition
import kz.aura.merp.employee.databinding.ActivityChiefBinding
import kz.aura.merp.employee.util.saveDataByKey

class ChiefActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChiefBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChiefBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide() // Hide toolbar

        // Turn off screenshot
        window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)

        binding.dealer.setOnClickListener { signInToAccount(StaffPosition.DEALER) }
        binding.master.setOnClickListener { signInToAccount(StaffPosition.MASTER) }
        binding.finAgent.setOnClickListener { signInToAccount(StaffPosition.FIN_AGENT) }
    }

    private fun signInToAccount(position: StaffPosition) {
        val staffId = binding.staffId.text.toString()
        if (staffId.isNotEmpty()) {
            saveDataByKey(this, staffId.toLong(), "staffId")

            when (position) {
                StaffPosition.DEALER -> goToActivity(DealerActivity())
                StaffPosition.MASTER -> goToActivity(MasterActivity())
                StaffPosition.FIN_AGENT -> goToActivity(FinanceAgentActivity())
                else -> Toast.makeText(this, "Произошла ошибка повторите попытку позже", Toast.LENGTH_LONG).show()
            }
        } else {
            Toast.makeText(this, "Введите ID", Toast.LENGTH_LONG).show()
        }
    }

    private fun goToActivity(activity: Activity) {
        val intent = Intent(this, activity::class.java)
        startActivity(intent)
        finish()
    }
}