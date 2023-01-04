package com.hnt.hnt_android.socket

import android.net.InetAddresses
import android.os.Handler
import android.os.Looper
import java.io.InputStream
import java.io.OutputStream
import java.io.Serializable
import java.net.Inet4Address
import java.net.InetAddress
import java.net.Socket

class SocketClient : Serializable {

    private lateinit var socket: Socket
    private lateinit var inputStream: InputStream
    private lateinit var outputStream: OutputStream

    fun connect(port: Int) {
        try {
            val socketAddress = InetAddress.getLocalHost()
            println(socketAddress)
            socket = Socket("192.168.0.1", port)
            outputStream = socket.getOutputStream()
            inputStream = socket.getInputStream()
        } catch (e: Exception) {
            println("socket connect exception start!!")
            println("e: $e")
        }
    }

    fun sendData(data: String) {
        outputStream.write(
            (data + "\n").toByteArray(Charsets.UTF_8)
        )
    }

    fun flush() {
        outputStream.flush()
    }

    fun read(): Boolean {
        var isRead = false
        if (inputStream.available() > 0) {
            isRead = true
        }
        inputStream.bufferedReader(Charsets.UTF_8).forEachLine {
            println(it)
        }
        return isRead
    }

    fun closeConnect() {
        outputStream.close()
        inputStream.close()
        socket.close()
    }
}

fun main() {
    /**
    val socket = SocketClient()
    socket.connect(80)

    val testData = "CFG_SET&user=andrew2767&ssid=andrew-iptime&passwd=90836242ab&dhcp=1&rtuip=192.168.10.250&submask=255.255.255.0&gwip=192.168.10.1&dns=8.8.8.8&subdns=1.1.1.1&brkdomain=hntnas.diskstation.me&brkport=1883&brkid=hnt1&brkpw=abcde&duty=5"
    for (i in 1..10) {
        socket.sendData(testData)
        println(testData)
    }
    socket.sendData("-----------------------------")
    socket.flush()
    var isRead = false
    while (!isRead) {
        isRead = socket.read()
    }
    if (isRead) {
        socket.closeConnect()
    }
    **/
    val hostAddress: String = InetAddress.getLocalHost().hostAddress
    println("loopbackaddrress : " + hostAddress)
    val address: InetAddress = InetAddress.getByName("192.168.0.1")
    val client = UDPClient(address)

    val testData = "CFG_SET&user=andrew2767&ssid=andrew-iptime&passwd=90836242ab&dhcp=1&rtuip=192.168.10.250&submask=255.255.255.0&gwip=192.168.10.1&dns=8.8.8.8&subdns=1.1.1.1&brkdomain=hntnas.diskstation.me&brkport=1883&brkid=hnt1&brkpw=abcde&duty=5"
    val testData2 = "CFG_GET"

    var getResult: String = client.sendEcho(testData2, 1113)
    println("1 : " + getResult)
    client.close()

    if(getResult != null && getResult != "") {
        println("1-1")
        Thread.sleep(1000)
        val client2 = UDPClient(address)
        //var result: String = client2.sendEcho(testData, 1113)
        //println("2 : " + result);

        //client2.close()
    }
}