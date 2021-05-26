package kz.aura.merp.employee.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kz.aura.merp.employee.R
import kz.aura.merp.employee.databinding.ContributionCardBinding
import kz.aura.merp.employee.model.Contribution
import kz.aura.merp.employee.util.MobDiffUtil

class ContributionsAdapter : RecyclerView.Adapter<ContributionsAdapter.ContributionsViewHolder>() {

    private var dataList = mutableListOf<Contribution>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ContributionsViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ContributionCardBinding.inflate(layoutInflater, parent, false)
        return ContributionsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ContributionsViewHolder, position: Int) {
        holder.bind(dataList[position])
    }

    override fun getItemCount(): Int = dataList.size

    fun setData(contributions: List<Contribution>) {
        val contributionsDiffUtil = MobDiffUtil(dataList, contributions)
        val contributionsDiffResult = DiffUtil.calculateDiff(contributionsDiffUtil)
        this.dataList.clear()
        this.dataList.addAll(contributions)
        contributionsDiffResult.dispatchUpdatesTo(this)
    }

    class ContributionsViewHolder(val binding: ContributionCardBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(contribution: Contribution) {
            binding.contribution = contribution
            binding.executePendingBindings()
            displayPaymentMethodIcons(contribution)
        }

        private fun displayPaymentMethodIcons(contribution: Contribution) {
            val paymentMethodId = contribution.paymentMethodId
            val bankDrawable = when (contribution.paymentBankId) {
                2L -> R.drawable.ic_forte_bank
                4L -> R.drawable.ic_halyk_bank
                5L -> R.drawable.ic_center_credit_bank
                6L -> R.drawable.ic_atf_bank
                7L -> R.drawable.ic_kaspi_bank
                else -> R.drawable.ic_baseline_account_balance_24
            }

            binding.paymentMethodImg.visibility = View.VISIBLE
            binding.bankImg.visibility = View.VISIBLE

            when (contribution.resultId) {
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