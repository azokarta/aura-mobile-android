package kz.aura.merp.customer.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.aura.R
import kz.aura.merp.customer.activities.MainActivity
import kz.aura.merp.customer.adapters.BonusAdapter
import kz.aura.merp.customer.models.Bonus
import kz.aura.merp.customer.presenters.BonusPresenter
import kz.aura.merp.customer.presenters.IBonusPresenter
import kz.aura.merp.customer.utils.Constants
import kz.aura.merp.customer.utils.Helpers.decimalFormatter
import kz.aura.merp.customer.views.IBonusView
import kotlinx.android.synthetic.main.fragment_bonuses.*

class BonusesFragment : Fragment(), IBonusView {

    private lateinit var bonusPresenter: IBonusPresenter
    private val bonusList = arrayListOf<Bonus>()
    private val bonusAdapter = BonusAdapter(bonusList)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bonuses, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (activity as MainActivity).supportActionBar?.title = getString(R.string.bonuses)
        bonusPresenter = BonusPresenter(this, this.requireContext())
        bonusPresenter.getBonuses(Constants.CUSTOMER_ID)
        hideProgressBar(false)

        bonuses_recycler_view.layoutManager = LinearLayoutManager(this.context)
        bonuses_recycler_view.isNestedScrollingEnabled = false;
        bonuses_recycler_view.adapter = bonusAdapter
    }

    override fun onSuccess(bonuses: ArrayList<Bonus>) {
        bonusList.addAll(bonuses)
        bonusAdapter.notifyDataSetChanged()
        initTotal(bonuses)
    }

    override fun onError(error: Any) {
        hideProgressBar(true)
    }

    private fun initTotal(bonuses: ArrayList<Bonus>) {
        val confirmedBonuses = bonuses.filter { it.confirmedByCustomer == 1 }
        var total = 0.0
        confirmedBonuses.forEach {
            if (it.drcrk == "H") {
                total -= it.amount
            } else {
                total += it.amount
            }
        }
        bonust_total.text = decimalFormatter(total) + " KZT"
        hideProgressBar(true)
    }

    private fun hideProgressBar(visibility: Boolean) {
        if (visibility) {
            bonus_progress_bar.visibility = View.INVISIBLE
            bonuses_recycler_view.visibility = View.VISIBLE
        } else {
            bonus_progress_bar.visibility = View.VISIBLE
            bonuses_recycler_view.visibility = View.INVISIBLE
        }
    }
}