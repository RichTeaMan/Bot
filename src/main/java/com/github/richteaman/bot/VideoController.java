package com.github.richteaman.bot;

import com.github.sarxos.webcam.Webcam;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Controller
public class VideoController {

    private Webcam webcam = Webcam.getDefault();

    public VideoController() {


        Dimension dimension = new Dimension(320, 240);
        webcam.setViewSize(dimension);

        webcam.open();
    }

    @GetMapping(path = "/plain", produces = "video/mp4")
    @ResponseBody
    public FileSystemResource plain() {

        return new FileSystemResource("D:/Projects/Bot/Test.mp4");
    }

    @GetMapping(path = "/cam", produces = "image/jpg")
    @ResponseBody
    public ByteArrayResource cam(@RequestHeader HttpHeaders headers)
            throws IOException {


        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(webcam.getImage(), "JPG", baos);

        return new ByteArrayResource(baos.toByteArray());
    }

}
