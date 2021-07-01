package co.proxy.sdk.example.ui.manualUnlock

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import co.proxy.sdk.api.Presence
import co.proxy.sdk.example.R
import co.proxy.sdk.example.databinding.PresenceRowBinding
import co.proxy.sdk.example.ui.extensions.inflater

class PresenceAdapter(
    private val onPresenceClickListener: (Presence) -> Unit
) : ListAdapter<Presence, PresenceAdapter.PresenceViewHolder>(diffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PresenceViewHolder {
        return PresenceViewHolder(PresenceRowBinding.inflate(parent.inflater(), parent, false))
    }

    override fun onBindViewHolder(holder: PresenceViewHolder, position: Int) {
        holder.bind(getItem(position))
    }


    inner class PresenceViewHolder(private val view: PresenceRowBinding) : RecyclerView.ViewHolder(view.root) {
        fun bind(presence: Presence) {
            with(view) {
                tvPresenceId.text = presence.id
                tvPresenceName.text = presence.name()
                view.root.setOnClickListener { onPresenceClickListener(presence) }
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

    companion object {
        internal val diffCallback = object : DiffUtil.ItemCallback<Presence>() {
            override fun areItemsTheSame(oldItem: Presence, newItem: Presence) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Presence, newItem: Presence) =
                oldItem.id == newItem.id
                        && oldItem.name() == newItem.name()
                        && newItem.rssi.toFloat() == oldItem.rssi.toFloat()

        }
    }
}