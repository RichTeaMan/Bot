package com.github.richteaman.bot.controllers;

import com.github.richteaman.bot.services.GpioService;
import com.pi4j.io.gpio.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;

@Controller
public class GpioRestController {

    private final GpioService gpioService;

    @Autowired
    public GpioRestController(GpioService gpioService) {
        this.gpioService = gpioService;
    }

    @GetMapping(path = "/gpio")
    @ResponseBody
    public String cam(@RequestHeader HttpHeaders headers)
            throws InterruptedException {

        System.out.println("<--Pi4J--> GPIO Control Example ... started.");

        System.out.println("--> GPIO state should be: ON");

        Thread.sleep(5000);

        // turn off gpio pin #01
        gpioService.getPin1().low();
        System.out.println("--> GPIO state should be: OFF");

        Thread.sleep(5000);

        // toggle the current state of gpio pin #01 (should turn on)
        gpioService.getPin1().toggle();
        System.out.println("--> GPIO state should be: ON");

        Thread.sleep(5000);

        // toggle the current state of gpio pin #01  (should turn off)
        gpioService.getPin1().toggle();
        System.out.println("--> GPIO state should be: OFF");

        Thread.sleep(5000);

        // turn on gpio pin #01 for 1 second and then off
        System.out.println("--> GPIO state should be: ON for only 1 second");
        gpioService.getPin1().pulse(1000, true); // set second argument to 'true' use a blocking call

        System.out.println("Exiting ControlGpioExample");

        return "hellgpio";
    }

    @GetMapping(path="/pwm1")
    @ResponseBody
    public String UpdatePwm(@RequestParam("pwm") int pwm) {

        gpioService.getPwm1().setPwm(pwm);
        Integer result =  gpioService.getPwm1().getPwm();
        return result.toString();
    }
}