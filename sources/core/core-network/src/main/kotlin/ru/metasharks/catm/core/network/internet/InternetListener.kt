package ru.metasharks.catm.core.network.internet

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import timber.log.Timber

class InternetListener(private val callback: Callback) {

    private val internetAvailabilityCallback = object : InternetAvailability.Callback() {

        override fun onNetworkChangedAvailability(
            type: InternetAvailability.Type,
            internetAvailability: InternetAvailability,
            previouslyAvailable: Boolean,
            currentlyAvailable: Boolean,
        ) {
            super.onNetworkChangedAvailability(
                type,
                internetAvailability,
                previouslyAvailable,
                currentlyAvailable
            )
            when {
                previouslyAvailable && currentlyAvailable.not() -> {
                    callback.onInternetLost()
                }
                previouslyAvailable.not() && currentlyAvailable -> {
                    callback.onInternetAcquired()
                }
            }
        }
    }

    private val connectivityCellularCallback =
        getNetworkCallback(InternetAvailability.Type.CELLULAR)

    private val connectivityWiFiCallback = getNetworkCallback(InternetAvailability.Type.WIFI)

    private val internetAvailability: InternetAvailability = InternetAvailability().apply {
        setCallback(internetAvailabilityCallback)
    }

    private val networkCellularRequest = NetworkRequest.Builder()
        .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
        .build()

    private val networkWiFiRequest = NetworkRequest.Builder()
        .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
        .build()

    private fun getNetworkCallback(type: InternetAvailability.Type): ConnectivityManager.NetworkCallback {
        return object : ConnectivityManager.NetworkCallback() {

            override fun onCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities
            ) {
                super.onCapabilitiesChanged(network, networkCapabilities)
                type.onCapabilitiesChanged(networkCapabilities)
            }

            override fun onLost(network: Network) {
                type.onLost()
                super.onLost(network)
            }
        }
    }

    fun InternetAvailability.Type.onCapabilitiesChanged(networkCapabilities: NetworkCapabilities) {
        Timber.d(networkCapabilities.toString())
        if (networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)) {
            Timber.d("$this network acquired")
            internetAvailability.setNetworkAvailable(this, true)
        }
    }

    private fun InternetAvailability.Type.onLost() {
        Timber.d("$this network lost")
        internetAvailability.setNetworkAvailable(this, false)
    }

    fun setup(context: Context) {
        val service: ConnectivityManager = context.getSystemService(ConnectivityManager::class.java)
        service.registerNetworkCallback(networkCellularRequest, connectivityCellularCallback)
        service.registerNetworkCallback(networkWiFiRequest, connectivityWiFiCallback)
    }

    fun clear(context: Context) {
        val service: ConnectivityManager = context.getSystemService(ConnectivityManager::class.java)
        service.unregisterNetworkCallback(connectivityCellularCallback)
        service.unregisterNetworkCallback(connectivityWiFiCallback)
    }

    fun trigger() {
        if (internetAvailability.isAvailable) {
            callback.onInternetAcquired()
        } else {
            callback.onInternetLost()
        }
    }

    interface Callback {

        /**
         * Метод вызывается при соединении с сетью интернет (даже в самый первый раз,
         * когда соеднинение проверяется)
         */
        fun onInternetAcquired()

        fun onInternetLost()
    }
}
