package kz.aura.merp.employee.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_splash.view.*
import kz.aura.merp.employee.R
import kz.aura.merp.employee.util.Helpers.definePosition
import kz.aura.merp.employee.util.Helpers.getPositionId
import kz.aura.merp.employee.util.Helpers.getToken
import kz.aura.merp.employee.util.StaffPosition

class SplashFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (getToken(requireContext())?.isNotBlank() == true) {
            // Passcode
        } else {
            openFragment(R.id.authorizationFragment)
        }

        return inflater.inflate(R.layout.fragment_splash, container, false)
    }

    private fun openFragment(id: Int) = findNavController().navigate(R.id.action_splashFragment_to_passCodeFragment)
}