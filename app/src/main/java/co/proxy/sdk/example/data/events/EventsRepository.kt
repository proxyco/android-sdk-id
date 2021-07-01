package co.proxy.sdk.example.data.events

import co.proxy.sdk.example.data.model.ProxySampleEvent
import co.proxy.sdk.example.datasource.sdk.ProxySDKDatasource
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EventsRepositoryImp @Inject constructor(
    private val proxySDKDatasource: ProxySDKDatasource
) : EventsRepository {

    @InternalCoroutinesApi
    override suspend fun getEventsFlow() = proxySDKDatasource.getEventsFlow()
}

interface EventsRepository {
    suspend fun getEventsFlow(): Flow<ProxySampleEvent>
}
