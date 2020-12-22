package kz.aura.merp.customer.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.aura.R
import kz.aura.merp.customer.activities.MainActivity
import kz.aura.merp.customer.utils.TabLayoutFragmentAdapter
import kotlinx.android.synthetic.main.fragment_payments.*

class PaymentsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_payments, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (activity as MainActivity).supportActionBar?.title = "Платежи"


        val titles = ArrayList<String>()
        val fragments = ArrayList<Fragment>()

        titles.add("Все платежи")
        titles.add("История")

        fragments.add(AllPaymentsFragment())
        fragments.add(PaymentsHistoryFragment())

        val fragmentAdapter = TabLayoutFragmentAdapter(childFragmentManager, fragments, titles)
        viewPager.adapter = fragmentAdapter
        tabLayout.setupWithViewPager(viewPager)
    }

}