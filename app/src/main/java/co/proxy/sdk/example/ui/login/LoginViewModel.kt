package co.proxy.sdk.example.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.proxy.sdk.example.data.user.UserRepository
import co.proxy.sdk.example.ui.util.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class LoginViewModel @Inject constructor(
    userRepository: UserRepository
) : ViewModel() {

    private val _loginState = MutableLiveData<LoginUiState>()
    val loginState: LiveData<LoginUiState> get() = _loginState

    private val _tryToLogin = MutableLiveData<Event<String>>()
    val tryToLogin: LiveData<Event<String>> get() = _tryToLogin

    init {
        showLoading()
        viewModelScope.launch {
            emit(userRepository.isUserLoggedIn())
        }
    }

    fun onLoginClick(email: String) {
        showLoading()

        when {
            email.isEmpty() || email.isBlank() -> showError(LoginUiStateError.EMPTY_EMAIL)
            !isValidEmail(email) ->   showError(LoginUiStateError.INVALID_EMAIL)
            else -> tryToLogin(email)
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return email.contains("@")
    }

    private fun tryToLogin(email: String) {
        _tryToLogin.value = Event(email)
    }

    private fun emit(isLoggedIn: Boolean) {
        _loginState.value = LoginUiState.Success(isLoggedIn)
    }

    private fun showError(error: LoginUiStateError) {
        _loginState.value = LoginUiState.Error(Event(error))
    }

    private fun showLoading() {
        _loginState.value = LoginUiState.Progress
    }
}

sealed class LoginUiState {
    object Progress : LoginUiState()
    data class Error(val error: Event<LoginUiStateError>? = null): LoginUiState()
    data class Success(val isLoggedIn: Boolean): LoginUiState()
}

enum class LoginUiStateError {
    EMPTY_EMAIL,
    INVALID_EMAIL
}