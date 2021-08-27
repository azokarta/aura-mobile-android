package kz.aura.merp.employee.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kz.aura.merp.employee.R
import kz.aura.merp.employee.databinding.ContributionCardBinding
import kz.aura.merp.employee.model.Contribution

class ContributionsAdapter : ListAdapter<Contribution, ContributionsAdapter.ContributionsViewHolder>(ContributionsDiffUtil()) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ContributionsViewHolder {
        return ContributionsViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ContributionsViewHolder, position: Int) {
        holder.bind(getItem(position))
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

        companion object {
            fun from(parent: ViewGroup): ContributionsViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ContributionCardBinding.inflate(layoutInflater, parent, false)
                return ContributionsViewHolder(binding)
            }
        }
    }

    private class ContributionsDiffUtil : DiffUtil.ItemCallback<Contribution>() {
        override fun areItemsTheSame(oldItem: Contribution, newItem: Contribution): Boolean = oldItem == newItem

        override fun areContentsTheSame(oldItem: Contribution, newItem: Contribution): Boolean = oldItem.id == newItem.id
    }
}