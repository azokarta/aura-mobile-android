package kz.aura.merp.customer.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.aura.R
import kz.aura.merp.customer.activity.MainActivity
import kz.aura.merp.customer.adapter.ProductsAdapter
import kz.aura.merp.customer.data.model.Product
import kz.aura.merp.customer.presenters.IProductsPresenter
import kz.aura.merp.customer.presenters.ProductsPresenter
import kz.aura.merp.customer.util.Constants
import kz.aura.merp.customer.views.IProductsView
import kotlinx.android.synthetic.main.fragment_products.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProductsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProductsFragment : Fragment(), IProductsView {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var productsPresenter: IProductsPresenter
    private val productList = arrayListOf<Product>()
    private val productsAdapter = ProductsAdapter(productList)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_products, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (activity as MainActivity).supportActionBar?.title = "Товары"

        products_recycler_view.layoutManager = LinearLayoutManager(this.requireContext())
        products_recycler_view.adapter = productsAdapter

        productsPresenter = ProductsPresenter(this, this.requireContext())
        productsPresenter.getAll(Constants.CUSTOMER_ID)
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProductsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onSuccessProducts(products: ArrayList<Product>) {
        products_progress_bar.visibility = View.GONE
        productList.addAll(products)
        productsAdapter.notifyDataSetChanged()
    }

    override fun onError(error: Any) {
        products_progress_bar.visibility = View.GONE
    }

}