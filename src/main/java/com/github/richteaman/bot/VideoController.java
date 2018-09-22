package com.github.richteaman.bot;

import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import org.springframework.core.io.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

@Controller
public class VideoController {

    @GetMapping(path = "/plain", produces = "video/mp4")
    @ResponseBody
    public FileSystemResource plain() {

        return new FileSystemResource("D:/Projects/Bot/Test.mp4");
    }

}
