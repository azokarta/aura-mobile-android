package kz.aura.merp.employee.activity

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kz.aura.merp.employee.R
import kz.aura.merp.employee.util.Helpers.saveDataByKey
import kz.aura.merp.employee.util.StaffPosition
import kotlinx.android.synthetic.main.activity_chief.*

class ChiefActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chief)
        supportActionBar?.hide() // Hide toolbar

        dealer.setOnClickListener { signInToAccount(StaffPosition.DEALER) }
        master.setOnClickListener { signInToAccount(StaffPosition.MASTER) }
        fin_agent.setOnClickListener { signInToAccount(StaffPosition.FIN_AGENT) }
    }

    private fun signInToAccount(position: StaffPosition) {
        val staffId = staffId.text.toString()
        if (staffId.isNotEmpty()) {
            saveDataByKey(this, staffId, "staffId")

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