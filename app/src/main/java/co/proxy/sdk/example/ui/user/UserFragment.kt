package co.proxy.sdk.example.ui.user

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import co.proxy.sdk.api.User
import co.proxy.sdk.example.R
import co.proxy.sdk.example.databinding.FragmentUserBinding
import co.proxy.sdk.example.ui.BaseFragment
import co.proxy.sdk.example.ui.extensions.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_user.*


@AndroidEntryPoint
class UserFragment : BaseFragment<FragmentUserBinding>() {

    private val viewModel: UserViewModel by viewModels()
    override val binding: FragmentUserBinding by viewBinding { FragmentUserBinding.inflate(requireActivity().layoutInflater) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.fetchButton.setOnClickListener { viewModel.onFetchAccessCards() }
    }

    override fun attachObservers() {
        viewModel.user.observe(viewLifecycleOwner, { user -> populateUserInformation(user) })

        viewModel.fetchStatus.observe(viewLifecycleOwner, { fetchStatusEvent ->
            when (fetchStatusEvent.consume()) {
                UserViewModel.FetchStatus.Fetching -> showFetchStatusToast(R.string.fetch_user_toast)
                UserViewModel.FetchStatus.FetchSuccess -> showFetchStatusToast(R.string.fetch_user_toast_success)
                UserViewModel.FetchStatus.FetchFail -> showFetchStatusToast(R.string.fetch_user_toast_failure)
            }
        })
    }

    private fun showFetchStatusToast(message: Int) {
        Toast.makeText(requireContext(), getString(message), Toast.LENGTH_SHORT).also { it.show() }
    }

    private fun populateUserInformation(user: User) {
        with(binding) {
            information_name.text = user.name?.toString()
            informationId.text = user.id
            informationCardSize.text = user.cards.size.toString()
            informationEmail.text = user.email
        }
    }
}