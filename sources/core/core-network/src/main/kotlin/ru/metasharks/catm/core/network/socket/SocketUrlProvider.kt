package ru.metasharks.catm.core.network.socket

fun interface SocketUrlProvider {

    operator fun invoke(token: String): String
}
