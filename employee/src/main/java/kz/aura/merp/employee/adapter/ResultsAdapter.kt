package kz.aura.merp.employee.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kz.aura.merp.employee.model.Result
import kz.aura.merp.employee.databinding.ResultRowBinding
import kz.aura.merp.employee.util.MobDiffUtil
import kz.aura.merp.employee.view.OnSelectResult

class ResultsAdapter(val iOnSelectResult: OnSelectResult) : RecyclerView.Adapter<ResultsAdapter.ResultsViewHolder>() {

    private var dataList = mutableListOf<Result>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultsViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ResultRowBinding.inflate(layoutInflater, parent, false)
        return ResultsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ResultsViewHolder, position: Int) {
        holder.bind(dataList[position], position)
    }

    fun setData(results: List<Result>) {
        val resultsDiffUtil = MobDiffUtil(dataList, results)
        val resultsDiffResult = DiffUtil.calculateDiff(resultsDiffUtil)
        this.dataList.clear()
        this.dataList.addAll(results)
        resultsDiffResult.dispatchUpdatesTo(this)
    }

    override fun getItemCount(): Int = dataList.size

    inner class ResultsViewHolder(private val binding: ResultRowBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(result: Result, position: Int) {
            binding.result = result.title
            binding.resultIcon.setImageResource(result.icon)
            binding.executePendingBindings()
            binding.root.setOnClickListener {
                iOnSelectResult.selectResult(position)
            }
        }
    }
}