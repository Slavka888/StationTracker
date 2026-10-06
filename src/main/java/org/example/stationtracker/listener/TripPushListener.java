package org.example.stationtracker.listener;

import org.example.stationtracker.DTO.TripStartedEvent;
import org.example.stationtracker.service.PushService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class TripPushListener {
    private final PushService pushService;

    @Autowired
    public TripPushListener(PushService pushService) {
        this.pushService = pushService;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onTripStarted(TripStartedEvent tripStartedEvent) {
        pushService.sendTripStarted(tripStartedEvent.userId(),  tripStartedEvent.tripId());
    }
}
