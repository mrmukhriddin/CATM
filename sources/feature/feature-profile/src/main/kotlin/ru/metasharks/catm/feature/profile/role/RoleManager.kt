package ru.metasharks.catm.feature.profile.role

interface RoleManager : RoleProvider {

    fun setRole(role: String?)
}
