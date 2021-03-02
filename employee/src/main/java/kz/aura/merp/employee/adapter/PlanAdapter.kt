package kz.aura.merp.employee.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kz.aura.merp.employee.data.model.Plan
import kz.aura.merp.employee.databinding.FinAgentCardBinding
import kz.aura.merp.employee.util.MobDiffUtil

class PlanAdapter : RecyclerView.Adapter<PlanAdapter.FinanceViewHolder>() {

    private var dataList = mutableListOf<Plan>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FinanceViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = FinAgentCardBinding.inflate(layoutInflater, parent, false)
        return FinanceViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FinanceViewHolder, position: Int) {
        val plan: Plan = dataList[position]
        holder.bind(plan)
    }

    fun setData(plans: ArrayList<Plan>) {
        val clientDiffUtil = MobDiffUtil(dataList, plans)
        val clientDiffResult = DiffUtil.calculateDiff(clientDiffUtil)
        this.dataList.clear()
        this.dataList.addAll(plans)
        clientDiffResult.dispatchUpdatesTo(this)
    }


    override fun getItemCount(): Int = dataList.size

    class FinanceViewHolder(private val binding: FinAgentCardBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(plan: Plan) {
            binding.plan = plan
            binding.executePendingBindings()
        }
    }

}