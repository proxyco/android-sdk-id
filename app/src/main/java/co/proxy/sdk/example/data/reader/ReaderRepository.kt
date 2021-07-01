package co.proxy.sdk.example.data.reader

import co.proxy.sdk.api.Presence
import co.proxy.sdk.example.datasource.sdk.ProxySDKDatasource
import co.proxy.sdk.services.ProxyOperation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReaderRepositoryImp @Inject constructor(
    private val proxySDKDatasource: ProxySDKDatasource
): ReaderRepository {

    private val presenceCache = mutableListOf<Presence>()

    override suspend fun getPresenceListFlow() = flow {
        proxySDKDatasource.getPresenceFlow().collect {
            when(it.event) {
                ProxyOperation.OperationEvent.CHANGE_RANGE,
                ProxyOperation.OperationEvent.ENTER_RANGE -> {
                    if(!isOnPresenceCacheList(it.presence)) {
                        presenceCache.add(it.presence)
                    } else {
                        presenceCache.remove(it.presence)
                        presenceCache.add(it.presence)
                    }
                }
                ProxyOperation.OperationEvent.EXIT_RANGE -> {
                    presenceCache.remove(it.presence)
                }
                else -> Unit
            }
            emit(presenceCache.toList())
        }
    }

    override suspend fun manualUnlockReader(presenceId: String) {
        proxySDKDatasource.manualUnlockReader(presenceId)
    }

    private fun isOnPresenceCacheList(presence: Presence): Boolean {
        return presenceCache.any {
            it.id == presence.id
                    && it.target.id == presence.target.id
                    && it.name() == presence.name()
        }
    }

}

interface ReaderRepository {
    suspend fun getPresenceListFlow(): Flow<List<Presence>>
    suspend fun manualUnlockReader(presenceId: String)
}
