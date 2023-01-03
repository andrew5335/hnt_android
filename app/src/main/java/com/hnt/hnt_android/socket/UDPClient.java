package com.hnt.hnt_android.socket;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.*;

public class UDPClient {

    private DatagramSocket socket;
    private InetAddress address;
    private byte[] buf;
    private byte[] buf2;

    public UDPClient(InetAddress address) {
        try {
            socket = new DatagramSocket();
            //address = InetAddress.getByName("192.168.0.1");
            this.address = address;
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public String sendEcho(String msg, int port) throws Exception {
        String received = "";
        try {
            buf = msg.getBytes();
            buf2 = new byte[1024];
            DatagramPacket send
                    = new DatagramPacket(buf, buf.length, address, port);
            socket.send(send);
            DatagramPacket receive = new DatagramPacket(buf2, buf2.length);
            socket.receive(receive);
            received = new String(
                    receive.getData(), 0, receive.getLength());
        } catch(Exception e) {
            e.printStackTrace();
        }

        return received;
    }

    public void close() {
        socket.close();
    }
}


