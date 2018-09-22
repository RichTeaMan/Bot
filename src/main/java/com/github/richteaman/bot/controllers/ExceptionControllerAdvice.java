package com.github.richteaman.bot.controllers;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class ExceptionControllerAdvice {

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ModelAndView handleThrowable(final Throwable ex) {

        final String errorMessage = ExceptionUtils.getMessage(ex);
        final String rootCause = ExceptionUtils.getRootCauseMessage(ex);
        final String errorStacktrace = ExceptionUtils.getStackTrace(ex);
        final ModelAndView modelAndView = new ModelAndView("errorPage");
        modelAndView.addObject("errorMessage", errorMessage);
        modelAndView.addObject("rootCause", rootCause);
        modelAndView.addObject("stackTrace", errorStacktrace);
        return modelAndView;
    }

}