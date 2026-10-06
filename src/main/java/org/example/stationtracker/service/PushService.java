package org.example.stationtracker.service;

import com.google.firebase.messaging.AndroidConfig;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import lombok.extern.slf4j.Slf4j;
import org.example.stationtracker.entity.UserDevice;
import org.example.stationtracker.repository.UserDeviceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class PushService {
    private final FirebaseMessaging firebaseMessaging;
    private final UserDeviceRepository userDeviceRepository;

    @Autowired
    public PushService(FirebaseMessaging firebaseMessaging, UserDeviceRepository userDeviceRepository) {
        this.firebaseMessaging = firebaseMessaging;
        this.userDeviceRepository = userDeviceRepository;
    }

    public void sendTripStarted(Long userId, Long tripId) {
        List<UserDevice> devices = userDeviceRepository.findAllByUserIdAndEnabledTrue(userId);

        for  (UserDevice device : devices) {
            Message message = Message
                    .builder()
                    .setFid(device.getFirebaseInstallationId())
                    .putData("type", "TRIP_STARTED")
                    .putData("tripId", tripId.toString())
                    .setAndroidConfig(
                            AndroidConfig
                                    .builder()
                                    .setPriority(AndroidConfig.Priority.HIGH)
                                    .build()
                    )
                    .build();

            try {
                String messageId = firebaseMessaging.send(message);

                log.info(
                        "Trip start push sent: tripId = {}, userId = {}, messageId = {}",
                        tripId,
                        userId,
                        messageId
                );
            }
            catch (FirebaseMessagingException e){
                log.error(
                        "Failed to send trip started message: tripid = {}, userId = {}",
                        tripId,
                        userId,
                        e
                );
            }
        }
    }
}
