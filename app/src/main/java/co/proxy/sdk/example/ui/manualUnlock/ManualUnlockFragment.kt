package co.proxy.sdk.example.ui.manualUnlock

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import co.proxy.sdk.api.Presence
import co.proxy.sdk.example.R
import co.proxy.sdk.example.databinding.FragmentManualUnlockBinding
import co.proxy.sdk.example.ui.BaseFragment
import co.proxy.sdk.example.ui.extensions.viewBinding
import co.proxy.sdk.example.ui.util.Event
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ManualUnlockFragment : BaseFragment<FragmentManualUnlockBinding>() {

    private val viewModel: ManualUnlockViewModel by navGraphViewModels(R.id.nav_graph) { defaultViewModelProviderFactory }
    override val binding: FragmentManualUnlockBinding by viewBinding { FragmentManualUnlockBinding.inflate(requireActivity().layoutInflater) }
    private val presenceAdapter: PresenceAdapter by lazy { PresenceAdapter(viewModel::onPresenceClick) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.readerList.apply {
            (itemAnimator as? SimpleItemAnimator)?.supportsChangeAnimations = false
            layoutManager = LinearLayoutManager(requireContext())
            adapter = presenceAdapter
            addItemDecoration(DividerItemDecoration(requireContext(), LinearLayout.VERTICAL))
        }
    }

    override fun attachObservers() {
        viewModel.uiState.observe(viewLifecycleOwner, { state ->
            when (state) {
                is ManualUnlockUiState.Progress -> Unit
                is ManualUnlockUiState.Error -> handleError(state.error)
                is ManualUnlockUiState.Success -> handleSuccess(state.readerList)
            }
        })
    }

    private fun handleSuccess(readerList: List<Presence>) {
        presenceAdapter.submitList(readerList)
    }

    private fun handleError(error: Event<ManualUnlockUiStateError>?) {

    }
}