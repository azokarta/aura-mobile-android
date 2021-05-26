package kz.aura.merp.employee.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kz.aura.merp.employee.model.PaymentSchedule
import kz.aura.merp.employee.databinding.PaymentScheduleRowBinding
import kz.aura.merp.employee.util.MobDiffUtil

class PaymentScheduleAdapter : RecyclerView.Adapter<PaymentScheduleAdapter.PaymentScheduleViewHolder>() {

    private var dataList = mutableListOf<PaymentSchedule>()
    private var currency = ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentScheduleViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = PaymentScheduleRowBinding.inflate(layoutInflater, parent, false)
        return PaymentScheduleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PaymentScheduleViewHolder, position: Int) {
        holder.bind(dataList[position], currency)
    }

    fun setData(paymentSchedule: List<PaymentSchedule>, currency: String) {
        this.currency = currency
        val paymentScheduleDiffUtil = MobDiffUtil(dataList, paymentSchedule)
        val paymentScheduleDiffResult = DiffUtil.calculateDiff(paymentScheduleDiffUtil)
        this.dataList.clear()
        this.dataList.addAll(paymentSchedule)
        paymentScheduleDiffResult.dispatchUpdatesTo(this)
    }

    override fun getItemCount(): Int = dataList.size

    class PaymentScheduleViewHolder(private val binding: PaymentScheduleRowBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(paymentSchedule: PaymentSchedule, currency: String) {
            binding.paymentSchedule = paymentSchedule
            binding.idx = adapterPosition+1
            binding.currency = currency
            binding.executePendingBindings()
        }
    }
}