package co.proxy.sdk.example.datasource.sdk

import android.content.Context
import android.nfc.NfcAdapter
import co.proxy.sdk.ProxySDK
import co.proxy.sdk.api.Command
import co.proxy.sdk.api.User
import co.proxy.sdk.api.http.ApiCallback
import co.proxy.sdk.example.data.model.ProxySampleEvent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import timber.log.Timber
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@Singleton
class ProxySDKDatasourceImp(
    private val context: Context
) : ProxySDKDatasource, ProxyBleServiceBinder {

    private val _bleEventsFlow: MutableStateFlow<ProxySampleEvent> = MutableStateFlow(ProxySampleEvent.AdvertiserStarted(false))

    @ExperimentalCoroutinesApi
    override suspend fun getPresenceFlow() = flow {
        _bleEventsFlow.collect { event ->
            when (event) {
                is ProxySampleEvent.PresenceEvent -> emit(event)
                else -> Unit
            }
        }
    }

    override suspend fun getEventsFlow() = flow {
        _bleEventsFlow.collect { event ->
            emit(event)
        }
    }

    override suspend fun isUserAuthenticated(): Boolean {
        return ProxySDK.isAuth()
    }

    override suspend fun manualUnlockReader(presenceId: String) {
        ProxySDK.sendPresenceCommand(presenceId, Command.COMMAND_UNLOCK_MANUAL)
    }

    override suspend fun isSignalEnabled(): Boolean {
        return ProxySDK.getSignalEnabledSetting().get()
    }

    override suspend fun doLogout() {
        ProxySDK.stopBleServices(context)
        ProxySDK.stopGeoFencesService()
        unbindServices()
        stopServices()
        ProxySDK.invalidateCaches()
        ProxySDK.revokeToken(object : ApiCallback<Void?> {
            override fun onSuccess(response: Void?) {
                Timber.d("Token successfully revoked")
            }

            override fun onFailure(t: Throwable) {
                Timber.e(Exception(t), "Failed to revoke token: %s", t.message)
            }
        })
    }

    override suspend fun isScannerRunning(): Boolean {
        return ProxySDK.isBleScannerRunning(context)
    }

    override suspend fun isAdvertiserEnabled(): Boolean {
        return ProxySDK.isBleAdvertiserRunning(context)
    }

    override suspend fun isNFCEnabled(): Boolean {
        return NfcAdapter.getDefaultAdapter(context)?.isEnabled ?: false
    }

    override fun startServices() {
        ProxySDK.startBleServices(context, true, BleAdvertiserServiceListenerAdapter {
            _bleEventsFlow.value = it
        }, BleScannerServiceListenerAdapter {
            _bleEventsFlow.value = it
        })
    }

    override fun unbindServices() {
        ProxySDK.unbindBleServices(context)
    }

    override fun stopServices() {
        ProxySDK.stopBleServices(context)
    }

    override suspend fun getUserWithLegacyCard(): SdkResult<User, Throwable> {
        return try {
            suspendCoroutine { continuation ->
                ProxySDK.getUserWithLegacyCard(object : ApiCallback<User> {
                    override fun onSuccess(user: User) {
                        continuation.resume(SdkResult.Success(user))
                    }

                    override fun onFailure(t: Throwable) {
                        continuation.resume(SdkResult.Error(t))
                    }
                })
            }
        } catch (e: Exception) {
            SdkResult.Error(e)
        }
    }
}

interface ProxySDKDatasource {
    suspend fun getPresenceFlow(): Flow<ProxySampleEvent.PresenceEvent>
    suspend fun getEventsFlow(): Flow<ProxySampleEvent>
    suspend fun isUserAuthenticated(): Boolean
    suspend fun manualUnlockReader(presenceId: String)
    suspend fun isSignalEnabled(): Boolean
    suspend fun doLogout()
    suspend fun isScannerRunning(): Boolean
    suspend fun isAdvertiserEnabled(): Boolean
    suspend fun isNFCEnabled(): Boolean
    suspend fun getUserWithLegacyCard(): SdkResult<User, Throwable>
}

interface ProxyBleServiceBinder {
    fun startServices()
    fun unbindServices()
    fun stopServices()
}

sealed class SdkResult<T : Any, E : Throwable> {
    data class Success<T : Any, E : Throwable>(val value: T) : SdkResult<T, E>()
    data class Error<T : Any, E : Throwable>(val e: E) : SdkResult<T, E>()
}