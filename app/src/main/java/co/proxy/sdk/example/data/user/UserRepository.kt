package co.proxy.sdk.example.data.user

import co.proxy.sdk.example.datasource.sdk.ProxySDKDatasource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImp @Inject constructor(
    private val proxySDKDatasource: ProxySDKDatasource
): UserRepository {
    override suspend fun isUserLoggedIn(): Boolean {
        return proxySDKDatasource.isUserAuthenticated()
    }

    override suspend fun doLogout() {
        proxySDKDatasource.doLogout()
    }
}

interface UserRepository {
    suspend fun isUserLoggedIn(): Boolean
    suspend fun doLogout()
}
