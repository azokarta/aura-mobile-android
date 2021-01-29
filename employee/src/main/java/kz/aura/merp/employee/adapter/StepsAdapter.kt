package kz.aura.merp.employee.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kz.aura.merp.employee.R
import kz.aura.merp.employee.databinding.StepViewRowBinding
import kz.aura.merp.employee.diffUtil.StepDiffUtil

class StepsAdapter(val completedStepListener: CompletedStepListener) : RecyclerView.Adapter<StepsAdapter.StepsViewHolder>() {
    private var dataList = mutableListOf<String>()
    private var step = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StepsViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = StepViewRowBinding.inflate(layoutInflater, parent, false)
        return StepsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StepsViewHolder, position: Int) {
        holder.bind(dataList[position], position, step, dataList.size)

        holder.binding.completedBtn.setOnClickListener {
            step+=1
            notifyDataSetChanged()
            completedStepListener.stepCompleted(position)
        }
    }

    fun setData(steps: ArrayList<String>) {
        val stepDiffUtil = StepDiffUtil(dataList, steps)
        val stepDiffResult = DiffUtil.calculateDiff(stepDiffUtil)
        this.dataList.clear()
        this.dataList.addAll(steps)
        stepDiffResult.dispatchUpdatesTo(this)
    }

    fun setStep(step: Int) {
        this.step = step
    }

    override fun getItemCount(): Int = dataList.size

    companion object {
        interface CompletedStepListener {
            fun stepCompleted(position: Int)
        }
    }

    class StepsViewHolder(val binding: StepViewRowBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(stepTitle: String, position: Int, step: Int, size: Int) {
            binding.stepTitle = stepTitle
            binding.executePendingBindings()

            drawByStep(step, size, position)
        }

        private fun drawByStep(step: Int, size: Int, position: Int) {
            if (step == position) {
                binding.stepText.setTextColor(ContextCompat.getColor(binding.root.context, R.color.colorBlack))
                binding.stepIcon.setImageResource(R.drawable.ic_baseline_radio_button_checked_24)
                binding.completedBtn.visibility = View.VISIBLE
            }
            if (step > position) {
                binding.stepIcon.setImageResource(R.drawable.ic_baseline_check_circle_24)
                binding.stepLine.setImageResource(R.drawable.completed_line)
                binding.stepText.setTextColor(ContextCompat.getColor(binding.root.context, R.color.colorBlack))
                binding.completedBtn.visibility = View.INVISIBLE
            }
            if (step < position) {
                binding.stepIcon.setImageResource(R.drawable.ic_baseline_radio_button_checked_24)
                binding.stepIcon.setColorFilter(ContextCompat.getColor(binding.root.context, R.color.colorLightGray))
                binding.stepLine.setImageResource(R.drawable.step_line)
                binding.completedBtn.visibility = View.INVISIBLE
            }
            if (position == size - 1) {
                binding.stepLine.visibility = View.GONE
            }
        }
    }

}