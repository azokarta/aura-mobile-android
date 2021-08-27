package kz.aura.merp.employee.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kz.aura.merp.employee.model.PaymentSchedule
import kz.aura.merp.employee.databinding.PaymentScheduleRowBinding

class PaymentScheduleAdapter : ListAdapter<PaymentSchedule, PaymentScheduleAdapter.PaymentScheduleViewHolder>(PaymentScheduleDiffUtil()) {

    var currency: String = ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentScheduleViewHolder {
        return PaymentScheduleViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: PaymentScheduleViewHolder, position: Int) {
        holder.bind(getItem(position), currency)
    }

    class PaymentScheduleViewHolder(private val binding: PaymentScheduleRowBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(paymentSchedule: PaymentSchedule, currency: String) {
            binding.paymentSchedule = paymentSchedule
            binding.idx = adapterPosition+1
            binding.currency = currency
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): PaymentScheduleViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = PaymentScheduleRowBinding.inflate(layoutInflater, parent, false)
                return PaymentScheduleViewHolder(binding)
            }
        }
    }

    private class PaymentScheduleDiffUtil : DiffUtil.ItemCallback<PaymentSchedule>() {
        override fun areItemsTheSame(oldItem: PaymentSchedule, newItem: PaymentSchedule): Boolean = oldItem == newItem

        override fun areContentsTheSame(
            oldItem: PaymentSchedule,
            newItem: PaymentSchedule
        ): Boolean = oldItem == newItem
    }
}