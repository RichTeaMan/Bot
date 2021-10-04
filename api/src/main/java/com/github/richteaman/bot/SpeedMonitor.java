package com.github.richteaman.bot;

import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class SpeedMonitor implements GpioPinListenerDigital {

    private int eventsPerRev = 20;

    private List<Long> list = new ArrayList<>();


    @Override
    public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent gpioPinDigitalStateChangeEvent) {

        synchronized (this) {
            list.add(System.currentTimeMillis());
        }
    }


    public double getRevsPerSecond() {

        // get a second ago
        long interval = System.currentTimeMillis() - 1000L;
        synchronized (this) {
            List<Long> newList = list.stream().filter(t -> t > interval).collect(Collectors.toList());
            list = newList;
        }

        double revs = ((double)list.size()) / eventsPerRev;
        return revs;
    }

    public double getRevsPerSecondQuarter() {

        // get a second ago
        long interval = System.currentTimeMillis() - 250L;
        synchronized (this) {
            List<Long> newList = list.stream().filter(t -> t > interval).collect(Collectors.toList());
            list = newList;
        }

        double revs = ((double)list.size()) / eventsPerRev;
        return revs * 4;
    }
}
