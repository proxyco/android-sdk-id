package co.proxy.sdk.example.data.configuration

import co.proxy.sdk.example.datasource.sdk.ProxySDKDatasource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConfigurationRepositoryImp @Inject constructor(
    private val proxySDKDatasource: ProxySDKDatasource
): ConfigurationRepository {

    override suspend fun isSignalStateEnabled(): Boolean {
        return proxySDKDatasource.isSignalEnabled()
    }

    override suspend fun isScannerRunning(): Boolean {
        return proxySDKDatasource.isScannerRunning()
    }

    override suspend fun isAdvertiserEnabled(): Boolean {
        return proxySDKDatasource.isAdvertiserEnabled()
    }

    override suspend fun isNFCEnabled(): Boolean {
        return proxySDKDatasource.isNFCEnabled()
    }
}

interface ConfigurationRepository {
    suspend fun isSignalStateEnabled(): Boolean
    suspend fun isScannerRunning(): Boolean
    suspend fun isAdvertiserEnabled(): Boolean
    suspend fun isNFCEnabled(): Boolean
}
