package kz.aura.merp.customer.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.preference.PreferenceManager
import com.example.aura.R
import kotlinx.android.synthetic.main.activity_auth.*
import kotlinx.android.synthetic.main.fragment_profile.view.*

class AuthActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
    }

    fun signInTest(view: View) {

        if (customerID.text.toString() != "") {
            var customerID = customerID.text.toString().toLong()

        PreferenceManager.getDefaultSharedPreferences(this)
                .edit()
                .putLong("customerId", customerID)
                .apply()


        var intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
    }

    fun toOCR(view: View) {
        val intent = Intent(this, OcrWebActivity::class.java)
        startActivity(intent)
    }
}