package kz.aura.merp.customer.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.aura.R
import kz.aura.merp.customer.activity.PaymentScheduleActivity
import kz.aura.merp.customer.data.model.Product
import kz.aura.merp.customer.util.Helpers.dateFormatter
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_product.*

private const val ARG_PARAM1 = "product"

class ProductFragment : Fragment() {
    private var product: Product? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            product = it.getSerializable(ARG_PARAM1) as Product
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_product, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val image = "https://www.rain-del-queen.co.za/images/homePage/roboclean.png"
        fragment_product_title.text = product!!.matnrName
        fragment_product_recommender.text = "${product!!.customerFirstName} ${product!!.customerLastName} ${product!!.customerMiddleName}"
        fragment_product_purchaseDate.text = dateFormatter(product!!.contractDate)
        fragment_product_serviceDate.text = dateFormatter(product!!.lastServiceDate)
        fragment_product_payment.text = product!!.price.toString()
        fragment_product_paymentTill.text = product!!.nextPaymentDate

        if((product!!.price - product!!.paid) == 0.toDouble()){
            payment_amount_label.visibility = View.INVISIBLE
            fragment_product_payment.visibility = View.INVISIBLE
            next_payment_date_label.visibility = View.INVISIBLE
            fragment_product_paymentTill.visibility = View.INVISIBLE
        }

        fragment_product_paymentschedule_btn.setOnClickListener {
            val intent = Intent(this.requireContext(), PaymentScheduleActivity::class.java)
            intent.putExtra("product", product)
            startActivity(intent)
        }

        Picasso.get()
            .load(image)
            .into(fragment_product_image)
    }

    companion object {
        @JvmStatic
        fun newInstance(product: Product) =
            ProductFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_PARAM1, product)
                }
            }
    }
}