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

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.imageio.ImageIO;
import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Controller
public class VideoController {

    private Webcam webcam;

    @PostConstruct
    public void init() {

        String arch = System.getProperty("os.arch");
        if (arch.contains("arm")) {
            Webcam.setDriver(new V4l4jDriver());
        }

        int attemptLimit = 10;
        Exception videoException = null;

        for(int attempt = 0; attempt < attemptLimit; attempt++) {

            videoException = null;
            try {
                webcam = Webcam.getDefault();
                break;
            }
            catch(Exception ex) {
                videoException = ex;
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    throw new IllegalStateException(e);
                }
            }
        }
        if (null != videoException) {
            throw new IllegalStateException("Cannot instantiate video.", videoException);
        }

        Dimension dimension = new Dimension(640, 480);
        webcam.setViewSize(dimension);

        webcam.open();
    }

    @PreDestroy
    public void shutdown() {
        webcam.close();
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
