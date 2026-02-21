package com.margaritaolivera.compras.core.network

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WebSocketManager @Inject constructor(
    private val client: OkHttpClient,
    private val tokenManager: TokenManager
) {
    private var webSocket: WebSocket? = null

    private val _messages = MutableSharedFlow<String>(extraBufferCapacity = 10)
    val messages = _messages.asSharedFlow()

    suspend fun connect() {
        val token = tokenManager.getToken()
        if (token.isNullOrEmpty()) return

        val wsUrl = "wss://messi-cristiano.online/?token=$token"

        val request = Request.Builder()
            .url(wsUrl)
            .build()

        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                super.onOpen(webSocket, response)
                println(" WebSocket Conectado a $wsUrl")
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                super.onMessage(webSocket, text)
                println("Mensaje recibido: $text")
                _messages.tryEmit(text)
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                super.onClosed(webSocket, code, reason)
                println(" WebSocket Cerrado: $reason")
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                super.onFailure(webSocket, t, response)
                println("Error en WebSocket: ${t.message}")
            }
        })
    }

    fun sendMessage(jsonMessage: String) {
        webSocket?.send(jsonMessage)
        println(" Mensaje enviado: $jsonMessage")
    }

    fun disconnect() {
        webSocket?.close(1000, "Cierre normal por el usuario")
        webSocket = null
    }
}