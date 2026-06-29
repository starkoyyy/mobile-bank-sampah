package com.example.tugas_pab.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

object RetrofitClient {
    var BASE_URL = "https://clench-maker-friend.ngrok-free.dev/"
    private var _instance: ApiService? = null

    val instance: ApiService
        get() {
            if (_instance == null) {
                val retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                _instance = retrofit.create(ApiService::class.java)
            }
            return _instance!!
        }

    fun setServerIp(ip: String) {
        var cleanIp = ip.trim()
        if (!cleanIp.contains(":")) {
            cleanIp += ":3000" // default port
        }
        val newUrl = "http://$cleanIp/"
        if (BASE_URL != newUrl) {
            BASE_URL = newUrl
            _instance = null // force recreate on next access
        }
    }

    fun discoverServer(onFound: (String) -> Unit) {
        Thread {
            try {
                val socket = DatagramSocket()
                socket.broadcast = true
                socket.soTimeout = 3000 // 3 seconds timeout

                val sendData = "DISCOVER_BANK_SAMPAH_SERVER".toByteArray()
                // Broadcast to local network
                val sendPacket = DatagramPacket(sendData, sendData.size, InetAddress.getByName("255.255.255.255"), 41234)
                socket.send(sendPacket)

                val recvData = ByteArray(1024)
                val recvPacket = DatagramPacket(recvData, recvData.size)
                socket.receive(recvPacket)

                val response = String(recvPacket.data, 0, recvPacket.length)
                if (response == "BANK_SAMPAH_SERVER_ACK") {
                    val serverIp = recvPacket.address.hostAddress
                    android.os.Handler(android.os.Looper.getMainLooper()).post {
                        onFound(serverIp)
                    }
                }
                socket.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.start()
    }
}
