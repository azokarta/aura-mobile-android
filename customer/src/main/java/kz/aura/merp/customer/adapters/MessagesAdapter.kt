package kz.aura.merp.customer.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kz.aura.merp.customer.models.Message
import kz.aura.merp.customer.utils.Helpers.dateFormatter
import com.example.aura.R
import kotlinx.android.synthetic.main.message_card.view.*

class MessagesAdapter(private val messages: ArrayList<Message>) : RecyclerView.Adapter<MessagesViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessagesViewHolder {
        val inflater = LayoutInflater.from(parent.context).inflate(R.layout.message_card, parent, false)
        return MessagesViewHolder(inflater)
    }

    override fun onBindViewHolder(holder: MessagesViewHolder, position: Int) {
        val message: Message = messages[position]
        if (message.read == 0) {
            holder.view.message_read.visibility = View.GONE
        }
        holder.view.message_card_message.text = message.message
        holder.view.message_card_date.text = dateFormatter(message.date)
//        holder.view.message_card_btn.text = if (message.messageType <= 2) "Оплатить" else "Подтвердить"
    }

    override fun getItemCount(): Int = messages.size
}

class MessagesViewHolder(val view: View) : RecyclerView.ViewHolder(view)