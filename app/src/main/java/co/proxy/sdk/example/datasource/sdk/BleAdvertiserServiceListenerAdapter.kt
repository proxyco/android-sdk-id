package co.proxy.sdk.example.datasource.sdk

import co.proxy.sdk.example.data.model.ProxySampleEvent
import co.proxy.sdk.services.BaseService
import co.proxy.sdk.services.BleAdvertiserServiceListener

class BleAdvertiserServiceListenerAdapter(
    private val onEventDetected: (ProxySampleEvent) -> Unit
) : BleAdvertiserServiceListener {

    override fun onAdvertiserStatus(status: Int, errorCode: Int) {
        if (status == BaseService.STATUS_ENABLED) {
            onEventDetected(ProxySampleEvent.AdvertiserEnabled())
        } else {
            onEventDetected(ProxySampleEvent.AdvertiserError(errorCode))
        }
    }

    override fun onStart(isSuccess: Boolean) {
        onEventDetected(ProxySampleEvent.AdvertiserStarted(isSuccess))
    }
}