package kz.aura.merp.employee.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kz.aura.merp.employee.databinding.CallCardBinding
import kz.aura.merp.employee.model.Call
import kz.aura.merp.employee.util.MobDiffUtil

class CallsAdapter : RecyclerView.Adapter<CallsAdapter.CallsViewHolder>() {

    private var dataList = mutableListOf<Call>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CallsViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = CallCardBinding.inflate(layoutInflater, parent, false)
        return CallsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CallsViewHolder, position: Int) {
        holder.bind(dataList[position])
    }

    override fun getItemCount(): Int = dataList.size

    fun setData(calls: ArrayList<Call>) {
        val callsDiffUtil = MobDiffUtil(dataList, calls)
        val callsDiffResult = DiffUtil.calculateDiff(callsDiffUtil)
        this.dataList.clear()
        this.dataList.addAll(calls)
        callsDiffResult.dispatchUpdatesTo(this)
    }

    class CallsViewHolder(private val binding: CallCardBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(call: Call) {
            binding.call = call
            binding.executePendingBindings()
        }
    }
}