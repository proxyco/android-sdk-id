package co.proxy.sdk.example.ui.user

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.proxy.sdk.api.User
import co.proxy.sdk.example.data.user.UserRepository
import co.proxy.sdk.example.ui.util.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _user = MutableLiveData<User>()
    val user: LiveData<User> get() = _user

    private val _fetchStatus = MutableLiveData<Event<FetchStatus>>()
    val fetchStatus: LiveData<Event<FetchStatus>> get() = _fetchStatus

    init {
        onFetchAccessCards()
    }

    fun onFetchAccessCards() {
        viewModelScope.launch {
            _fetchStatus.value = Event(FetchStatus.Fetching)
            val user = userRepository.fetchRemoteUserWithLegacyCard()
            if (user != null) {
                _user.value = user
                _fetchStatus.value = Event(FetchStatus.FetchSuccess)
            } else {
                _fetchStatus.value = Event(FetchStatus.FetchFail)
            }
        }
    }

    sealed class FetchStatus {
        object Fetching: FetchStatus()
        object FetchSuccess: FetchStatus()
        object FetchFail: FetchStatus()
    }
}