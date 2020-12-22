package kz.aura.merp.employee.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import kz.aura.merp.employee.R
import kz.aura.merp.employee.adapter.DemoRecomandationAdapter
import kz.aura.merp.employee.data.model.Recomandation
import kotlinx.android.synthetic.main.fragment_demo_recomandation.*

class RecomandationFragment : Fragment() {

    private val recomandations = arrayListOf(Recomandation("", "", false))
    private val rAdapter = DemoRecomandationAdapter(recomandations)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_demo_recomandation, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        demo_recomandation_recycler_view.layoutManager = LinearLayoutManager(this.requireContext())
        demo_recomandation_recycler_view.adapter = rAdapter

      /*  recomandation_remove_btn.setOnClickListener {
            recomandations.add(Recomandation("",""))
            rAdapter.notifyDataSetChanged()
        }*/

        // rAdapter.recomandations
    }
}