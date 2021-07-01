package co.proxy.sdk.example.ui.settings

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.format.DateFormat
import android.view.View
import android.widget.Toast
import androidx.core.os.ConfigurationCompat
import androidx.fragment.app.viewModels
import co.proxy.sdk.ProxySDK
import co.proxy.sdk.example.BuildConfig
import co.proxy.sdk.example.R
import co.proxy.sdk.example.databinding.SettingsFragmentBinding
import co.proxy.sdk.example.ui.BaseFragment
import co.proxy.sdk.example.ui.extensions.viewBinding
import co.proxy.sdk.example.ui.settings.SettingsViewModel.SettingsNavigationDestination.*
import co.proxy.sdk.ui.AuthActivity
import co.proxy.sdk.util.EmailUtil
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.util.*

@AndroidEntryPoint
class SettingsFragment : BaseFragment<SettingsFragmentBinding>() {

    override val binding: SettingsFragmentBinding by viewBinding { SettingsFragmentBinding.inflate(layoutInflater) }
    private val viewModel: SettingsViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            permissionRowLayout.tvSettingsRowTitle.text = getString(R.string.permissions_row_title)
            permissionRowLayout.tvSettingsRowDesc.text = getString(R.string.permissions_row_desc)
            permissionRowLayout.ivRowIcon.setImageResource(R.drawable.ic_lock_open_24)

            permissionRowLayout.settingsRowRootLayout.setOnClickListener {
                viewModel.onPermissionRequestClick()
            }

            feedbackRowLayout.tvSettingsRowTitle.text = getString(R.string.feedback_row_title)
            feedbackRowLayout.tvSettingsRowDesc.text = getString(R.string.feedback_row_desc)
            feedbackRowLayout.ivRowIcon.setImageResource(R.drawable.ic_feedback_24)

            feedbackRowLayout.settingsRowRootLayout.setOnClickListener {
                viewModel.onFeedbackRequested()
            }

            logoutRowLayout.tvSettingsRowTitle.text = getString(R.string.logout_row_title)
            logoutRowLayout.tvSettingsRowDesc.text = getString(R.string.logout_row_desc)
            logoutRowLayout.ivRowIcon.setImageResource(R.drawable.ic_exit_to_app_24)

            logoutRowLayout.settingsRowRootLayout.setOnClickListener {
                viewModel.onLogoutRequested()
            }
        }
    }

    override fun attachObservers() {
        viewModel.navigation.observe(viewLifecycleOwner, {
            when (it.consume()) {
                Permissions -> openPermissions()
                Logout -> doLogout()
                is Feedback -> sendEmailFeedback(it.peek() as Feedback)
                null -> Unit // Already consumed, we do nothing
            }
        })
    }

    private fun sendEmailFeedback(feedbackState: Feedback) {
        val emailFeedbackTo = getString(R.string.feedback_to)
        val emailSubject = getString(R.string.feedback_subject)
        val emailText = getString(R.string.feedback_body_attachment, getEmailBody(feedbackState))
        val applicationID = BuildConfig.APPLICATION_ID

        // Find activities able to send email with attachments
        val intents = EmailUtil.getEmailAppsIntent(requireContext(), emailFeedbackTo, emailSubject, emailText, applicationID)

        // Start activity or send event if not available
        if (intents.size > 0) {
            val chooser = Intent.createChooser(intents.removeAt(intents.size - 1), getString(R.string.send_feedback_chooser_title))
            chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, intents.toTypedArray())
            startActivityForResult(chooser, SEND_FEEDBACK_ACTIVITY_REQUEST_CODE)
        } else {
            Toast.makeText(requireContext(), R.string.send_feedback_no_email_clients, Toast.LENGTH_LONG).show()
            Timber.d("No email client found - skipping send email")
        }
    }

    private fun getEmailBody(feedbackState: Feedback): String {
        val date = DateFormat.format(DATE_FORMAT, Date()).toString() + " " + TimeZone.getDefault().id
        val device = "Android ${Build.MODEL.replace("([();])".toRegex(), "")} Version ${Build.VERSION.SDK_INT} ${Build.VERSION.RELEASE} (Build ${Build.ID})"
        val version =  "${BuildConfig.FLAVOR.capitalize()} Version ${BuildConfig.VERSION_NAME} (Build ${BuildConfig.APPLICATION_ID}:${BuildConfig.VERSION_CODE})"
        val locale = ConfigurationCompat.getLocales(resources.configuration)[0]
        val language = "${locale.language}_${locale.country}"

        val location = if (feedbackState.isLocationEnabled) getString(R.string.feedback_location_disabled) else getString(R.string.feedback_location_enabled)
        val bluetooth = when(feedbackState.bluetoothState) {
            ProxySDK.RxBleClientState.READY.name -> getString(R.string.feedback_bluetooth_enabled)
            ProxySDK.RxBleClientState.BLUETOOTH_NOT_ENABLED.name -> getString(R.string.feedback_bluetooth_disabled)
            else -> feedbackState.bluetoothState.capitalize()
        }
        val scanning = if (feedbackState.isScanning) getString(R.string.feedback_scanning_on) else getString(R.string.feedback_scanning_off)
        val signalling = if (feedbackState.isAdvertiserEnabled) getString(R.string.feedback_signalling_on) else getString(R.string.feedback_signalling_off)

        return """
            ${getString(R.string.feedback_date, date)}
            ${getString(R.string.feedback_device, device)}
            ${getString(R.string.feedback_version, version)}
            ${getString(R.string.feedback_language, language)}
            ${getString(R.string.feedback_location, location)}
            ${getString(R.string.feedback_bluetooth, bluetooth, scanning, signalling)}
            ${getString(R.string.feedback_nfc, if (feedbackState.isNfcEnabled) "Enabled" else "Disabled")}
           
            """.trimIndent()
    }

    private fun doLogout() {
        navigateTo(SettingsFragmentDirections.actionSettingsFragmentToLoginFragment())
    }

    private fun openPermissions() {
        Intent(requireContext(), AuthActivity::class.java).apply {
            putExtra(AuthActivity.PERMISSIONS_ONLY_EXTRA, true)
            putExtra(AuthActivity.APP_NAME_EXTRA, getString(R.string.app_name))
            putExtra(AuthActivity.APP_ICON_EXTRA, R.mipmap.ic_launcher)
            putExtra(AuthActivity.INCLUDE_BG_LOCATION_PERMISSION, true)
            startActivityForResult(this, PERMISSIONS_REQUEST_CODE)
        }
    }

    companion object {
        private const val SEND_FEEDBACK_ACTIVITY_REQUEST_CODE = 900
        private const val PERMISSIONS_REQUEST_CODE = 902
        private const val DATE_FORMAT = "yyyy-MM-dd HH:mm:ss"
    }
}