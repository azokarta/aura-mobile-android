package kz.aura.merp.employee.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kz.aura.merp.employee.activity.ServiceApplicationActivity
import kz.aura.merp.employee.data.model.ServiceApplication
import kz.aura.merp.employee.databinding.ServiceApplicationCardBinding
import kz.aura.merp.employee.diffUtil.ServiceApplicationDiffUtil

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

    fun setData(serviceApplications: ArrayList<ServiceApplication>) {
        val serviceApplicationDiffUtil = ServiceApplicationDiffUtil(dataList, serviceApplications)
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

            binding.root.setOnClickListener {
                val intent = Intent(binding.root.context, ServiceApplicationActivity::class.java)
                intent.putExtra("serviceApplication", serviceApplication)
                binding.root.context.startActivity(intent)
            }
        }
    }
}