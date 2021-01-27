package kz.aura.merp.employee.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kz.aura.merp.employee.data.model.Demo
import kz.aura.merp.employee.databinding.DemoCardBinding
import kz.aura.merp.employee.diffUtil.DemoDiffUtil

class DemoAdapter : RecyclerView.Adapter<DemoAdapter.DemoViewHolder>() {

    private var dataList = mutableListOf<Demo>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DemoViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = DemoCardBinding.inflate(layoutInflater, parent, false)
        return DemoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DemoViewHolder, position: Int) {
        val demo: Demo = dataList[position]
        holder.bind(demo)
    }

    fun setData(demoList: ArrayList<Demo>) {
        val demoDiffUtil = DemoDiffUtil(dataList, demoList)
        val demoDiffResult = DiffUtil.calculateDiff(demoDiffUtil)
        this.dataList.clear()
        this.dataList.addAll(demoList)
        demoDiffResult.dispatchUpdatesTo(this)
    }


    override fun getItemCount(): Int = dataList.size

    class DemoViewHolder(private val binding: DemoCardBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(demo: Demo) {
            binding.demo = demo
            binding.executePendingBindings()

            binding.root.setOnClickListener {
//                val intent = Intent(binding.root.context, DemoActivity::class.java)
//                intent.putExtra("demo", demo)
//                binding.root.context.startActivity(intent)
            }
        }
    }
}