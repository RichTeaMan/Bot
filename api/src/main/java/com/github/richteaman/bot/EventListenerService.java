package com.github.richteaman.bot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class EventListenerService {

    private boolean threadStarted = false;

    private SpeedControlThread speedControlThread;

    @Autowired
    public EventListenerService(SpeedControlThread speedControlThread) {
        this.speedControlThread = speedControlThread;
    }

    @EventListener
    public void onApplicationEvent(ContextRefreshedEvent event) {

        if (!threadStarted) {
            threadStarted = true;
            speedControlThread.start();
        }
    }
}
