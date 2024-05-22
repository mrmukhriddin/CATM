package ru.metasharks.catm.feature.profile.role

interface RoleProvider {

    val currentRole: Role

    fun isAllowed(action: Action): Boolean
}
