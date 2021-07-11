
package kz.aura.merp.employee.ui.activity

import android.os.Bundle
import androidx.activity.viewModels
import dagger.hilt.android.AndroidEntryPoint
import kz.aura.merp.employee.databinding.ActivitySplashBinding
import kz.aura.merp.employee.util.*
import kz.aura.merp.employee.viewmodel.AuthViewModel

@AndroidEntryPoint
class SplashActivity : BaseActivity() {

    private lateinit var binding: ActivitySplashBinding
    private val mAuthViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAuthViewModel.salary.observe(this, { salary ->
            binding.logo.alpha = 0f
            binding.logo.animate().setDuration(1500).alpha(1f).withEndAction {
                if (!getToken(this).isNullOrBlank() && salary != null) {
                    clearPreviousAndOpenActivity(this, VerifyPasscodeActivity())
                } else {
                    clearPreviousAndOpenActivity(this, AuthorizationActivity())
                }
            }
        })

        mAuthViewModel.getSalary()
    }

}