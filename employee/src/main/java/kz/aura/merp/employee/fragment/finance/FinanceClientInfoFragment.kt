package kz.aura.merp.employee.fragment.finance

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import kz.aura.merp.employee.data.model.*
import kz.aura.merp.employee.data.viewmodel.FinanceViewModel
import kz.aura.merp.employee.databinding.FragmentFinanceClientInfoBinding
import kz.aura.merp.employee.R
import kz.aura.merp.employee.activity.MapActivity

private const val ARG_PARAM1 = "plan"

class FinanceClientInfoFragment : Fragment() {

    private var client: Client? = null
    private var _binding: FragmentFinanceClientInfoBinding? = null
    private val binding get() = _binding!!
    private val mFinanceViewModel: FinanceViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            client = it.getParcelable(ARG_PARAM1) as Client?
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Data binding
        _binding = FragmentFinanceClientInfoBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.client = client

        setHasOptionsMenu(true)

        // Observe MutableLiveData
        mFinanceViewModel.updatedClient.observe(viewLifecycleOwner, Observer { data ->
            client = data
            binding.client = data
            binding.executePendingBindings()
            Snackbar.make(binding.root, getString(R.string.successfullySaved), Snackbar.LENGTH_LONG).show()
        })

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.save_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.save -> {
                mFinanceViewModel.updateClient(client!!)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance(client: Client) =
            FinanceClientInfoFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_PARAM1, client)
                }
            }
    }
}