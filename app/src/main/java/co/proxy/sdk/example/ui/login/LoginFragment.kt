package co.proxy.sdk.example.ui.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import co.proxy.sdk.example.R
import co.proxy.sdk.example.databinding.FragmentLoginBinding
import co.proxy.sdk.example.ui.BaseFragment
import co.proxy.sdk.example.ui.extensions.viewBinding
import co.proxy.sdk.example.ui.extensions.visible
import co.proxy.sdk.ui.AuthActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.coroutines.ExperimentalCoroutinesApi

@AndroidEntryPoint
class LoginFragment : BaseFragment<FragmentLoginBinding>() {

    @ExperimentalCoroutinesApi
    private val viewModel by viewModels<LoginViewModel>()
    override val binding by viewBinding { FragmentLoginBinding.inflate(requireActivity().layoutInflater) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            emailSignInButton.setOnClickListener {
                viewModel.onLoginClick(email.text.toString())
            }
        }
    }

    @ExperimentalCoroutinesApi
    override fun attachObservers() {
        viewModel.loginState.observe(viewLifecycleOwner, { state ->
            with(binding) {
                when (state) {
                    is LoginUiState.Progress -> loginProgress.visible()
                    is LoginUiState.Error -> {

                    }
                    is LoginUiState.Success -> {
                        if (state.isLoggedIn) {
                            goToMain()
                        }
                    }
                }
            }
        })
        viewModel.tryToLogin.observe(viewLifecycleOwner, { tryToLoginEvent ->
            if (!tryToLoginEvent.consumed) {
                tryToLogin(tryToLoginEvent.peek())
            }
        })
    }

    private fun goToMain() {
        navigateTo(LoginFragmentDirections.actionLoginFragmentToMainFragment())
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == LOGIN_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            goToMain()
        }
    }

    private fun tryToLogin(email: String) {
        Intent(requireContext(), AuthActivity::class.java).apply {
            putExtra(AuthActivity.EMAIL_EXTRA, email)
            putExtra(AuthActivity.APP_NAME_EXTRA, getString(R.string.app_name))
            putExtra(AuthActivity.APP_ICON_EXTRA, R.mipmap.ic_launcher)
            putExtra(AuthActivity.INCLUDE_BG_LOCATION_PERMISSION, false)
            startActivityForResult(this, LOGIN_REQUEST_CODE)
        }
    }

    companion object {
        private const val LOGIN_REQUEST_CODE = 9997
    }
}