package com.enes.dronegcs;

import io.dronefleet.mavlink.MavlinkMessage;
import io.dronefleet.mavlink.common.BatteryStatus;
import io.dronefleet.mavlink.common.GlobalPositionInt;
import io.dronefleet.mavlink.minimal.Heartbeat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class MavlinkMessageHandler {

    private static final Logger logger = LoggerFactory.getLogger(MavlinkMessageHandler.class);

    public void handle(MavlinkMessage<?> message) {
        Object payload = message.getPayload();


        switch (payload){
            case Heartbeat heartbeat -> handleHeartbeat(message,heartbeat);
            case GlobalPositionInt position -> handlePosition(message,position);
            case BatteryStatus battery -> handleBattery(message,battery);
            default -> {}
        }
    }

    private void handleBattery(MavlinkMessage<?> message, BatteryStatus battery) {
        int remainingPercent = battery.batteryRemaining();
        double voltage = battery.voltages().get(0) / 1000.0;
        double currentAmps = battery.currentBattery() / 100.0;

        logger.info("Batarya durumu güncellendi! Sistem ID: {} | Kalan Batarya: {}% | Gerilim: {}V | Akım: {}A",
                message.getOriginSystemId(), remainingPercent, voltage, currentAmps);

    }

    private void  handleHeartbeat(MavlinkMessage<?> message, Heartbeat heartbeat) {
        logger.info("Heartbeat alındı! Sistem ID: " + message.getOriginSystemId());
    }

    private void handlePosition(MavlinkMessage<?> message, GlobalPositionInt position) {
        double latitude = position.lat() / 1e7;
        double longitude = position.lon() / 1e7;
        double altitudeMeters = position.relativeAlt() / 1000.0;

        logger.info("Konum güncellendi! Sistem ID: {} | Enlem: {} | Boylam: {} | İrtifa: {}m",
                message.getOriginSystemId(), latitude, longitude, altitudeMeters);
    }

}
