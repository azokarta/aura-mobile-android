package kz.aura.merp.customer.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import kz.aura.merp.customer.adapter.PaymentScheduleAdapter
import kz.aura.merp.customer.data.model.PaymentSchedule
import com.example.aura.R
import kz.aura.merp.customer.data.model.Product
import kz.aura.merp.customer.data.model.Service
import kz.aura.merp.customer.presenters.IPaymentsPresenter
import kz.aura.merp.customer.presenters.PaymentsPresenter
import kz.aura.merp.customer.util.Helpers.decimalFormatter
import kz.aura.merp.customer.views.IPaymentsView
import com.r0adkll.slidr.Slidr
import com.r0adkll.slidr.model.SlidrInterface
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_payment_schedule.*
import kz.aura.merp.customer.util.Helpers

class PaymentScheduleActivity : AppCompatActivity(), IPaymentsView {

    private lateinit var slidr: SlidrInterface
    private lateinit var paymentsPresenter: IPaymentsPresenter
    private val paymentScheduleList = arrayListOf<PaymentSchedule>()
    private lateinit var paymentScheduleAdapter: PaymentScheduleAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_schedule)

        // Off screenshot
        window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)

        // Toolbar
        setSupportActionBar(payment_schedule_toolbar as Toolbar)
        supportActionBar?.title = "График платежей"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        slidr = Slidr.attach(this)

        paymentsPresenter = PaymentsPresenter(this, applicationContext)

        // REMEMBER !!!
        // We're getting product or service
        // We're using only one in these:
        val product = intent.getSerializableExtra("product") as Product?
        val service = intent.getSerializableExtra("service") as Service?
        initData(product, service)

        paymentScheduleAdapter = PaymentScheduleAdapter(paymentScheduleList, product?.waers ?: service!!.waers)

        payment_schedule_recycler_view.layoutManager = LinearLayoutManager(this)
        payment_schedule_recycler_view.adapter = paymentScheduleAdapter
    }

//    private fun hideProgressBar(visibility: Boolean) {
//        if (visibility) {
////            payment_schedule_pay_btn.visibility = View.VISIBLE
//            payment_schedule_recycler_view.visibility = View.VISIBLE
//            payment_schedule_progress_bar.visibility = View.INVISIBLE
//        } else {
////            payment_schedule_pay_btn.visibility = View.INVISIBLE
//            payment_schedule_recycler_view.visibility = View.INVISIBLE
//            payment_schedule_progress_bar.visibility = View.VISIBLE
//        }
//    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == android.R.id.home) {
            onBackPressed();
        }

        return true;
    }

    override fun onSuccessPaymentsSchedule(paymentSchedules: ArrayList<PaymentSchedule>) {
        paymentScheduleList.addAll(paymentSchedules)
        paymentScheduleAdapter.notifyDataSetChanged()
    }

    private fun initData(product: Product?, service: Service?) {
        val image = "https://www.rain-del-queen.co.za/images/homePage/roboclean.png"
        if (product != null) {
            paymentsPresenter.getPaymentsSchedule(product.awkey, product.bukrs)
            payment_schedule_title.text = product.matnrName
            payment_schedule_paid.text = "Оплачено: ${decimalFormatter(product.paid)} ${product.waers}"
            payment_schedule_remainder.text = "Остаток: ${decimalFormatter(product.price - product.paid)} ${product.waers}"
            payment_schedule_description.visibility = View.GONE
            Picasso.get()
                .load(image)
                .into(payment_schedule_image)
        } else if (service != null) {
            payment_schedule_image.visibility = View.INVISIBLE
            paymentsPresenter.getPaymentsSchedule(service.awkey, service.bukrs)
            payment_schedule_title.text = "Услуга"
            payment_schedule_paid.text = "Оплачено: ${decimalFormatter(service.paid)} ${service.waers}"
            payment_schedule_remainder.text = "Остаток: ${decimalFormatter(service.paymentDue - service.paid!!)} ${service.waers}"
            payment_schedule_description.text = "Примечание: ${service.description}"
        }
    }
}