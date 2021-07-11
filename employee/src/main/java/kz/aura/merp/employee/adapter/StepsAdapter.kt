package kz.aura.merp.employee.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kz.aura.merp.employee.R
import kz.aura.merp.employee.model.BusinessProcessStatus
import kz.aura.merp.employee.databinding.StepViewRowBinding
import kz.aura.merp.employee.util.MobDiffUtil

class StepsAdapter(val completedStepListener: CompletedStepListener) : RecyclerView.Adapter<StepsAdapter.StepsViewHolder>() {
    private var dataList = mutableListOf<BusinessProcessStatus>()
    private var step: Long? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StepsViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = StepViewRowBinding.inflate(layoutInflater, parent, false)
        return StepsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StepsViewHolder, position: Int) {
        holder.bind(position)
    }

    fun setData(steps: List<BusinessProcessStatus>) {
        val stepDiffUtil = MobDiffUtil(dataList, steps)
        val stepDiffResult = DiffUtil.calculateDiff(stepDiffUtil)
        this.dataList.clear()
        this.dataList.addAll(steps)
        stepDiffResult.dispatchUpdatesTo(this)
    }

    fun setStep(step: Long) {
        this.step = step
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = dataList.size

    companion object {
        interface CompletedStepListener {
            fun stepCompleted(businessProcessStatus: BusinessProcessStatus, position: Int)
        }
    }

    inner class StepsViewHolder(val binding: StepViewRowBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            binding.stepTitle = dataList[position].name
            binding.executePendingBindings()

            drawByStep(step, dataList.size, position)

            binding.root.setOnClickListener {
                if (step == null && dataList[position].id == 1L) {
                    completedStepListener.stepCompleted(dataList[position], position)
                }
                if (step != null && step!! < dataList[position].id) {
                    completedStepListener.stepCompleted(dataList[position], position)
                }
            }
        }

        private fun drawByStep(step: Long?, size: Int, position: Int) {
            if (step != null  && step >= dataList[position].id) {
                binding.stepIcon.setImageResource(R.drawable.ic_baseline_check_circle_24)
                binding.stepLine.setImageResource(R.drawable.completed_line)
                binding.stepIcon.setColorFilter(ContextCompat.getColor(binding.root.context, R.color.colorPrimary))
            }
            if (step == null || step < dataList[position].id) {
                binding.stepIcon.setImageResource(R.drawable.ic_baseline_radio_button_checked_24)
                binding.stepIcon.setColorFilter(ContextCompat.getColor(binding.root.context, R.color.lightGray))
                binding.stepLine.setImageResource(R.drawable.step_line)
            }
            if (position == size - 1) {
                binding.stepLine.visibility = View.GONE
            }
        }
    }

}