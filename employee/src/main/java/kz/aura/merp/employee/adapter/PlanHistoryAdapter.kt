package kz.aura.merp.employee.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kz.aura.merp.employee.R
import kz.aura.merp.employee.model.PlanHistoryItem
import kz.aura.merp.employee.databinding.PlanHistoryItemRowBinding

class PlanHistoryAdapter : RecyclerView.Adapter<PlanHistoryAdapter.PlanHistoryViewHolder>() {

    var dataList = mutableListOf<PlanHistoryItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlanHistoryViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = PlanHistoryItemRowBinding.inflate(layoutInflater, parent, false)
        return PlanHistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlanHistoryViewHolder, position: Int) {
        holder.bind(dataList[position])
    }

    fun setData(history: List<PlanHistoryItem>) {
        val historyDiffUtil = MobDiffUtil(dataList, history)
        val historyDiffResult = DiffUtil.calculateDiff(historyDiffUtil)
        this.dataList.clear()
        this.dataList.addAll(history)
        historyDiffResult.dispatchUpdatesTo(this)
    }

    override fun getItemCount(): Int = dataList.size

    class PlanHistoryViewHolder(private val binding: PlanHistoryItemRowBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(historyItem: PlanHistoryItem) {
            binding.historyItem = historyItem
            binding.executePendingBindings()
            displayPaymentMethodIcons(historyItem)
        }

        private fun displayPaymentMethodIcons(historyItem: PlanHistoryItem) {
            val paymentMethodId = historyItem.planPaymentMethodId
            val bankDrawable = when (historyItem.planPaymentBankId) {
                2L -> R.drawable.ic_forte_bank
                4L -> R.drawable.ic_halyk_bank
                5L -> R.drawable.ic_center_credit_bank
                6L -> R.drawable.ic_atf_bank
                7L -> R.drawable.ic_kaspi_bank
                else -> R.drawable.ic_baseline_account_balance_24
            }

            binding.paymentMethodImg.visibility = View.VISIBLE
            binding.bankImg.visibility = View.VISIBLE

            when (historyItem.planResultId) {
                1L -> {
                    binding.paymentMethodImg.setImageResource(R.drawable.ic_baseline_help_outline_24)
                    binding.bankImg.visibility = View.GONE
                }
                2L -> {
                    when (paymentMethodId) {
                        1L -> {
                            binding.paymentMethodImg.setImageResource(R.drawable.ic_baseline_money_24)
                            binding.bankImg.visibility = View.GONE
                        }
                        2L -> {
                            binding.paymentMethodImg.setImageResource(R.drawable.ic_outline_change_circle_24)
                            binding.bankImg.setImageResource(bankDrawable)
                        }
                        3L -> {
                            binding.paymentMethodImg.setImageResource(R.drawable.ic_baseline_account_balance_24)
                            binding.bankImg.setImageResource(bankDrawable)
                        }
                    }
                }
                3L -> {
                    binding.paymentMethodImg.setImageResource(R.drawable.ic_baseline_date_range_24)
                    binding.bankImg.visibility = View.GONE
                }
                4L -> {
                    binding.paymentMethodImg.setImageResource(R.drawable.ic_baseline_close_24)
                    binding.bankImg.visibility = View.GONE
                }
                else -> {
                    binding.paymentMethodImg.visibility = View.GONE
                    binding.bankImg.visibility = View.GONE
                }
            }

        }
    }

}