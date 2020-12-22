package kz.aura.merp.customer.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.aura.R
import kz.aura.merp.customer.activities.ProductDetailsActivity
import kz.aura.merp.customer.models.Product
import kz.aura.merp.customer.utils.Helpers.dateFormatter
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.product_card.view.*

class ProductsAdapter(private val products: ArrayList<Product>) : RecyclerView.Adapter<ProductViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val inflater = LayoutInflater.from(parent.context).inflate(R.layout.product_card, parent, false)
        return ProductViewHolder(inflater)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        var product: Product = products[position]
        holder.view.product_title.text = product.matnrName
        holder.view.purchase_date.text = dateFormatter(product.contractDate)
        holder.view.service_date.text = dateFormatter(product.lastServiceDate)
        holder.view.payment_till.text = dateFormatter(product.nextPaymentDate)
        holder.view.product_payment.text = product.paymentAmount.toString()
        holder.view.product_payment.text = product.price.toString()
        val image = "https://www.rain-del-queen.co.za/images/homePage/roboclean.png"
        Picasso.get()
            .load(image)
            .into(holder.view.product_image)
        if((product.price - product.paid) == 0.toDouble()){
            holder.view.product_payment.visibility = View.GONE
            holder.view.product_payment_amount.visibility = View.GONE
            holder.view.payment_till.visibility = View.GONE
            holder.view.payment_till_text.visibility = View.GONE
//            holder.view.product_btn.visibility = View.GONE
            holder.view.product_payed_icon.visibility = View.VISIBLE
            holder.view.product_payed.visibility = View.VISIBLE
        }
        holder.contractId = product.contractId
    }

    override fun getItemCount(): Int = products.size
}

class ProductViewHolder(val view: View, var contractId: Long? = null) : RecyclerView.ViewHolder(view) {
    init {
        view.setOnClickListener {
            val intent = Intent(view.context, ProductDetailsActivity::class.java)
            intent.putExtra("contractId", contractId)
            view.context.startActivity(intent)
        }
    }
}