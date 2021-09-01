package co.proxy.sdk.example

import android.app.Application
import co.proxy.sdk.ProxySDK
import co.proxy.sdk.ProxySDKConfig
import co.proxy.sdk.ProxySDKEnv
import co.proxy.sdk.ProxySDKUITheme
import co.proxy.sdk.example.ui.MainActivity
import co.proxy.sdk.example.util.ProxySdkEventListener
import co.proxy.sdk.services.NotificationInfo
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class ProxySampleApp : Application() {

    override fun onCreate() {
        super.onCreate()

        var env = ProxySDKEnv.PROD
        if (BuildConfig.FLAVOR.contains(STAGING_BUILD_TYPE)) {
            env = ProxySDKEnv.STAGING
        }

        val uiTheme = ProxySDKUITheme.Builder()
            .setProxySDKUIThemeMode(ProxySDKUITheme.ProxySDKUIThemeMode.BRANDED)
            .setColor(R.color.colorPrimary)
            .build()

        val notificationInfo = NotificationInfo.Builder()
            .setSmallIcon(R.drawable.ic_proxy_logo)
            .setTitle(getString(R.string.app_name))
            .setText(getString(R.string.services_running))
            .setLauncherActivity(MainActivity::class.java)
            .setColor(R.color.colorPrimary)
            .setGeoFenceNotificationId(GEO_FENCE_NOTIFICATION_ID)
            .setForegroundServiceNotificationId(FOREGROUND_SERVICE_NOTIFICATION_ID)
            .setAppNotificationId(APP_NOTIFICATION_ID)
            .setNotificationChannelName(getString(R.string.app_name))
            .build()

        val config = ProxySDKConfig.Builder()
            .setAppId(BuildConfig.APPLICATION_ID)
            .setClientId("PUT YOUR CLIENT ID HERE")
            .setDefaultNotificationInfo(notificationInfo)
            .setFileLoggingEnabled(true)
            .setFileLogUploadingEnabled(true)
            .setFileLogUploadingDomains(getRemoteConfigDomains())
            .setFileLogUploadingUsers(getRemoteConfigUsers())
            .setDebugNotifications(true)
            .setUnlockAutoDisabled(false)
            .setEnv(env)
            .setUITheme(uiTheme)
            .build()
        ProxySDK.init(this, config)
        ProxySDK.getSignalEnabledSetting().set(true)

    }

    private fun getRemoteConfigDomains() = listOf("proxy.com", "your-domain.com")

    private fun getRemoteConfigUsers() = listOf("test@proxy.com", "specific-user@your-domain.com")

    companion object {
        const val STAGING_BUILD_TYPE = "staging"
        const val DEBUG_LOG_TAG = "PXLOG"

        const val GEO_FENCE_NOTIFICATION_ID = 9998
        const val FOREGROUND_SERVICE_NOTIFICATION_ID = 9997
        const val APP_NOTIFICATION_ID = 9996
    }
}