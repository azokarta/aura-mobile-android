package kz.aura.merp.employee.fragment.dealer

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import kotlinx.android.synthetic.main.fragment_demo.view.*
import kz.aura.merp.employee.R
import kz.aura.merp.employee.data.viewmodel.DealerViewModel
import kz.aura.merp.employee.data.viewmodel.ReferenceViewModel
import kz.aura.merp.employee.util.Helpers
import kz.aura.merp.employee.util.TabLayoutFragmentAdapter

class DemoFragment : Fragment() {

//    private val args: DemoFragmentArgs by navArgs()

    private val mDealerViewModel: DealerViewModel by activityViewModels()
    private val mReferenceViewModel: ReferenceViewModel by activityViewModels()
    private lateinit var mView: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mView = inflater.inflate(R.layout.fragment_demo, container, false)

        val titles = ArrayList<String>()
        val fragments = ArrayList<Fragment>()

        titles.add(getString(R.string.demoData))
        titles.add(getString(R.string.businessProcesses))

//        fragments.add(DemoDataFragment.newInstance(args.demo))
//        fragments.add(DemoBusinessProcessesFragment.newInstance(args.demo))

        val fragmentAdapter = TabLayoutFragmentAdapter(childFragmentManager, fragments, titles)
        mView.demo_view_pager.adapter = fragmentAdapter
        mView.demo_tab_layout.setupWithViewPager(mView.demo_view_pager)

        // Errors
        mDealerViewModel.error.observe(viewLifecycleOwner, Observer { error ->
            Helpers.exceptionHandler(error, requireContext())
        })
        mReferenceViewModel.error.observe(viewLifecycleOwner, Observer { error ->
            Helpers.exceptionHandler(error, requireContext())
        })

        return mView
    }

}