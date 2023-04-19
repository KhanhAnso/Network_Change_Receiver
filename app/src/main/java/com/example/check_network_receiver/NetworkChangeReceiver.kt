package com.example.check_network_receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import com.nispok.snackbar.Snackbar
import com.nispok.snackbar.SnackbarManager
import com.nispok.snackbar.enums.SnackbarType

class NetworkChangeReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, p1: Intent?) {

        val connectivityManager = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT < 23){
            val networkInfo = connectivityManager.activeNetworkInfo
            networkInfo.runCatching {
                if (this != null && isConnected &&
                    (type == ConnectivityManager.TYPE_WIFI || type == ConnectivityManager.TYPE_MOBILE))
                    Result.success(this)
                else
                    Result.failure(Throwable())
            }.fold(onSuccess = {
                if (SnackbarManager.getCurrentSnackbar()?.isShowing == true)
                    SnackbarManager.getCurrentSnackbar().dismiss()
            }, onFailure = {
                SnackbarManager.show(
                    Snackbar.with(context)
                        .position(Snackbar.SnackbarPosition.BOTTOM_CENTER)
                        .text("Cannot connect to the internet. Please check your internet connection.")
                        .type(SnackbarType.MULTI_LINE)
                        .color(Color.RED)
                        .duration(Snackbar.SnackbarDuration.LENGTH_INDEFINITE)
                        .animation(true)
                )
            })
        }else{
            val network = connectivityManager.activeNetwork
            network?.let {
                val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
                if (networkCapabilities!!.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                    || networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)){
                    if (SnackbarManager.getCurrentSnackbar()?.isShowing == true){
                        SnackbarManager.getCurrentSnackbar().dismiss()
                    }
                    return
                }
            }

            SnackbarManager.show(
                Snackbar.with(context)
                    .position(Snackbar.SnackbarPosition.BOTTOM_CENTER)
                    .text("Cannot connect to the internet. Please check your internet connection.")
                    .type(SnackbarType.MULTI_LINE)
                    .color(Color.RED)
                    .duration(Snackbar.SnackbarDuration.LENGTH_INDEFINITE)
                    .animation(true)
            )
        }
    }
}