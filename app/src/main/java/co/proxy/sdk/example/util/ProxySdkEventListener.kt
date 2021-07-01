package co.proxy.sdk.example.util

import android.content.Context
import co.proxy.sdk.api.ProxyEvent
import co.proxy.sdk.util.ProxyEventListener
import timber.log.Timber

class ProxySdkEventListener(
    private var context: Context? = null
) : ProxyEventListener {

    override fun onAuthRevokeEvent(proxyEvent: ProxyEvent?) {
        Timber.i("onAuthRevokeEvent")
    }

    override fun onAuthRemovedEvent() {
        Timber.i("onAuthRemovedEvent")
    }

    override fun onCardUpdate(s: String?) {
        Timber.i("onCardUpdate")
    }

    override fun onInvalidateTokens(proxyEvent: ProxyEvent?) {
        Timber.i("onInvalidateTokens")
    }

    override fun onRefreshTokens(proxyEvent: ProxyEvent?) {
        Timber.i("onRefreshTokens")
    }
}