package co.proxy.sdk.example.ui.main

import android.os.Bundle
import android.view.View
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import co.proxy.sdk.example.R
import co.proxy.sdk.example.databinding.FragmentMainBinding
import co.proxy.sdk.example.ui.BaseFragment
import co.proxy.sdk.example.ui.extensions.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.FlowPreview

@AndroidEntryPoint
class MainFragment : BaseFragment<FragmentMainBinding>() {

    override val binding: FragmentMainBinding by viewBinding { FragmentMainBinding.inflate(requireActivity().layoutInflater) }
    private val viewModel: MainViewModel by navGraphViewModels(R.id.nav_graph) { defaultViewModelProviderFactory }
    private val eventAdapter: SampleEventAdapter = SampleEventAdapter()

    @FlowPreview
    override fun attachObservers() {
        viewModel.event.observe(viewLifecycleOwner, {
            eventAdapter.submitList(it)
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            mainLogList.apply {
                (itemAnimator as? SimpleItemAnimator)?.supportsChangeAnimations = false
                layoutManager = LinearLayoutManager(this@MainFragment.requireContext())
                adapter = eventAdapter
            }
        }
    }
}