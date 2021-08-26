package kz.aura.merp.employee.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kz.aura.merp.employee.model.ServiceApplication
import kz.aura.merp.employee.databinding.ServiceApplicationCardBinding

class ServiceApplicationAdapter : RecyclerView.Adapter<ServiceApplicationAdapter.MasterViewHolder>() {

    private var dataList = mutableListOf<ServiceApplication>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MasterViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ServiceApplicationCardBinding.inflate(layoutInflater, parent, false)
        return MasterViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MasterViewHolder, position: Int) {
        val serviceApplication: ServiceApplication = dataList[position]
        holder.bind(serviceApplication)
    }

    fun setData(serviceApplications: List<ServiceApplication>) {
        val serviceApplicationDiffUtil = MobDiffUtil(dataList, serviceApplications)
        val serviceApplicationDiffResult = DiffUtil.calculateDiff(serviceApplicationDiffUtil)
        this.dataList.clear()
        this.dataList.addAll(serviceApplications)
        serviceApplicationDiffResult.dispatchUpdatesTo(this)
    }


    override fun getItemCount(): Int = dataList.size

    class MasterViewHolder(private val binding: ServiceApplicationCardBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(serviceApplication: ServiceApplication) {
            binding.serviceApplication = serviceApplication
            binding.executePendingBindings()
        }
    }
}