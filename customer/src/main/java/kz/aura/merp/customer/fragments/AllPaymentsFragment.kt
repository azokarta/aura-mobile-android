package kz.aura.merp.customer.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.aura.R
import kz.aura.merp.customer.activities.CalendarViewActivity
import kz.aura.merp.customer.adapters.PaymentsAdapter
import kz.aura.merp.customer.models.PaymentSchedule
import kz.aura.merp.customer.presenters.IPaymentsPresenter
import kz.aura.merp.customer.presenters.PaymentsPresenter
import kz.aura.merp.customer.utils.Constants
import kz.aura.merp.customer.views.IPaymentsView
import kotlinx.android.synthetic.main.fragment_all_payments.*

class AllPaymentsFragment : Fragment(), IPaymentsView {

    private lateinit var paymentsPresenter: IPaymentsPresenter
    private var paymentSchedules = arrayListOf<PaymentSchedule>()
    private val paymentsAdapter = PaymentsAdapter(paymentSchedules)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_all_payments, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        paymentsPresenter = PaymentsPresenter(this, this.requireContext())
        paymentsPresenter.getAll(Constants.CUSTOMER_ID)

        all_payments_recycler_view.layoutManager = LinearLayoutManager(this.requireContext())
        all_payments_recycler_view.adapter = paymentsAdapter

        calendar_view.setOnClickListener {
            val intent = Intent(context, CalendarViewActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onSuccessPayments(paymentSchedules: ArrayList<PaymentSchedule>) {
        paymentSchedules.addAll(paymentSchedules)
        paymentsAdapter.notifyDataSetChanged()
    }

    override fun hideProgressBar() {
        all_payments_progress_bar.visibility = View.GONE
        calendar_view.visibility = View.VISIBLE
        all_payments_recycler_view.visibility = View.VISIBLE
    }

    override fun showProgressBar() {
        all_payments_progress_bar.visibility = View.VISIBLE
        calendar_view.visibility = View.GONE
        all_payments_recycler_view.visibility = View.GONE
    }

}