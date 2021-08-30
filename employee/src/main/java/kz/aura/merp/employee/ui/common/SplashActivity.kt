package kz.aura.merp.employee.ui.common

import android.os.Bundle
import androidx.activity.viewModels
import dagger.hilt.android.AndroidEntryPoint
import kz.aura.merp.employee.base.BaseActivity
import kz.aura.merp.employee.databinding.ActivitySplashBinding
import kz.aura.merp.employee.util.*
import kz.aura.merp.employee.viewmodel.AuthViewModel

@AndroidEntryPoint
class SplashActivity : BaseActivity() {

    private lateinit var binding: ActivitySplashBinding

    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val salary = authViewModel.preferences.salary
        val token = authViewModel.preferences.token

        binding.logo.alpha = 0f
        binding.logo.animate().setDuration(1500).alpha(1f).withEndAction {
            if (!token.isNullOrBlank() && salary != null) {
                navigateToActivity(VerifyPasscodeActivity::class, true)
            } else {
                navigateToActivity(AuthActivity::class, true)
            }
        }
    }

}