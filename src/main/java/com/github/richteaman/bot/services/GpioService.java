package com.github.richteaman.bot.services;

import com.pi4j.io.gpio.*;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Component
public class GpioService {

private GpioController gpioController;

    private GpioPinDigitalOutput pin1;

    private GpioPinPwmOutput pwm1;

    @PostConstruct
    public void init() {

        gpioController = GpioFactory.getInstance();

        pin1 = gpioController.provisionDigitalOutputPin(RaspiPin.GPIO_01, "MyLED", PinState.HIGH);
        pin1.setShutdownOptions(true, PinState.LOW);

        pwm1 = gpioController.provisionPwmOutputPin(RaspiPin.GPIO_23);
        pwm1.setPwm(0);
        pwm1.setPwmRange(1000);

    }

    @PreDestroy
    public void shutdown() {
        gpioController.shutdown();
    }

    public GpioController getGpioController() {
        return gpioController;
    }

    public GpioPinDigitalOutput getPin1() {
        return pin1;
    }

    public GpioPinPwmOutput getPwm1() {
        return pwm1;
    }
}