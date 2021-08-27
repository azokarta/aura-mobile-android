package kz.aura.merp.employee.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kz.aura.merp.employee.databinding.MessageCardBinding
import kz.aura.merp.employee.model.Message

class MessagesAdapter : ListAdapter<Message, MessagesAdapter.MessagesViewHolder>(MessagesDiffUtil()) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MessagesViewHolder {
        return MessagesViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: MessagesViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class MessagesViewHolder(private val binding: MessageCardBinding) : RecyclerView.ViewHolder(binding.root)  {
        fun bind(message: Message) {
            binding.setMessage(message)
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): MessagesViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = MessageCardBinding.inflate(layoutInflater, parent, false)
                return MessagesViewHolder(binding)
            }
        }
    }

    private class MessagesDiffUtil : DiffUtil.ItemCallback<Message>() {
        override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean = oldItem == newItem

        override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean = oldItem == newItem
    }
}