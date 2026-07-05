package com.enes.dronegcs;

import io.dronefleet.mavlink.MavlinkConnection;
import io.dronefleet.mavlink.MavlinkMessage;
import io.dronefleet.mavlink.minimal.Heartbeat;

import java.io.IOException;
import java.io.OutputStream;
import java.net.DatagramSocket;

public class Main {

    public static void main(String[] args) throws IOException {
        DatagramSocket socket = new DatagramSocket(14550);
        UdpInputStream inputStream = new UdpInputStream(socket);

        MavlinkConnection connection = MavlinkConnection
                .create(inputStream, OutputStream.nullOutputStream());

        System.out.println("Dinleme başladı , mesajlar bekleniyor...");


        while (true) {
            MavlinkMessage<?> message = connection.next();
            Object payload = message.getPayload();

            if (payload instanceof Heartbeat heartbeat) {
                System.out.println("Heartbeat alındı! Sistem ID: " + message.getOriginSystemId());
            }
        }
    }
}
