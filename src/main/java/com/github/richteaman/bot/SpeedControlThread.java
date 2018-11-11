package com.github.richteaman.bot;

import com.github.richteaman.bot.controllers.GpioRestController;
import com.github.richteaman.bot.services.GpioService;
import com.github.richteaman.bot.services.PidController;
import com.pi4j.io.gpio.PinState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SpeedControlThread extends Thread {

    private double requiredSpeed = 1.0;

    private int sleepTime = 1;

    private GpioService gpioService;

    private PidController pid = new PidController();

    private boolean shouldReset = false;

    @Autowired
    public SpeedControlThread(GpioService gpioService) {
        this.gpioService = gpioService;
    }

    public void run() {

        pid.MinOutput = -1000;
        pid.MaxOutput = 1000;
        pid.Kp = 0;
        pid.Kd = 0;
        pid.Ki = 0;

        long lastTime = System.currentTimeMillis();
        double lastOutput = 0;

        while (true) {

            long currentTime = System.currentTimeMillis();
            long elapsed = currentTime - lastTime;
            double revs = gpioService.getSpeedMonitor().getRevsPerSecond();
            if (lastOutput < 0) {
                //revs = -revs;
            }

            double output = pid.control(requiredSpeed, revs, (int)elapsed);
            System.out.println(String.format("Target %s | Speed %s | Output %s | Elapsed %s", requiredSpeed, revs, output, elapsed));
            UpdatePwm2((int)output);

            lastOutput = output;
            lastTime = currentTime;

            try {
                Thread.sleep(sleepTime);

                if (shouldReset) {
                    UpdatePwm2(0);
                    Thread.sleep(3000L);
                    shouldReset = false;
                    lastOutput = 0;
                    lastTime = System.currentTimeMillis();
                }
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }

        }
    }

    public void reset() {
        shouldReset = true;
    }

    private void UpdatePwm2(int pwm) {

        gpioService.getPin1().setState(PinState.LOW);
        gpioService.getPin4().setState(PinState.LOW);

        if (pwm > 0) {
            gpioService.getPin1().setState(PinState.HIGH);
        } else if (pwm < 0) {
            gpioService.getPin4().setState(PinState.HIGH);
        }
        int modulatedPwm = Math.abs(pwm);

        gpioService.getPwm2().setPwm(modulatedPwm);
    }

    public double getRequiredSpeed() {
        return requiredSpeed;
    }

    public void setRequiredSpeed(double requiredSpeed) {
        this.requiredSpeed = requiredSpeed;
    }

    public PidController getPid() {
        return pid;
    }
}
