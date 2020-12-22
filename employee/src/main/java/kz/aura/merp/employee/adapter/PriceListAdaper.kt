package kz.aura.merp.employee.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import kz.aura.merp.employee.R
import kz.aura.merp.employee.data.model.PriceList

class PriceListAdaper(private val priceList: ArrayList<PriceList>, val context: Context) : BaseAdapter() {
    override fun getCount(): Int = priceList.size

    override fun getItem(p0: Int) = priceList[p0]

    override fun getItemId(p0: Int): Long = p0.toLong()

    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        val priceItem = priceList[p0]
        val layoutInflater = LayoutInflater.from(context)
        val row = layoutInflater.inflate(R.layout.price_list_row, p2, false)
        val price = row.findViewById<TextView>(R.id.price)
        val tradeIn = row.findViewById<TextView>(R.id.tradeIn)
        val month = row.findViewById<TextView>(R.id.month)
        val waers = row.findViewById<TextView>(R.id.waers)
        val tradeInLabel = row.findViewById<TextView>(R.id.trade_in_label)
        if (priceItem.tradeIn != 0) {
            tradeIn.visibility = View.VISIBLE
            tradeInLabel.visibility = View.VISIBLE
        }
        price.text = priceItem.price.toString()
        tradeIn.text = priceItem.tradeIn.toString()
        month.text = priceItem.month.toString()
        waers.text = priceItem.waers.toString()
        return row
    }
}