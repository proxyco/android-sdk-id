package co.proxy.sdk.example.ui.extensions

import android.app.Activity
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding

inline fun <reified T> Activity.safeExtra(key: String, defaultValue: T): Lazy<T> = lazy {
    val value = intent.extras?.get(key)
    if (value is T) {
        value
    } else {
        defaultValue
    }
}

inline fun <T : ViewBinding> AppCompatActivity.viewBinding(
    crossinline bindingInflater: (LayoutInflater) -> T
) = lazy(LazyThreadSafetyMode.NONE) {
    bindingInflater.invoke(layoutInflater)
}