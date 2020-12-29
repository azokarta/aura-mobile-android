package kz.aura.merp.customer.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.WindowManager
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContentProviderCompat.requireContext
import com.example.aura.R
import kz.aura.merp.customer.data.model.PaymentCalendar
import com.r0adkll.slidr.Slidr
import com.r0adkll.slidr.model.SlidrInterface
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_calendar_item_details.*
import kz.aura.merp.customer.util.Helpers

class CalendarItemDetailsActivity : AppCompatActivity() {

    private lateinit var slidr: SlidrInterface

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar_item_details)

        // Off screenshot
        window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)

        setSupportActionBar(calendar_item_toolbar as Toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // received data
        val intent = intent
        val received: PaymentCalendar = intent.getSerializableExtra("data") as PaymentCalendar

        supportActionBar?.title = received.date

        slidr = Slidr.attach(this)

        calendar_view_dateTo.text = "до ${received.dateTo}"
        calendar_view_nextPayment.text = "След. платеж: ${received.nextPayment}"
        Picasso.get()
            .load(received.productImage)
            .into(calendar_view_image)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == android.R.id.home) {
            onBackPressed();
        }

        return true;
    }
}