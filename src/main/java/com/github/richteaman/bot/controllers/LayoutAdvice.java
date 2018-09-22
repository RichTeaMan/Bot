package com.github.richteaman.bot.controllers;

import java.io.IOException;
import java.io.Writer;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template.Fragment;

@ControllerAdvice
public class LayoutAdvice {

    @ModelAttribute("layout")
    public Mustache.Lambda layout() {
        return new Layout();
    }

}

class Layout implements Mustache.Lambda {

    String body;

    @Override
    public void execute(final Fragment frag, final Writer out) throws IOException {
        body = frag.execute();
    }

}
