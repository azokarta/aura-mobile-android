package kz.aura.merp.employee.fragment.dealer

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import kz.aura.merp.employee.R
import kz.aura.merp.employee.adapter.DemoRecommendationAdapter
import kz.aura.merp.employee.databinding.FragmentDemoRecommendationBinding
import kz.aura.merp.employee.util.Helpers.showToast

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class DemoRecommendationFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var _binding: FragmentDemoRecommendationBinding? = null
    private val binding get() = _binding!!
    private val recommendationsAdapter: DemoRecommendationAdapter by lazy { DemoRecommendationAdapter() }

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
    ): View {
        _binding = FragmentDemoRecommendationBinding.inflate(inflater, container, false)

        setupRecyclerView()

        setHasOptionsMenu(true)

        binding.addRecommendation.setOnClickListener {
            recommendationsAdapter.addRecommendation()
        }

        return binding.root
    }

    private fun setupRecyclerView() {
        with(binding) {
            demoRecommendationsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
            demoRecommendationsRecyclerView.adapter = recommendationsAdapter
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.save -> {
                showToast(requireContext(), getString(R.string.recommendationsSaved))
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.save_menu, menu)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment DemoRecommendationFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            DemoRecommendationFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}