package ru.metasharks.catm.core.network.internet

class InternetAvailability {

    private var firstConnection: Type? = null

    private var cellular: Boolean = false
    private var wifi: Boolean = false

    private var callback: Callback? = null

    fun setCallback(callback: Callback?) {
        this.callback = callback
    }

    private val available: Boolean
        get() = cellular || wifi

    fun setNetworkAvailable(type: Type, isAvailable: Boolean) {
        val previouslyAvailable = available
        when (type) {
            Type.CELLULAR -> setCellularAvailable(isAvailable)
            Type.WIFI -> setWiFiAvailable(isAvailable)
        }
        callback?.onNetworkChangedAvailability(
            type,
            this,
            previouslyAvailable,
            available
        )
    }

    val isAvailable: Boolean
        get() = available

    private fun setCellularAvailable(isAvailable: Boolean) {
        cellular = isAvailable
    }

    private fun setWiFiAvailable(isAvailable: Boolean) {
        wifi = isAvailable
    }

    enum class Type {
        CELLULAR, WIFI
    }

    abstract class Callback {

        open fun onNetworkChangedAvailability(
            type: Type,
            internetAvailability: InternetAvailability,
            previouslyAvailable: Boolean,
            currentlyAvailable: Boolean,
        ) {
            when {
                previouslyAvailable.not() && currentlyAvailable -> {
                    internetAvailability.firstConnection = type
                }
                previouslyAvailable && currentlyAvailable.not() -> {
                    internetAvailability.firstConnection = null
                }
            }
        }
    }
}
