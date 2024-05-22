package ru.metasharks.catm.feature.profile.role

import ru.metasharks.catm.core.storage.prefs.PreferencesDelegate
import ru.metasharks.catm.core.storage.prefs.PreferencesProvider
import javax.inject.Inject

internal class RoleManagerImpl @Inject constructor(
    private val preferencesProvider: PreferencesProvider
) : RoleManager {

    private var roleString: String? by PreferencesDelegate(
        { preferencesProvider.applicationPreferences },
        PREF_ROLE,
        null
    )

    override val currentRole: Role
        get() {
            val role = roleString
            return Role.values().first {
                it.role == role
            }
        }

    override fun setRole(role: String?) {
        roleString = role
    }

    override fun isAllowed(action: Action): Boolean {
        return when (currentRole) {
            Role.DIRECTOR -> directorActions.contains(action)
            Role.OBSERVER -> observerActions.contains(action)
            Role.SECURITY_MANAGER -> securityManagerActions.contains(action)
            Role.WORKER -> workerActions.contains(action)
        }
    }

    companion object {

        private val observerActions = setOf(
            Action.GET_WORKERS
        )

        private val securityManagerActions = setOf(
            Action.GET_WORKERS
        )

        private val directorActions = setOf(
            Action.GET_WORKERS
        )

        private val workerActions = setOf<Action>()

        const val PREF_ROLE = "pref.role"
    }
}
