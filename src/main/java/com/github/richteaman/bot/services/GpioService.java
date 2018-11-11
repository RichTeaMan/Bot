package com.github.richteaman.bot.services;

import com.github.richteaman.bot.SpeedMonitor;
import com.pi4j.io.gpio.*;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Component
public class GpioService {

private GpioController gpioController;

    private GpioPinDigitalOutput pin0;

    private GpioPinDigitalOutput pin1;

    private GpioPinDigitalOutput pin3;

    private GpioPinDigitalOutput pin4;

    private GpioPinDigitalInput pin25;

    private GpioPinDigitalInput pin27;

    private GpioPinPwmOutput pwm1;

    private GpioPinPwmOutput pwm2;

    private SpeedMonitor speedMonitorLeftWheel = new SpeedMonitor();

    private SpeedMonitor speedMonitorRightWheel = new SpeedMonitor();

    @PostConstruct
    public void init() {

        gpioController = GpioFactory.getInstance();

        pin0 = gpioController.provisionDigitalOutputPin(RaspiPin.GPIO_00, "LeftForward", PinState.HIGH);
        pin0.setShutdownOptions(true, PinState.LOW);

        pin1 = gpioController.provisionDigitalOutputPin(RaspiPin.GPIO_01, "RightForward", PinState.HIGH);
        pin1.setShutdownOptions(true, PinState.LOW);

        pin3 = gpioController.provisionDigitalOutputPin(RaspiPin.GPIO_03, "LeftBackward", PinState.HIGH);
        pin3.setShutdownOptions(true, PinState.LOW);

        pin4 = gpioController.provisionDigitalOutputPin(RaspiPin.GPIO_04, "RightBackward", PinState.HIGH);
        pin4.setShutdownOptions(true, PinState.LOW);

        pin25 = gpioController.provisionDigitalInputPin(RaspiPin.GPIO_25, PinPullResistance.PULL_DOWN);
        pin25.setShutdownOptions(true);
        pin25.addListener(speedMonitorLeftWheel);

        pin27 = gpioController.provisionDigitalInputPin(RaspiPin.GPIO_27, PinPullResistance.PULL_DOWN);
        pin27.setShutdownOptions(true);
        pin27.addListener(speedMonitorRightWheel);

        pwm1 = gpioController.provisionPwmOutputPin(RaspiPin.GPIO_23);
        pwm1.setPwm(0);
        pwm1.setPwmRange(1000);

        pwm2 = gpioController.provisionPwmOutputPin(RaspiPin.GPIO_26);
        pwm2.setPwm(0);
        pwm2.setPwmRange(1000);

    }

    @PreDestroy
    public void shutdown() {
        gpioController.shutdown();
    }

    public GpioController getGpioController() {
        return gpioController;
    }

    public GpioPinDigitalOutput getPin0() {
        return pin0;
    }

    public GpioPinDigitalOutput getPin1() {
        return pin1;
    }

    public GpioPinDigitalOutput getPin3() {
        return pin3;
    }

    public GpioPinDigitalOutput getPin4() {
        return pin4;
    }

    public GpioPinPwmOutput getPwm1() {
        return pwm1;
    }

    public GpioPinPwmOutput getPwm2() {
        return pwm2;
    }

    public SpeedMonitor getSpeedMonitorLeftWheel() {
        return speedMonitorLeftWheel;
    }

    public SpeedMonitor getSpeedMonitorRightWheel() {
        return speedMonitorRightWheel;
    }
}