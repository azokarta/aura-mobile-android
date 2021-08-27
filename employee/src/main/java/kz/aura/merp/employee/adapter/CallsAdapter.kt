package kz.aura.merp.employee.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kz.aura.merp.employee.databinding.CallCardBinding
import kz.aura.merp.employee.model.Call

class CallsAdapter : ListAdapter<Call, CallsAdapter.CallsViewHolder>(CallsDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CallsViewHolder {
        return CallsViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: CallsViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class CallsViewHolder(private val binding: CallCardBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(call: Call) {
            binding.call = call
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): CallsViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = CallCardBinding.inflate(layoutInflater, parent, false)
                return CallsViewHolder(binding)
            }
        }
    }

    private class CallsDiffUtil : DiffUtil.ItemCallback<Call>() {
        override fun areItemsTheSame(oldItem: Call, newItem: Call): Boolean = oldItem == newItem

        override fun areContentsTheSame(oldItem: Call, newItem: Call): Boolean = oldItem.id == newItem.id
    }
}