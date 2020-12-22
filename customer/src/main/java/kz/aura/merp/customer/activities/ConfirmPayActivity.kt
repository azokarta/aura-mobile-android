package kz.aura.merp.customer.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.WindowManager
import androidx.appcompat.widget.Toolbar
import com.example.aura.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_confirm_pay.*

class ConfirmPayActivity : AppCompatActivity() {
    private val image = "https://www.rain-del-queen.co.za/images/homePage/roboclean.png"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirm_pay)
        window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)

        setSupportActionBar(confirm_pay_toolbar as Toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Оплата"

        Picasso.get()
            .load(image)
            .into(confirm_pay_image)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == android.R.id.home) {
            onBackPressed();
        }

        return true;
    }




}
