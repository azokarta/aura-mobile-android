package kz.aura.merp.employee.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kz.aura.merp.employee.data.model.PhoneNumber
import kz.aura.merp.employee.databinding.PhoneNumberRowBinding
import kz.aura.merp.employee.diffUtil.PhoneNumberDiffUtil

class PhoneNumberAdapter : RecyclerView.Adapter<DemoDataPhoneNumberViewHolder>() {

    var dataList = emptyList<PhoneNumber>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DemoDataPhoneNumberViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = PhoneNumberRowBinding.inflate(inflater, parent, false)

        return DemoDataPhoneNumberViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DemoDataPhoneNumberViewHolder, position: Int) {
        val phoneNumber = dataList[position]
        holder.bind(phoneNumber)
    }

    override fun getItemCount(): Int = dataList.size

    fun setData(phoneNumbers: ArrayList<PhoneNumber>) {
        val phoneDiffUtil = PhoneNumberDiffUtil(dataList, phoneNumbers)
        val phoneDiffResult = DiffUtil.calculateDiff(phoneDiffUtil)
        this.dataList = phoneNumbers
        phoneDiffResult.dispatchUpdatesTo(this)
    }

}

class DemoDataPhoneNumberViewHolder(val binding: PhoneNumberRowBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(phoneNumber: PhoneNumber) {
        binding.phone = phoneNumber
        binding.executePendingBindings()
    }
}