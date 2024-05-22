package ru.metasharks.catm.core.navigation

import android.app.Activity
import android.content.ActivityNotFoundException
import com.github.terrakok.cicerone.Back
import com.github.terrakok.cicerone.BackTo
import com.github.terrakok.cicerone.Command
import com.github.terrakok.cicerone.Forward
import com.github.terrakok.cicerone.Navigator
import com.github.terrakok.cicerone.Replace
import com.github.terrakok.cicerone.Screen
import com.github.terrakok.cicerone.androidx.ActivityScreen
import com.github.terrakok.cicerone.androidx.FragmentScreen
import timber.log.Timber

open class ApplicationNavigator : Navigator {

    // Here we init application navigator  activity stack
    private val activityStack: ArrayDeque<Activity> = ArrayDeque()

    private val currentActivity: Activity
        get() = activityStack.last()

    override fun applyCommands(commands: Array<out Command>) {
        for (command in commands) {
            applyCommand(command)
        }
    }

    private fun applyCommand(command: Command) {
        when (command) {
            is Forward -> forward(command)
            is Replace -> replace(command)
            is BackTo -> backTo(command)
            is Back -> back()
            is ForwardWithResult<*> -> forwardWithResult(command)
            is BackWithResult<*> -> backWithResult(command)
        }
    }

    class BackWithResult<Output>(
        val code: Int,
        val output: Output
    ) : Command

    class ForwardWithResult<Output>(
        val screen: Screen,
        val function: (resultCode: Int, Output) -> Unit
    ) : Command

    protected open fun backTo(command: BackTo) {
        val screen = command.screen
        if (screen == null) {
            backToRoot()
        } else {
            val screenKey = screen.screenKey
            val index = activityStack.indexOfFirst { it::class.java.name == screenKey }
            if (index != -1) {
                while (activityStack.size != index) {
                    activityStack.removeLast().finish()
                }
            } else {
                backToUnexisting(screen)
            }
        }
    }

    protected open fun backToUnexisting(screen: Screen) {
        backToRoot()
    }

    private fun backToRoot() {
        while (activityStack.size != 1) {
            activityStack.removeLast().finish()
        }
    }

    protected open fun forward(command: Forward) {
        when (val screen = command.screen) {
            is ActivityScreen -> {
                checkAndStartActivity(screen)
            }
            is FragmentScreen -> {
//                Timber.d("ApplicationNavigator doesn't support fragments")
            }
        }
    }

    protected open fun replace(command: Replace) {
        when (val screen = command.screen) {
            is ActivityScreen -> {
                val activity = activityStack.removeLast()
                checkAndStartActivity(screen, activity)
                activity.finish()
            }
            is FragmentScreen -> {
                throw IllegalStateException("fragment screens are not supported")
            }
        }
    }

    protected open fun back() {
        activityBack()
    }

    fun registerActivity(activity: Activity) {
        if (!activityStack.contains(activity)) {
            activityStack.add(activity)
        }
    }

    private fun backWithResult(command: BackWithResult<*>) {
        TODO("Not yet implemented")
    }

    private fun forwardWithResult(command: ForwardWithResult<*>) {
        when (command.screen) {
            is ActivityScreen -> {
                checkAndStartActivity(command.screen)
            }
        }
    }

    private fun checkAndStartActivity(
        screen: ActivityScreen,
        activityToStartFrom: Activity? = null
    ) {
        if (activityStack.isEmpty() && activityToStartFrom == null) {
            throw IllegalStateException("No activity has been added to stack")
        }
        val processActivity = activityToStartFrom ?: currentActivity
        val activityIntent = screen.createIntent(processActivity)
        try {
            processActivity.startActivity(activityIntent, screen.startActivityOptions)
        } catch (e: ActivityNotFoundException) {
            Timber.e(e)
            throw ActivityNotFoundException()
        }
    }

    protected open fun activityBack() {
        activityStack.removeLast().finish()
    }
}
