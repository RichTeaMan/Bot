package com.github.richteaman.bot;

import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class VideoStreamingResponseBody implements StreamingResponseBody {

    private ByteArrayOutputStream outputStream;

    private int currentPosition = 0;

    public VideoStreamingResponseBody(ByteArrayOutputStream outputStream) {

        this.outputStream = outputStream;
    }

    @Override
    public void writeTo(OutputStream responseStream) throws IOException {

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        int outputStreamSize = outputStream.size();
        if (currentPosition < outputStreamSize) {
            byte[] outputArray = outputStream.toByteArray();
            byte[] responseArray = new byte[outputStreamSize - currentPosition];
            System.arraycopy(outputArray, currentPosition, responseArray, 0, outputStreamSize - currentPosition);

            responseStream.write(responseArray);

            currentPosition = outputStreamSize;

            System.out.println("written.");
        }

        responseStream.flush();

        System.out.println("body write.");
    }

    public int getCurrentPosition() {
        return currentPosition;
    }

    public void setCurrentPosition(int currentPosition) {
        this.currentPosition = currentPosition;
    }
}
