package co.proxy.sdk.example.datasource.sdk

import co.proxy.sdk.api.Presence
import co.proxy.sdk.example.data.model.ProxySampleEvent
import co.proxy.sdk.services.BaseService
import co.proxy.sdk.services.BleScannerServiceListener
import co.proxy.sdk.services.ProxyOperation

class BleScannerServiceListenerAdapter(
    private val onEventDetected: (ProxySampleEvent) -> Unit
) : BleScannerServiceListener {

    override fun onScannerStatus(status: Int, errorCode: Int) {
        if (status == BaseService.STATUS_ENABLED) {
            onEventDetected(ProxySampleEvent.ScannerEnabled())
        } else {
            onEventDetected(ProxySampleEvent.ScannerError(errorCode))
        }
    }

    override fun onPresence(presence: Presence?) {
        presence?.let {
            onEventDetected(ProxySampleEvent.PresenceEvent(ProxyOperation.OperationEvent.ENTER_RANGE, it))
        }
    }

    override fun onPresenceList(presenceList: MutableList<Presence>?) {
        presenceList?.let {
            it.forEach {
                onEventDetected(ProxySampleEvent.PresenceEvent(ProxyOperation.OperationEvent.ENTER_RANGE, it))
            }
        }
    }

    override fun onStart(isSuccess: Boolean) {
        onEventDetected(ProxySampleEvent.ScannerStarted(isSuccess))
    }

    override fun onScannerError(p0: String?, p1: String?, p2: ProxyOperation.OperationErrorType?, errorCode: ProxyOperation.OperationErrorCode?, rawErrorCode: Int) {
        onEventDetected(ProxySampleEvent.ScannerError(rawErrorCode))
    }

    override fun onScannerEvent(p0: Presence?, p1: ProxyOperation.OperationEvent?, p2: String?) {
        p1?.let { event ->
            p0?.let {
                onEventDetected(ProxySampleEvent.PresenceEvent(event, it))
            }
        }
    }
}