package kz.aura.merp.employee.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_chief.view.*
import kz.aura.merp.employee.R
import kz.aura.merp.employee.util.Helpers
import kz.aura.merp.employee.util.StaffPosition

class ChiefFragment : Fragment() {

    private lateinit var mView: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_chief, container, false)

        mView.dealer.setOnClickListener { signInToAccount(StaffPosition.DEALER) }
        mView.master.setOnClickListener { signInToAccount(StaffPosition.MASTER) }
        mView.fin_agent.setOnClickListener { signInToAccount(StaffPosition.FIN_AGENT) }

        return mView
    }

    private fun signInToAccount(position: StaffPosition) {
        val staffId = mView.staffId.text.toString()
        if (staffId.isNotEmpty()) {
            Helpers.saveDataByKey(requireContext(), staffId.toLong(), "staffId")

            when (position) {
//                StaffPosition.DEALER -> openFragment(R.id.dealerFragment)
//                StaffPosition.MASTER -> openFragment(R.id.masterFragment)
//                StaffPosition.FIN_AGENT -> openFragment(R.id.financeFragment)
                else -> Toast.makeText(requireContext(), "Произошла ошибка повторите попытку позже", Toast.LENGTH_LONG).show()
            }
        } else {
            Toast.makeText(requireContext(), "Введите ID", Toast.LENGTH_LONG).show()
        }
    }

    private fun openFragment(id: Int) = findNavController().navigate(id)
}