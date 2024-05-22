package ru.metasharks.catm.core.navigation

import com.github.terrakok.cicerone.Router
import ru.metasharks.catm.core.navigation.screens.result.ResultScreen

class ApplicationRouter : Router() {

    fun <T> navigateToWithResult(resultScreen: ResultScreen, onResult: (T) -> Unit) {
        setResultListener(resultScreen.resultKey) { result -> onResult(result as T) }
        navigateTo(resultScreen)
    }

    fun sendResultBy(key: String, data: Any) {
        super.sendResult(key, data)
    }
}
