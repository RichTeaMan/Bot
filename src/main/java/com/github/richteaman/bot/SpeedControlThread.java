package com.github.richteaman.bot;

import com.github.richteaman.bot.services.GpioService;
import com.github.richteaman.bot.services.PidController;
import com.pi4j.io.gpio.PinState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SpeedControlThread extends Thread {

    /** Logger. */
    private final static Logger logger = LoggerFactory.getLogger(SpeedControlThread.class);

    private double requiredSpeedLeftWheel = 3.5;

    private double requiredSpeedRightWheel = 3.5;

    private int sleepTime = 1;

    private GpioService gpioService;

    private PidController pidLeftWheel = new PidController(200, 0, 100, PidController.Direction.DIRECT);

    private PidController pidRightWheel = new PidController(200, 0, 100, PidController.Direction.DIRECT);

    private boolean shouldReset = false;

    @Autowired
    public SpeedControlThread(GpioService gpioService) {
        this.gpioService = gpioService;
    }

    public void run() {

        pidLeftWheel.setOutputLimits(-1000, 1000);
        pidLeftWheel.setSampleTime(5);
        pidLeftWheel.setMode(PidController.Mode.AUTOMATIC);

        pidRightWheel.setOutputLimits(-1000, 1000);
        pidRightWheel.setSampleTime(5);
        pidRightWheel.setMode(PidController.Mode.AUTOMATIC);

        long lastTime = System.currentTimeMillis();
        double lastOutputLeftWheel = 0;
        double lastOutputRightWheel = 0;

        while (true) {

            long currentTime = System.currentTimeMillis();
            long elapsed = currentTime - lastTime;

            double revsLeftWheel = gpioService.getSpeedMonitorLeftWheel().getRevsPerSecondQuarter();
            if (lastOutputLeftWheel < 0) {
                //revs = -revs;
            }

            if (pidLeftWheel.compute(revsLeftWheel, requiredSpeedLeftWheel)) {
                double output = pidLeftWheel.getLastOutput();

                logger.debug("Left - Target {} | Speed {} | Output {} | Elapsed {}", requiredSpeedLeftWheel, revsLeftWheel, output, elapsed);
                UpdatePwm1((int) output);
                lastOutputLeftWheel = output;
            }

            double revsRightWheel = gpioService.getSpeedMonitorRightWheel().getRevsPerSecondQuarter();
            if (lastOutputRightWheel < 0) {
                //revs = -revs;
            }

            if (pidRightWheel.compute(revsRightWheel, requiredSpeedRightWheel)) {
                double output = pidRightWheel.getLastOutput();

                logger.debug("Right - Target {} | Speed {} | Output {} | Elapsed {}", requiredSpeedRightWheel, revsRightWheel, output, elapsed);
                UpdatePwm2((int) output);
                lastOutputRightWheel = output;
            }
            lastTime = currentTime;

            try {
                Thread.sleep(sleepTime);

                if (shouldReset) {
                    UpdatePwm2(0);
                    Thread.sleep(3000L);
                    shouldReset = false;

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

    private void UpdatePwm1(int pwm) {

        gpioService.getPin0().setState(PinState.LOW);
        gpioService.getPin3().setState(PinState.LOW);

        if (pwm > 0) {
            gpioService.getPin0().setState(PinState.HIGH);
        } else if (pwm < 0) {
            gpioService.getPin3().setState(PinState.HIGH);
        }
        int modulatedPwm = Math.abs(pwm);

        gpioService.getPwm1().setPwm(modulatedPwm);
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

    public double getRequiredSpeedRightWheel() {
        return requiredSpeedRightWheel;
    }

    public void setRequiredSpeedRightWheel(double requiredSpeedRightWheel) {
        this.requiredSpeedRightWheel = requiredSpeedRightWheel;
    }

    public PidController getPidRightWheel() {
        return pidRightWheel;
    }
}
