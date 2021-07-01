package co.proxy.sdk.example.ui.main

import androidx.lifecycle.*
import co.proxy.sdk.example.data.events.EventsRepository
import co.proxy.sdk.example.data.model.ProxySampleEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@FlowPreview
@HiltViewModel
class MainViewModel @Inject constructor(
    private val eventsRepository: EventsRepository
) : ViewModel() {
    private val eventList = mutableListOf<ProxySampleEvent>()
    private val _event = MutableLiveData<List<ProxySampleEvent>>()
    val event: LiveData<List<ProxySampleEvent>>
        get() = _event

    init {
        viewModelScope.launch {
            eventsRepository.getEventsFlow()
                .flowOn(Dispatchers.IO)
                .collect { event ->
                    if(event is ProxySampleEvent.PresenceEvent && isPresenceEventAlreadyInList(event)) {
                        updateSignalStrength(event)
                    } else {
                        eventList.add(event)
                    }
                    _event.value = eventList.toList()
            }
        }
    }

    private fun updateSignalStrength(event: ProxySampleEvent.PresenceEvent) {
        val position = eventList.indexOf(event)
        if(position != -1) {
            eventList.removeAt(position)
            eventList.add(position, event)
        }
    }

    private fun isPresenceEventAlreadyInList(event: ProxySampleEvent.PresenceEvent): Boolean {
        return (event as? ProxySampleEvent.PresenceEvent)?.presence?.id?.let { presenceId ->
            eventList.any { (it as? ProxySampleEvent.PresenceEvent)?.presence?.id == presenceId }
        } ?: false
    }
}