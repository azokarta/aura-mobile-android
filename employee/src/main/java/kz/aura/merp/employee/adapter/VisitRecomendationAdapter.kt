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

class VisitRecomendationAdapter(var recomandations: ArrayList<Recomandation>) : RecyclerView.Adapter<VisitRecomandationViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VisitRecomandationViewHolder {
        val inflater = LayoutInflater.from(parent.context).inflate(R.layout.demo_recomandation_card, parent, false)
        return VisitRecomandationViewHolder(inflater)
    }


    override fun getItemCount(): Int = recomandations.size
    override fun onBindViewHolder(holder: VisitRecomandationViewHolder, position: Int) {
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
        holder.view.recomandation_add_btn.setOnClickListener {
            if (recomandations.size != 1) {
                recomandations.removeAt(position)
                notifyDataSetChanged()
            }
        }
    }

}

class VisitRecomandationViewHolder(val view: View) : RecyclerView.ViewHolder(view)


