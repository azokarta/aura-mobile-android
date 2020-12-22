package kz.aura.merp.customer.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.aura.R
import kz.aura.merp.customer.activities.MainActivity
import kz.aura.merp.customer.adapters.ServicesAdapter
import kz.aura.merp.customer.models.Service
import kz.aura.merp.customer.presenters.IServicesPresenter
import kz.aura.merp.customer.presenters.ServicesPresenter
import kz.aura.merp.customer.utils.Constants
import kz.aura.merp.customer.views.IServicesView
import kotlinx.android.synthetic.main.fragment_services.*

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ServicesFragment : Fragment(), IServicesView {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private val serviceList = arrayListOf<Service>()
    private val servicesAdapter = ServicesAdapter(serviceList)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    private lateinit var servicePresenter: IServicesPresenter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_services, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        services_recycler_view.layoutManager = LinearLayoutManager(this.requireContext())
        services_recycler_view.adapter = servicesAdapter

        (activity as MainActivity).supportActionBar?.title = "Услуги"
        servicePresenter = ServicesPresenter(this, this.requireContext())
        servicePresenter.getAll(Constants.CUSTOMER_ID)
    }


    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ServicesFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onSuccessServices(services: ArrayList<Service>) {
        serviceList.addAll(services)
        servicesAdapter.notifyDataSetChanged()
        services_progress_bar.visibility = View.GONE
    }

    override fun onError(error: Any) {
        services_progress_bar.visibility = View.GONE
    }
}