package kz.aura.merp.employee.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kz.aura.merp.employee.databinding.ScheduledCallCardBinding
import kz.aura.merp.employee.model.ScheduledCall

class ScheduledCallsAdapter : ListAdapter<ScheduledCall, ScheduledCallsAdapter.ScheduledCallsViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ScheduledCallsViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ScheduledCallCardBinding.inflate(layoutInflater, parent, false)
        return ScheduledCallsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ScheduledCallsViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ScheduledCallsViewHolder(private val binding: ScheduledCallCardBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(call: ScheduledCall) {
            binding.scheduledCall = call
            binding.executePendingBindings()
        }
    }
}