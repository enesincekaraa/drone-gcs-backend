package com.enes.dronegcs;

import io.dronefleet.mavlink.MavlinkConnection;
import io.dronefleet.mavlink.MavlinkMessage;
import io.dronefleet.mavlink.minimal.Heartbeat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.net.DatagramSocket;

public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws IOException {
        DatagramSocket socket = new DatagramSocket(14550);
        UdpInputStream inputStream = new UdpInputStream(socket);

        MavlinkConnection connection = MavlinkConnection
                .create(inputStream, OutputStream.nullOutputStream());

        logger.info("Dinleme başladı , mesajlar bekleniyor...");


        while (true) {
            MavlinkMessage<?> message = connection.next();
            Object payload = message.getPayload();

            if (payload instanceof Heartbeat heartbeat) {
                logger.info("Heartbeat alındı! Sistem ID: " + message.getOriginSystemId());
            }
        }
    }
}
