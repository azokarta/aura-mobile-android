package kz.aura.merp.employee.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kz.aura.merp.employee.databinding.PhoneNumberRowBinding
import kz.aura.merp.employee.view.OnSelectPhoneNumber

class PhoneNumbersAdapter(private val iOnSelectPhoneNumber: OnSelectPhoneNumber? = null) :
    ListAdapter<String, PhoneNumbersAdapter.PhoneNumbersViewHolder>(PhoneNumbersDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhoneNumbersViewHolder {
        return PhoneNumbersViewHolder.from(parent, iOnSelectPhoneNumber)
    }

    override fun onBindViewHolder(holder: PhoneNumbersViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class PhoneNumbersViewHolder(val binding: PhoneNumberRowBinding, private val iOnSelectPhoneNumber: OnSelectPhoneNumber?) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(phoneNumber: String?) {
            binding.phone = phoneNumber
            binding.executePendingBindings()

            binding.incomingBtn.setOnClickListener {
                iOnSelectPhoneNumber?.incoming(phoneNumber!!)
            }
            binding.outgoingBtn.setOnClickListener {
                iOnSelectPhoneNumber?.outgoing(phoneNumber!!)
            }
        }

        companion object {
            fun from(parent: ViewGroup, iOnSelectPhoneNumber: OnSelectPhoneNumber?): PhoneNumbersViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = PhoneNumberRowBinding.inflate(layoutInflater, parent, false)
                return PhoneNumbersViewHolder(binding, iOnSelectPhoneNumber)
            }
        }
    }

    private class PhoneNumbersDiffUtil : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean = oldItem == newItem

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean = oldItem == newItem
    }

}