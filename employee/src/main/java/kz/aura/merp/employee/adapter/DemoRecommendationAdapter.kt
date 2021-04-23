package kz.aura.merp.employee.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.RecyclerView
import kz.aura.merp.employee.model.DemoRecommendation
import kz.aura.merp.employee.databinding.DemoRecommendationCardBinding

class DemoRecommendationAdapter : RecyclerView.Adapter<DemoRecommendationAdapter.DemoRecommendationViewHolder>() {

    var dataList = arrayListOf<DemoRecommendation>().apply {
        add(DemoRecommendation("", ""))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DemoRecommendationViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = DemoRecommendationCardBinding.inflate(layoutInflater, parent, false)
        return DemoRecommendationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DemoRecommendationViewHolder, position: Int) {
        holder.bind(dataList[position])
    }

    override fun getItemCount(): Int = dataList.size

    fun addRecommendation() {
        dataList.add(DemoRecommendation("", ""))
        notifyItemInserted(dataList.size - 1)
    }

    fun deleteItem(index: Int){
        dataList.removeAt(index)
        notifyItemRemoved(index)
        notifyItemRangeChanged(index, dataList.size)
    }

    inner class DemoRecommendationViewHolder(private val binding: DemoRecommendationCardBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(demoRecommendation: DemoRecommendation) {

            binding.fullNameDemoRecommendation.doOnTextChanged { text, _, _, _ ->
                demoRecommendation.fullName = text.toString()
            }

            binding.phoneNumberDemoRecommendation.doOnTextChanged { text, _, _, _ ->
                demoRecommendation.phoneNumber = text.toString()
            }

            binding.fullNameDemoRecommendation.setText(demoRecommendation.fullName)
            binding.phoneNumberDemoRecommendation.setText(demoRecommendation.phoneNumber)

            binding.removeRecommendation.setOnClickListener {
                deleteItem(adapterPosition)
            }
        }
    }
}