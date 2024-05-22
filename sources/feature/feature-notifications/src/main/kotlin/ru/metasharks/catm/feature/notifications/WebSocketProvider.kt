package ru.metasharks.catm.feature.notifications

import com.neovisionaries.ws.client.WebSocket

object WebSocketProvider {

    private var _webSocket: WebSocket? = null
    val webSocket: WebSocket? = _webSocket

    internal fun setWebSocket(webSocket: WebSocket) {
        _webSocket = webSocket
    }

    internal fun resetWebSocket() {
        _webSocket = null
    }

    fun requireWebSocket(): WebSocket = requireNotNull(_webSocket)
}
