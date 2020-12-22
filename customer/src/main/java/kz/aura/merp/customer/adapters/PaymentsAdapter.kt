package kz.aura.merp.customer.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kz.aura.merp.customer.models.PaymentSchedule
import kz.aura.merp.customer.utils.Helpers.dateFormatter
import kz.aura.merp.customer.utils.Helpers.decimalFormatter
import com.example.aura.R
import kotlinx.android.synthetic.main.payment_card.view.*


class PaymentsAdapter(private val paymentSchedules: ArrayList<PaymentSchedule>) : RecyclerView.Adapter<PaymentsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentsViewHolder {
        val inflater = LayoutInflater.from(parent.context).inflate(R.layout.payment_card, parent, false)
        return PaymentsViewHolder(inflater)
    }

    override fun onBindViewHolder(holder: PaymentsViewHolder, position: Int) {
        val paymentSchedule: PaymentSchedule = paymentSchedules[position]
        if (paymentSchedule.paymentTypeId == 1) {
            holder.view.payment_title.text = paymentSchedule.matnrName
            holder.view.payment_service_description.visibility = View.GONE
        } else {
            holder.view.payment_title.text = "Услуга"
            holder.view.payment_service_description.visibility = View.VISIBLE
            holder.view.payment_service_description.text = "Примечание: ${paymentSchedule.serviceDescription}"
        }
        holder.view.payment_date.text = dateFormatter(paymentSchedule.paymentDate)
        holder.view.payment_schedule_paid.text = decimalFormatter(paymentSchedule.sum2 - paymentSchedule.paid)+paymentSchedule.waers
    }

    override fun getItemCount(): Int = paymentSchedules.size
}

class PaymentsViewHolder(val view: View) : RecyclerView.ViewHolder(view) {}