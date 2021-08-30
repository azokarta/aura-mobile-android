package kz.aura.merp.employee.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kz.aura.merp.employee.databinding.ScheduledCallCardBinding
import kz.aura.merp.employee.model.ScheduledCall

class ScheduledCallsAdapter : ListAdapter<ScheduledCall, ScheduledCallsAdapter.ScheduledCallsViewHolder>(ScheduledCallsDiffUtil()) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ScheduledCallsViewHolder {
        return ScheduledCallsViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ScheduledCallsViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ScheduledCallsViewHolder(private val binding: ScheduledCallCardBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(call: ScheduledCall) {
            binding.scheduledCall = call
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ScheduledCallsViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ScheduledCallCardBinding.inflate(layoutInflater, parent, false)
                return ScheduledCallsViewHolder(binding)
            }
        }
    }

    private class ScheduledCallsDiffUtil : DiffUtil.ItemCallback<ScheduledCall>() {
        override fun areItemsTheSame(oldItem: ScheduledCall, newItem: ScheduledCall): Boolean = oldItem == newItem

        override fun areContentsTheSame(oldItem: ScheduledCall, newItem: ScheduledCall): Boolean = oldItem.id == newItem.id
    }
}