package com.github.richteaman.bot;

import com.github.sarxos.webcam.Webcam;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import org.springframework.core.io.Resource;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.concurrent.Executors;

@Controller
public class VideoController {

    private StreamServerAgent streamServerAgent;
    public VideoController() {
        Webcam webcam = Webcam.getDefault();



        Dimension dimension = new Dimension(320, 240);
        webcam.setViewSize(dimension);

        webcam.open();

        streamServerAgent = new StreamServerAgent(webcam, dimension);
        streamServerAgent.start();
    }

    @GetMapping(path = "/plain", produces = "video/mp4")
    @ResponseBody
    public FileSystemResource plain() {

        return new FileSystemResource("D:/Projects/Bot/TotalRecall.mp4");
    }

    @GetMapping(path = "/w", produces = "video/mp4")
    @ResponseBody
    public ByteArrayResource w() {

        return new ByteArrayResource(streamServerAgent.getOutputStream().toByteArray());
    }


    @GetMapping(path = "/cam", produces = "video/mp4")
    @ResponseBody
    public ResponseEntity<StreamingResponseBody> cam(@RequestHeader HttpHeaders headers)
            throws IOException {


        VideoStreamingResponseBody videoStreamingResponseBody = new VideoStreamingResponseBody(streamServerAgent.getOutputStream());

List<HttpRange> range = headers.getRange();
//range.get(0).

        return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                .header("Transfer-Encoding", "chunked")
                .header("Connection","keep-alive")
                .contentType(MediaType.valueOf("video/mp4"))
                .body(videoStreamingResponseBody);
    }

}
