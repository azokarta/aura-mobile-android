package kz.aura.merp.employee.fragment.finance

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import kotlinx.android.synthetic.main.fragment_client.view.*
import kz.aura.merp.employee.R
import kz.aura.merp.employee.util.TabLayoutFragmentAdapter


class ClientFragment : Fragment() {

    private val args: ClientFragmentArgs by navArgs()

    private lateinit var mView: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_client, container, false)

        val titles = ArrayList<String>()
        val fragments = ArrayList<Fragment>()

        titles.add(getString(R.string.info))
        titles.add(getString(R.string.businessProcesses))

        fragments.add(FinanceClientInfoFragment.newInstance(args.client))
        fragments.add(FinanceBusinessProcessFragment.newInstance(args.client))

        val fragmentAdapter = TabLayoutFragmentAdapter(childFragmentManager, fragments, titles)
        mView.fin_view_pager.adapter = fragmentAdapter
        mView.fin_agent_tabLayout.setupWithViewPager(mView.fin_view_pager)
        return mView
    }
}