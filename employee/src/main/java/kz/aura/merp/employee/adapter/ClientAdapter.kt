package kz.aura.merp.employee.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kz.aura.merp.employee.data.model.Client
import kz.aura.merp.employee.databinding.FinAgentCardBinding
import kz.aura.merp.employee.diffUtil.ClientDiffUtil

class ClientAdapter : RecyclerView.Adapter<ClientAdapter.FinanceViewHolder>() {

    private var dataList = mutableListOf<Client>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FinanceViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = FinAgentCardBinding.inflate(layoutInflater, parent, false)
        return FinanceViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FinanceViewHolder, position: Int) {
        val client: Client = dataList[position]
        holder.bind(client)
    }

    fun setData(clients: ArrayList<Client>) {
        val clientDiffUtil = ClientDiffUtil(dataList, clients)
        val clientDiffResult = DiffUtil.calculateDiff(clientDiffUtil)
        this.dataList.clear()
        this.dataList.addAll(clients)
        clientDiffResult.dispatchUpdatesTo(this)
    }


    override fun getItemCount(): Int = dataList.size

    class FinanceViewHolder(private val binding: FinAgentCardBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(client: Client) {
            binding.client = client
            binding.executePendingBindings()
        }
    }

}