package co.proxy.sdk.example.ui.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.proxy.sdk.ProxySDK
import co.proxy.sdk.example.data.configuration.ConfigurationRepository
import co.proxy.sdk.example.data.user.UserRepository
import co.proxy.sdk.example.ui.util.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val configurationRepository: ConfigurationRepository
) : ViewModel() {

    private val _navigation = MutableLiveData<Event<SettingsNavigationDestination>>()
    val navigation: LiveData<Event<SettingsNavigationDestination>> get() = _navigation

    fun onPermissionRequestClick() {
        _navigation.value = Event(SettingsNavigationDestination.Permissions)
    }

    fun onLogoutRequested() {
        viewModelScope.launch {
            userRepository.doLogout()
            _navigation.value = Event(SettingsNavigationDestination.Logout)
        }
    }

    fun onFeedbackRequested() {
        viewModelScope.launch(Dispatchers.IO) {
            val isLocationEnabled = ProxySDK.getRxBleClientState() == ProxySDK.RxBleClientState.LOCATION_SERVICES_NOT_ENABLED.name
            val bluetoothState = ProxySDK.getRxBleClientState()
            val isScanning = configurationRepository.isScannerRunning()
            val isAdvertiserEnabled = configurationRepository.isAdvertiserEnabled()
            val isNFCEnabled = configurationRepository.isNFCEnabled()
            _navigation.postValue(
                Event(
                    SettingsNavigationDestination.Feedback(
                        isLocationEnabled,
                        bluetoothState,
                        isScanning,
                        isAdvertiserEnabled,
                        isNFCEnabled
                    )
                )
            )
        }

    }

    sealed class SettingsNavigationDestination {
        object Permissions : SettingsNavigationDestination()
        object Logout : SettingsNavigationDestination()
        data class Feedback(
            val isLocationEnabled: Boolean,
            val bluetoothState: String,
            val isScanning: Boolean,
            val isAdvertiserEnabled: Boolean,
            val isNfcEnabled: Boolean
        ) : SettingsNavigationDestination()
    }
}
