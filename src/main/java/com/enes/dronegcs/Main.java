package com.enes.dronegcs;

import io.dronefleet.mavlink.MavlinkConnection;
import io.dronefleet.mavlink.MavlinkMessage;
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

        MavlinkConnection connection = MavlinkConnection.create(inputStream,OutputStream.nullOutputStream());

        MavlinkMessageHandler handler = new MavlinkMessageHandler();

        logger.info("Drone GCS başlatıldı. MAVLink mesajları dinleniyor...");

        while (true) {
            MavlinkMessage<?> message = connection.next();
            handler.handle(message);
        }


    }
}
