package com.margaritaolivera.compras.features.lists.data.remote

import android.util.Log
import com.margaritaolivera.compras.core.network.TokenManager
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import okhttp3.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ListWebSocketClient @Inject constructor(
    private val client: OkHttpClient,
    private val tokenManager: TokenManager
) {
    private var webSocket: WebSocket? = null

    private val _messages = MutableSharedFlow<String>(extraBufferCapacity = 64)
    val messages = _messages.asSharedFlow()

    private val baseUrl = "wss://messi-cristiano.online"


    suspend fun connect(listId: String) {
        disconnect()

        val token = tokenManager.getToken()

        if (token.isNullOrBlank()) {
            Log.e("WS_CLIENT", "No se pudo conectar: Token no encontrado en DataStore")
            return
        }

        val request = Request.Builder()
            .url("$baseUrl/?token=$token")
            .build()

        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                Log.d("WS_CLIENT", "WebSocket Abierto. Enviando suscripción para: $listId")

                val subscribeMsg = """
                    {
                        "type": "subscribe",
                        "payload": { "listId": "$listId" }
                    }
                """.trimIndent()

                webSocket.send(subscribeMsg)
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                _messages.tryEmit(text)
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                Log.e("WS_CLIENT", "Error en la conexión WebSocket: ${t.message}")
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                Log.d("WS_CLIENT", "Servidor cerrando conexión: $reason")
            }
        })
    }

    fun sendMessage(message: String) {
        val success = webSocket?.send(message) ?: false
        if (!success) {
            Log.e("WS_CLIENT", "No se pudo enviar el mensaje. ¿Está conectado?")
        }
    }

    fun disconnect() {
        webSocket?.close(1000, "Desconexión manual")
        webSocket = null
    }
}