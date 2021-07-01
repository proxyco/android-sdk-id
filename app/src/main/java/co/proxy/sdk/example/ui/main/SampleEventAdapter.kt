package co.proxy.sdk.example.ui.main

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import co.proxy.sdk.api.Presence
import co.proxy.sdk.example.R
import co.proxy.sdk.example.data.model.ProxySampleEvent
import co.proxy.sdk.example.databinding.EventRowBinding
import co.proxy.sdk.example.ui.extensions.gone
import co.proxy.sdk.example.ui.extensions.inflater
import co.proxy.sdk.example.ui.extensions.visible
import java.text.SimpleDateFormat

class SampleEventAdapter : ListAdapter<ProxySampleEvent, SampleEventAdapter.BaseEventViewHolder>(diffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseEventViewHolder {
        return when (EventRowType.values()[viewType]) {
            EventRowType.PRESENCEEVENT -> PresenceEventViewHolder(EventRowBinding.inflate(parent.inflater(), parent, false))
            EventRowType.OTHER -> EventViewHolder(EventRowBinding.inflate(parent.inflater(), parent, false))
        }
    }

    override fun getItemViewType(position: Int): Int {
        return (when (getItem(position)) {
            is ProxySampleEvent.PresenceEvent -> EventRowType.PRESENCEEVENT
            else -> EventRowType.OTHER
        }).ordinal
    }

    override fun onBindViewHolder(holder: BaseEventViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class PresenceEventViewHolder(private val view: EventRowBinding) : BaseEventViewHolder(view) {
        override fun bind(event: ProxySampleEvent) {
            view.ivEventRowImage.setImageResource(R.drawable.ic_proxy_device)
            view.ivEventRowImage.setBackgroundColor(ContextCompat.getColor(view.root.context, R.color.colorAccentLight))
            val presence = (event as ProxySampleEvent.PresenceEvent).presence
            with(view) {
                tvEventName.text = presence.name()
                tvEventDesc.text = presence.id
                with(cvSignal) {
                    ivSignalOne.visible()
                    ivSignalTwo.visible()
                    ivSignalThree.visible()
                    ivSignalFour.visible()
                }
                val currentRange = Presence.Range.fromRssi(presence.rssi.toFloat())
                when (currentRange) {
                    Presence.Range.FAR -> showLowSignal()
                    Presence.Range.NEAR -> showGoodSignal()
                    Presence.Range.IMMEDIATE -> showBestSignal()
                    else -> Unit
                }
            }
        }

        private fun showBestSignal() {
            view.cvSignal.ivSignalOne.setImageResource(R.drawable.bg_row_image_radius_green)
            view.cvSignal.ivSignalTwo.setImageResource(R.drawable.bg_row_image_radius_green)
            view.cvSignal.ivSignalThree.setImageResource(R.drawable.bg_row_image_radius_green)
            view.cvSignal.ivSignalFour.setImageResource(R.drawable.bg_row_image_radius_green)
        }

        private fun showGoodSignal() {
            view.cvSignal.ivSignalOne.setImageResource(R.drawable.bg_row_image_radius_green)
            view.cvSignal.ivSignalTwo.setImageResource(R.drawable.bg_row_image_radius_green)
            view.cvSignal.ivSignalThree.setImageResource(R.drawable.bg_row_image_radius_green)
            view.cvSignal.ivSignalFour.setImageResource(R.drawable.bg_row_image_radius_grey)
        }

        private fun showLowSignal() {
            view.cvSignal.ivSignalOne.setImageResource(R.drawable.bg_row_image_radius_green)
            view.cvSignal.ivSignalTwo.setImageResource(R.drawable.bg_row_image_radius_grey)
            view.cvSignal.ivSignalThree.setImageResource(R.drawable.bg_row_image_radius_grey)
            view.cvSignal.ivSignalFour.setImageResource(R.drawable.bg_row_image_radius_grey)
        }
    }

    inner class EventViewHolder(private val view: EventRowBinding) : BaseEventViewHolder(view) {

        @SuppressLint("SimpleDateFormat")
        override fun bind(event: ProxySampleEvent) {
            with(view) {
                ivEventRowImage.setImageResource(R.drawable.ic_bluetooth)
                tvEventName.text = event.message
                tvEventDesc.text = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(event.eventTime)
                with(cvSignal) {
                    ivSignalOne.gone()
                    ivSignalTwo.gone()
                    ivSignalThree.gone()
                    ivSignalFour.gone()
                }
            }
            when (event) {
                is ProxySampleEvent.ScannerEnabled,
                is ProxySampleEvent.AdvertiserEnabled -> view.ivEventRowImage.setBackgroundColor(ContextCompat.getColor(view.root.context, R.color.blue))
                is ProxySampleEvent.ScannerStarted,
                is ProxySampleEvent.AdvertiserStarted -> view.ivEventRowImage.setBackgroundColor(ContextCompat.getColor(view.root.context, R.color.accept_green))
                is ProxySampleEvent.AdvertiserError,
                is ProxySampleEvent.ScannerError -> view.ivEventRowImage.setBackgroundColor(ContextCompat.getColor(view.root.context, R.color.proxy_red))
                is ProxySampleEvent.PresenceEvent -> Unit
            }
        }
    }

    abstract class BaseEventViewHolder(view: ViewBinding) : RecyclerView.ViewHolder(view.root) {
        abstract fun bind(event: ProxySampleEvent)
    }

    companion object {
        internal val diffCallback = object : DiffUtil.ItemCallback<ProxySampleEvent>() {
            override fun areItemsTheSame(oldItem: ProxySampleEvent, newItem: ProxySampleEvent) = when {
                oldItem is ProxySampleEvent.PresenceEvent && newItem is ProxySampleEvent.PresenceEvent -> oldItem.presence.id == newItem.presence.id
                else -> oldItem.message == newItem.message && oldItem.eventTime == newItem.eventTime
            }

            override fun areContentsTheSame(oldItem: ProxySampleEvent, newItem: ProxySampleEvent) = when {
                oldItem is ProxySampleEvent.PresenceEvent && newItem is ProxySampleEvent.PresenceEvent ->
                    (oldItem as? ProxySampleEvent.PresenceEvent)?.presence?.rssi == (newItem as? ProxySampleEvent.PresenceEvent)?.presence?.rssi
                else -> oldItem.message == newItem.message && oldItem.eventTime == newItem.eventTime
            }
        }
    }

    enum class EventRowType {
        PRESENCEEVENT,
        OTHER
    }
}