package ru.metasharks.catm.model.api

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import ru.metasharks.catm.R
import ru.metasharks.catm.core.network.socket.SocketUrlProvider
import javax.inject.Inject

internal class SocketUrlProviderImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : SocketUrlProvider {

    override fun invoke(token: String): String =
        StringBuilder(context.getString(R.string.base_socket_url))
            .append('?')
            .append("token=").append(token)
            .toString()
}
