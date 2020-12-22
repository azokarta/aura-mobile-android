package kz.aura.merp.customer.adapterItems

import com.example.aura.R
import kz.aura.merp.customer.models.HomeProduct
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.home_product_card.view.*

class HomeProductItem (val product: HomeProduct): Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.home_product_title.text = product.productTitle
        Picasso.get()
            .load(product.image)
            .into(viewHolder.itemView.home_product_image)

    }

    override fun getLayout(): Int {
        return R.layout.home_product_card
    }
}