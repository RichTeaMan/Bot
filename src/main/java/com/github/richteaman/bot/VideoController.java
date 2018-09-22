package com.github.richteaman.bot;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.ds.v4l4j.V4l4jDriver;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
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

    static {
        String arch = System.getProperty("os.arch");
        if (arch.contains("arm")) {
            Webcam.setDriver(new V4l4jDriver());
        }
    }

    private Webcam webcam = Webcam.getDefault();

    public VideoController() {


        Dimension dimension = new Dimension(320, 240);
        webcam.setViewSize(dimension);

        webcam.open();
    }

    @GetMapping(path = "/cam", produces = "image/jpg")
    @ResponseBody
    public ResponseEntity<ByteArrayResource> cam(@RequestHeader HttpHeaders headers)
            throws IOException {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(webcam.getImage(), "JPG", byteArrayOutputStream);
        ByteArrayResource byteArrayResource = new ByteArrayResource(byteArrayOutputStream.toByteArray());

        return ResponseEntity.ok()
                .header("Cache-Control", "no-cache, no-store, must-revalidate")
                .body(byteArrayResource);
    }

}
