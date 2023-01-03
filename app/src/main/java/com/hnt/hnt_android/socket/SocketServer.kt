package com.hnt.hnt_android.socket

import java.io.InputStream
import java.io.OutputStream
import java.io.Serializable
import java.net.ServerSocket
import java.net.Socket

class SocketServer : Serializable {
    private lateinit var serverSocket: ServerSocket
    private lateinit var clientSocket: Socket
    private lateinit var inputStream: InputStream
    private lateinit var outputStream: OutputStream

    fun isClose(): Boolean {
        if (this::serverSocket.isInitialized) {
            return serverSocket.isClosed
        }
        return true
    }

    fun connect(port: Int) {
        println("server connect start!! port : $port")
        serverSocket = ServerSocket(port)
        clientSocket = serverSocket.accept()
        inputStream = clientSocket.getInputStream()
        outputStream = clientSocket.getOutputStream()
    }

    fun read(): Boolean {
        if (inputStream.available() > 0) {
            inputStream.bufferedReader(Charsets.UTF_8).forEachLine {
                println(it)
            }
            return true
        }
        return false
    }

    fun sendData(data: String) {
        println(data)
        outputStream.write(
            (data + "\n").toByteArray(Charsets.UTF_8)
        )
        outputStream.flush()
    }

    fun connectClose() {
        serverSocket.close()
    }
}

fun main() {
    val socketServer = SocketServer()
    while (true) {
        try {
            if (socketServer.isClose()) {
                socketServer.connect(1113)
                socketServer.sendData("CFG_GET")
            } else {
                socketServer.connectClose()
            }
            var isRead = false
            println("First isRead : $isRead")
            while (isRead.not()) {
                isRead = socketServer.read()
            }
            println("Finish isRead : $isRead")
            if (isRead) {
                socketServer.sendData("CFG_GET")
            }
        } catch (e: Exception) {
            println(e.toString())
            socketServer.connectClose()
        }
    }
}