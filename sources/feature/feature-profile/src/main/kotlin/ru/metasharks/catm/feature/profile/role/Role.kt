package ru.metasharks.catm.feature.profile.role

enum class Role(val role: String?) {
    OBSERVER("observer"), SECURITY_MANAGER("security_manager"), DIRECTOR("director"), WORKER(null)
}
