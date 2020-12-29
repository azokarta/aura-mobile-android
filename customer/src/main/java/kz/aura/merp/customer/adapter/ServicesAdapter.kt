package kz.aura.merp.customer.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kz.aura.merp.customer.activity.ServiceActivity
import kz.aura.merp.customer.data.model.Service
import kz.aura.merp.customer.util.Helpers.decimalFormatter
import kz.aura.merp.customer.util.Helpers.dateFormatter
import com.example.aura.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.service_card.view.*

class ServicesAdapter(private val services: ArrayList<Service>) : RecyclerView.Adapter<ServicesViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServicesViewHolder {
        val inflater = LayoutInflater.from(parent.context).inflate(R.layout.service_card, parent, false)
        return ServicesViewHolder(inflater)
    }

    override fun onBindViewHolder(holder: ServicesViewHolder, position: Int) {
        val image = "https://www.rain-del-queen.co.za/images/homePage/roboclean.png"
        val service: Service = services[position]
        holder.view.date_of_service_card.text = dateFormatter(service.dateOpen)
        holder.view.description_of_service_card.text = service.description
        holder.view.amount_of_service_card.text = decimalFormatter(service.paymentDue) + " " + service.waers
        holder.view.arrears_of_serivce_card.text = decimalFormatter(service.sumTotal- service.paid!!)+" "+service.waers
//        holder.view.service_card_pay_btn.visibility = if (service.paid != 0.0) View.VISIBLE else View.INVISIBLE
        holder.serviceId = service.id
        Picasso.get()
            .load(image)
            .into(holder.view.service_image)
    }

    override fun getItemCount(): Int = services.size
}

class ServicesViewHolder(val view: View, var serviceId: Long? = null) : RecyclerView.ViewHolder(view) {
    init {
        view.setOnClickListener {
            val intent = Intent(view.context, ServiceActivity::class.java)
            intent.putExtra("serviceId", serviceId)
            view.context.startActivity(intent)
        }
    }
}