package co.proxy.sdk.example.ui.extensions

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

fun View.gone() {
    visibility = View.GONE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

fun View.visible() {
    visibility = View.VISIBLE
}

fun ViewGroup.inflater(): LayoutInflater = LayoutInflater.from(context)