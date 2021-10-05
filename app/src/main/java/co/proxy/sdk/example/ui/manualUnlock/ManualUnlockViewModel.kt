package co.proxy.sdk.example.ui.manualUnlock

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.proxy.sdk.api.Presence
import co.proxy.sdk.example.data.configuration.ConfigurationRepository
import co.proxy.sdk.example.data.reader.ReaderRepository
import co.proxy.sdk.example.ui.util.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ManualUnlockViewModel @Inject constructor(
    private val configurationRepository: ConfigurationRepository,
    private val readerRepository: ReaderRepository
) : ViewModel() {

    private val _uiState = MutableLiveData<ManualUnlockUiState>()
    val uiState: LiveData<ManualUnlockUiState> get() = _uiState

    init {
        showLoading()
        viewModelScope.launch {
            if(configurationRepository.isSignalStateEnabled()) {
                readerRepository.getPresenceListFlow()
                    .flowOn(Dispatchers.IO)
                    .collect {
                        emit(it.sortedBy { presence -> presence.name() })
                    }
            }
        }
    }

    fun onPresenceClick(presence: Presence) {
        viewModelScope.launch {
            readerRepository.manualUnlockReader(presence.target.id)
        }
    }

    private fun emit(presenceList: List<Presence>) {
        _uiState.value = ManualUnlockUiState.Success(presenceList)
    }

    private fun showError(error: ManualUnlockUiStateError) {
        _uiState.value = ManualUnlockUiState.Error(Event(error))
    }

    private fun showLoading() {
        _uiState.value = ManualUnlockUiState.Progress
    }

}

sealed class ManualUnlockUiState {
    object Progress : ManualUnlockUiState()
    data class Error(val error: Event<ManualUnlockUiStateError>? = null): ManualUnlockUiState()
    data class Success(val readerList: List<Presence>): ManualUnlockUiState()
}

enum class ManualUnlockUiStateError {
}