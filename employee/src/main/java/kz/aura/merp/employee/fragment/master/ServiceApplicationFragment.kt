package kz.aura.merp.employee.fragment.master

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import kotlinx.android.synthetic.main.fragment_service_application.view.*
import kz.aura.merp.employee.R
import kz.aura.merp.employee.data.model.ServiceApplication
import kz.aura.merp.employee.data.viewmodel.MasterViewModel
import kz.aura.merp.employee.data.viewmodel.ReferenceViewModel
import kz.aura.merp.employee.util.Helpers
import kz.aura.merp.employee.util.TabLayoutFragmentAdapter

class ServiceApplicationFragment : Fragment() {
    
    private val args: ServiceApplicationFragmentArgs by navArgs()

    private val mMasterViewModel: MasterViewModel by viewModels()
    private val mReferenceViewModel: ReferenceViewModel by viewModels()
    private lateinit var mView: View
    
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mView = inflater.inflate(R.layout.fragment_service_application, container, false)

        val titles = ArrayList<String>()
        val fragments = ArrayList<Fragment>()

        titles.add(getString(R.string.service))
        titles.add(getString(R.string.businessProcesses))

        fragments.add(ServiceApplicationDataFragment.newInstance(args.serviceApplication))
        fragments.add(ServiceApplicationBusinessFragment.newInstance(args.serviceApplication))

        val fragmentAdapter = TabLayoutFragmentAdapter(childFragmentManager, fragments, titles)
        mView.service_application_view_pager.adapter = fragmentAdapter
        mView.service_application_tab_layout.setupWithViewPager(mView.service_application_view_pager)

        // Errors
        mMasterViewModel.error.observe(viewLifecycleOwner, Observer { error ->
            Helpers.exceptionHandler(error, requireContext())
        })
        mReferenceViewModel.error.observe(viewLifecycleOwner, Observer { error ->
            Helpers.exceptionHandler(error, requireContext())
        })
        
        return mView
    }
}