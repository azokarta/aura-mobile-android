package kz.aura.merp.employee.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kz.aura.merp.employee.databinding.ScheduledCallCardBinding
import kz.aura.merp.employee.model.ScheduledCall

class ScheduledCallsAdapter : RecyclerView.Adapter<ScheduledCallsAdapter.ScheduledCallsViewHolder>() {

    private var dataList = mutableListOf<ScheduledCall>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ScheduledCallsViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ScheduledCallCardBinding.inflate(layoutInflater, parent, false)
        return ScheduledCallsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ScheduledCallsViewHolder, position: Int) {
        holder.bind(dataList[position])
    }

    override fun getItemCount(): Int = dataList.size

    fun setData(calls: List<ScheduledCall>) {
        val callsDiffUtil = MobDiffUtil(dataList, calls)
        val callsDiffResult = DiffUtil.calculateDiff(callsDiffUtil)
        this.dataList.clear()
        this.dataList.addAll(calls)
        callsDiffResult.dispatchUpdatesTo(this)
    }

    class ScheduledCallsViewHolder(private val binding: ScheduledCallCardBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(call: ScheduledCall) {
            binding.scheduledCall = call
            binding.executePendingBindings()
        }
    }
}