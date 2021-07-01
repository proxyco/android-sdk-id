package co.proxy.sdk.example.data.model

import co.proxy.sdk.api.Presence
import co.proxy.sdk.services.ProxyOperation
import java.util.*

sealed class ProxySampleEvent {

    val eventTime = Date()
    abstract val message: String

    data class PresenceEvent(
        val event: ProxyOperation.OperationEvent,
        val presence: Presence
    ) : ProxySampleEvent() {
        override val message: String
            get() = when(event) {
                ProxyOperation.OperationEvent.CHANGE_RANGE -> "Presence range updated: ${presence.name()} - (${presence.rssi})"
                ProxyOperation.OperationEvent.ENTER_RANGE -> "Presence discovered: ${presence.name()}"
                ProxyOperation.OperationEvent.EXIT_RANGE -> "Presence (${presence.name()}) lost"
                ProxyOperation.OperationEvent.AUTO_UNLOCK_TOKEN_WRITTEN,
                ProxyOperation.OperationEvent.UNLOCK_TOKEN_WRITTEN,
                ProxyOperation.OperationEvent.TEST,
                ProxyOperation.OperationEvent.UNKNOWN -> ""
                else->""
            }
    }

    class AdvertiserEnabled: ProxySampleEvent() {
        override val message: String
            get() = "Advertiser enabled"
    }

    data class AdvertiserStarted(val isSuccess: Boolean): ProxySampleEvent() {
        override val message: String
            get() = "Advertiser started: $isSuccess"
    }

    data class AdvertiserError(val errorCode: Int): ProxySampleEvent() {
        override val message: String
            get() = "Error starting Advertiser: code($errorCode)"
    }

    class ScannerEnabled: ProxySampleEvent() {
        override val message: String
            get() = "Scanner enabled"
    }

    data class ScannerError(val errorCode: Int): ProxySampleEvent() {
        override val message: String
            get() = "Error in Scanner: code(${errorCode})"
    }

    data class ScannerStarted(val isSuccess: Boolean): ProxySampleEvent() {
        override val message: String
            get() = "Scanner started: $isSuccess"
    }
}