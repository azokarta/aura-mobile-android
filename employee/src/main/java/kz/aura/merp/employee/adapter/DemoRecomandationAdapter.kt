package kz.aura.merp.employee.adapter

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kz.aura.merp.employee.R
import kz.aura.merp.employee.data.model.Recomandation
import kotlinx.android.synthetic.main.demo_recomandation_card.view.*

class DemoRecomandationAdapter(var recomandations: ArrayList<Recomandation>) :
    RecyclerView.Adapter<DemoRecomandationViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DemoRecomandationViewHolder {
        val inflater = LayoutInflater.from(parent.context)
            .inflate(R.layout.demo_recomandation_card, parent, false)
        return DemoRecomandationViewHolder(inflater)
    }

    override fun onBindViewHolder(holder: DemoRecomandationViewHolder, position: Int) {
        val recomandation = recomandations[position]

        holder.view.recomandation_fio_edit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                recomandation.fio = p0.toString()
            }

            override fun afterTextChanged(p0: Editable?) {}
        })
        holder.view.recomandation_phone_number_edit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                recomandation.phoneNumber = p0.toString()
            }

            override fun afterTextChanged(p0: Editable?) {}
        })

        if (recomandation.remove) {
            holder.view.recomandation_add_btn.setImageResource(R.drawable.ic_baseline_remove_24)
        } else {
            holder.view.recomandation_add_btn.setImageResource(R.drawable.ic_outline_add_24)
        }

        holder.view.recomandation_add_btn.setOnClickListener {
            if (!recomandation.remove) {
                recomandations.add(Recomandation("", "", false))
                notifyDataSetChanged()
                recomandation.remove = true

            } else {
                recomandations.removeAt(position)
                notifyDataSetChanged()
            }
        }
    }

    override fun getItemCount(): Int = recomandations.size
}

class DemoRecomandationViewHolder(val view: View) : RecyclerView.ViewHolder(view)