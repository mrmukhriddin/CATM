package ru.metasharks.catm.core.navigation.bottom

import android.view.View
import androidx.fragment.app.FragmentManager
import com.github.terrakok.cicerone.BaseRouter
import com.github.terrakok.cicerone.Command
import com.github.terrakok.cicerone.Navigator
import com.github.terrakok.cicerone.Screen
import com.github.terrakok.cicerone.androidx.FragmentScreen

class BottomNavigator(
    private val container: View,
    private val fragmentManager: FragmentManager,
) : Navigator {

    override fun applyCommands(commands: Array<out Command>) {
        commands.forEach { command ->
            applyCommand(command)
        }
    }

    // supports only custom Switch command
    private fun applyCommand(command: Command) {
        if (command !is Switch) {
            throw IllegalArgumentException("Unsupported Command type. BottomNavigation supports only Switch type of commands. Yours - $command")
        }
        check(command.screen is FragmentScreen) {
            "This navigator supports only Fragment Screens. Your screen - ${command.screen}"
        }
        switchToScreen(command.screen)
    }

    private fun switchToScreen(screen: FragmentScreen) {
        val key = screen.screenKey

        val transaction = fragmentManager.beginTransaction()

        val currentFragment = fragmentManager.fragments.firstOrNull { it.isVisible }
        if (currentFragment != null) {
            transaction.hide(currentFragment)
        }

        val nextFragment = fragmentManager.findFragmentByTag(key)
        if (nextFragment != null) {
            transaction.show(nextFragment)
        } else {
            val fragment = screen.createFragment(fragmentManager.fragmentFactory)
            transaction.add(container.id, fragment, key)
        }

        transaction.commit()
    }
}

class BottomRouter : BaseRouter() {

    fun switchScreen(screen: Screen) {
        executeCommands(Switch(screen))
    }
}

class Switch(val screen: Screen) : Command
