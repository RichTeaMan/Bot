package com.github.richteaman.bot;

import com.github.richteaman.bot.controllers.GpioRestController;
import com.github.richteaman.bot.services.GpioService;
import com.github.richteaman.bot.services.PidController;
import org.springframework.beans.factory.annotation.Autowired;

public class SpeedControlThread extends Thread {

    private double requiredSpeed = 2.0;

    private int sleepTime = 50;

    private GpioService gpioService;

    private GpioRestController gpioRestController;


    @Autowired
    public SpeedControlThread(GpioService gpioService, GpioRestController gpioRestController) {
        this.gpioService = gpioService;
        this.gpioRestController = gpioRestController;
    }

    public void run() {

        PidController pid = new PidController();
        pid.MinOutput = -1000;
        pid.MaxOutput = 1000;


        while (true) {

            double output = pid.control(requiredSpeed, gpioService.getSpeedMonitor().getRevsPerSecond(), sleepTime);
            gpioRestController.UpdatePwm2((int)output);


            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
        }
    }

    public double getRequiredSpeed() {
        return requiredSpeed;
    }

    public void setRequiredSpeed(double requiredSpeed) {
        this.requiredSpeed = requiredSpeed;
    }
}
