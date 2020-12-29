package kz.aura.merp.customer.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kz.aura.merp.customer.activity.ConfirmPayActivity
import kz.aura.merp.customer.data.model.Defrayal
import com.example.aura.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.defrayal_card.view.*

class DefrayalAdapter(private val defrayals: ArrayList<Defrayal>) : RecyclerView.Adapter<DefrayalViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DefrayalViewHolder {
        val inflater = LayoutInflater.from(parent.context).inflate(R.layout.defrayal_card, parent, false)
        return DefrayalViewHolder(inflater)
    }

    override fun onBindViewHolder(holder: DefrayalViewHolder, position: Int) {
        val image = "https://www.rain-del-queen.co.za/images/homePage/roboclean.png"
        val defrayal: Defrayal = defrayals[position]
        holder.view.defrayal_title.text = defrayal.title
        holder.view.defrayal_amount.text = defrayal.paymentDue.toString()+" "+defrayal.waers
        Picasso.get()
            .load(image)
            .into(holder.view.defrayal_image)
    }

    override fun getItemCount(): Int = defrayals.size
}

class DefrayalViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
    init {
        view.defrayal_card_pay_btn.setOnClickListener {
            val intent = Intent(view.context, ConfirmPayActivity::class.java)
            view.context.startActivity(intent)
        }
    }
}