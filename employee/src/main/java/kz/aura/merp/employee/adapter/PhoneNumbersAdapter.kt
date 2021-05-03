package kz.aura.merp.employee.adapter

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kz.aura.merp.employee.databinding.PhoneNumberRowBinding
import kz.aura.merp.employee.ui.activity.AddCallActivity
import kz.aura.merp.employee.util.MobDiffUtil

class PhoneNumbersAdapter : RecyclerView.Adapter<PhoneNumbersAdapter.PhoneNumbersViewHolder>() {

    var dataList = emptyList<String?>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhoneNumbersViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = PhoneNumberRowBinding.inflate(inflater, parent, false)

        return PhoneNumbersViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PhoneNumbersViewHolder, position: Int) {
        val phoneNumber = dataList[position]
        holder.bind(phoneNumber)
    }

    override fun getItemCount(): Int = dataList.size

    fun setData(phoneNumbers: List<String?>?) {
        if (phoneNumbers != null) {
            val phoneDiffUtil = MobDiffUtil(dataList, phoneNumbers)
            val phoneDiffResult = DiffUtil.calculateDiff(phoneDiffUtil)
            this.dataList = phoneNumbers
            phoneDiffResult.dispatchUpdatesTo(this)
        }
    }

    class PhoneNumbersViewHolder(val binding: PhoneNumberRowBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(phoneNumber: String?) {
            binding.phone = phoneNumber
            binding.executePendingBindings()

            binding.callBtn.setOnClickListener {
                val intent = Intent(binding.root.context, AddCallActivity::class.java)
                intent.putExtra("phoneNumber", phoneNumber)
                binding.root.context.startActivity(intent)
            }
        }
    }

}