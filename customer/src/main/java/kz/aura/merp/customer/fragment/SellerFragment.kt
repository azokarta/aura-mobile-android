package kz.aura.merp.customer.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kz.aura.merp.customer.data.model.Product
import kz.aura.merp.customer.util.Helpers.dateFormatter
import com.example.aura.R
import kotlinx.android.synthetic.main.fragment_seller.*


private const val ARG_PARAM1 = "product"

class SellerFragment : Fragment() {
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
        return inflater.inflate(R.layout.fragment_seller, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        fragment_seller_name.text = product!!.staffFirstName+" "+product!!.staffMiddleName+" "+product!!.staffLastName
        fragment_seller_number.text = product!!.staffPhone
        seller_birthday.text = dateFormatter(product!!.staffBirthday)
        fragment_seller_status.text = if (product!!.staffSacked == 0) "Работает" else "Уволен"
    }

    companion object {
        @JvmStatic
        fun newInstance(product: Product) =
            SellerFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_PARAM1, product)
                }
            }
    }
}