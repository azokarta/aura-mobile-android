package kz.aura.merp.employee.diffUtil

import androidx.recyclerview.widget.DiffUtil
import kz.aura.merp.employee.data.model.Demo

class DemoDiffUtil(
    private val oldList: List<Demo>,
    private val newList: List<Demo>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] === newList[newItemPosition]
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}