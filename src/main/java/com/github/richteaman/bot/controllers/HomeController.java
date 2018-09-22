package com.github.richteaman.bot.controllers;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.LoggerContext;

/**
 * Home controller
 */
@Controller
@EnableAutoConfiguration
public class HomeController {

    /** Logger. */
    private final static Logger logger = LoggerFactory.getLogger(HomeController.class);

    @RequestMapping("/")
    public ModelAndView home() {
        logger.info("Home called.");
        return new ModelAndView("home");
    }

}