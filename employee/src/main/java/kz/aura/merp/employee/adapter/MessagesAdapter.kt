package kz.aura.merp.employee.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kz.aura.merp.employee.databinding.MessageCardBinding
import kz.aura.merp.employee.model.Message
import kz.aura.merp.employee.util.MobDiffUtil
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter

class MessagesAdapter : RecyclerView.Adapter<MessagesAdapter.MessagesViewHolder>() {
    private val messages = mutableListOf<Message>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MessagesViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = MessageCardBinding.inflate(layoutInflater, parent, false)
        return MessagesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MessagesViewHolder, position: Int) {
        holder.bind(messages[position])
    }

    fun setData(messages: List<Message>) {
        val messagesDiffUtil = MobDiffUtil(this.messages, messages)
        val messagesDiffResult = DiffUtil.calculateDiff(messagesDiffUtil)
        this.messages.clear()
        this.messages.addAll(messages)
        messagesDiffResult.dispatchUpdatesTo(this)
    }

    override fun getItemCount(): Int = messages.size

    class MessagesViewHolder(private val binding: MessageCardBinding) : RecyclerView.ViewHolder(binding.root)  {
        fun bind(message: Message) {
            binding.setMessage(message)
            binding.executePendingBindings()
        }
    }
}