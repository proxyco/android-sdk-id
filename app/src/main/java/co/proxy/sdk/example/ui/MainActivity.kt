package co.proxy.sdk.example.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI.setupActionBarWithNavController
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import co.proxy.sdk.example.R
import co.proxy.sdk.example.databinding.ActivityMainBinding
import co.proxy.sdk.example.datasource.sdk.ProxyBleServiceBinder
import co.proxy.sdk.example.datasource.sdk.ProxySDKDatasource
import co.proxy.sdk.example.ui.extensions.gone
import co.proxy.sdk.example.ui.extensions.visible
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val navHostFragment by lazy { supportFragmentManager.findFragmentById(R.id.nav_host_container) as NavHostFragment }

    @Inject
    lateinit var serviceBinder: ProxyBleServiceBinder

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            setupBottomNavigationBar()
        } // Else, need to wait for onRestoreInstanceState

        navHostFragment.navController.addOnDestinationChangedListener { _, destination, _ ->
            if(destination.id == R.id.login_fragment) {
                binding.bottomNav.gone()
            } else {
                binding.bottomNav.visible()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        serviceBinder.startServices()
    }

    override fun onPause() {
        serviceBinder.stopServices()
        super.onPause()
    }

    override fun onDestroy() {
        serviceBinder.stopServices()
        serviceBinder.unbindServices()
        super.onDestroy()
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        setupBottomNavigationBar()
    }

    /**
     * Called on first creation and when restoring state.
     */
    private fun setupBottomNavigationBar() {
        binding.bottomNav.setupWithNavController(navHostFragment.navController)
    }
}