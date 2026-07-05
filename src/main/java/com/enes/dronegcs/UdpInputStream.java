package com.enes.dronegcs;

import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class UdpInputStream extends InputStream {

    private final DatagramSocket socket;

    private final byte[] buffer = new byte[4096];

    private int bufferPosition = 0;
    private int bufferLength = 0;


    public UdpInputStream(DatagramSocket socket) {
        this.socket = socket;
    }


    @Override
    public int read() throws IOException {
        if (bufferPosition >= bufferLength) {
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            socket.receive(packet);
            bufferLength = packet.getLength();
            bufferPosition = 0;
        }
        return buffer[bufferPosition++] & 0xFF;
    }
}
