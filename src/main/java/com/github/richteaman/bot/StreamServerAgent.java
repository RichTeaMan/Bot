package com.github.richteaman.bot;

import com.github.richteaman.bot.handler.H264StreamEncoder;
import com.github.sarxos.webcam.Webcam;
import org.jboss.netty.buffer.ChannelBuffer;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.util.concurrent.*;

public class StreamServerAgent {
    protected final Webcam webcam;
    protected final Dimension dimension;
    //I just move the stream encoder out of the channel pipeline for the performance
    protected final H264StreamEncoder h264StreamEncoder;
    protected ScheduledExecutorService timeWorker;
    protected ExecutorService encodeWorker;
    protected int FPS = 25;
    protected ScheduledFuture<?> imageGrabTaskFuture;

    private ByteArrayOutputStream outputStream;

    public StreamServerAgent(Webcam webcam, Dimension dimension) {
        super();
        this.webcam = webcam;
        this.dimension = dimension;
        this.timeWorker = new ScheduledThreadPoolExecutor(1);
        this.encodeWorker = Executors.newSingleThreadExecutor();
        this.h264StreamEncoder = new H264StreamEncoder(dimension, false);
        outputStream = new ByteArrayOutputStream();

        System.out.println("stream server agent.");
    }


    public int getFPS() {
        return FPS;
    }

    public void setFPS(int fPS) {
        FPS = fPS;
    }

    public ByteArrayOutputStream getOutputStream() {
        return outputStream;
    }

    public void start() {
        //do some thing
        Runnable imageGrabTask = new ImageGrabTask();
        ScheduledFuture<?> imageGrabFuture =
                timeWorker.scheduleWithFixedDelay(imageGrabTask,
                        0,
                        1000 / FPS,
                        TimeUnit.MILLISECONDS);
        imageGrabTaskFuture = imageGrabFuture;
    }

    public void stop() {
        timeWorker.shutdown();
        encodeWorker.shutdown();
    }


    protected volatile long frameCount = 0;

    private class ImageGrabTask implements Runnable {

        @Override
        public void run() {
            BufferedImage bufferedImage = webcam.getImage();

            /**
             * using this when the h264 encoder is added to the pipeline
             * */
            //channelGroup.write(bufferedImage);
            /**
             * using this when the h264 encoder is inside this class
             * */
            //System.out.println("image grab.");
            encodeWorker.execute(new EncodeTask(bufferedImage));
        }

    }

    private class EncodeTask implements Runnable {
        private final BufferedImage image;

        public EncodeTask(BufferedImage image) {
            super();
            this.image = image;
        }

        @Override
        public void run() {
            try {
               // System.out.println("encode.");
                ChannelBuffer msg = h264StreamEncoder.encode(image);



                if (msg != null) {
                    outputStream.write(msg.array());

                    FileOutputStream fos = new FileOutputStream("D:/Projects/Bot/t", false);
                    fos.write(msg.array());
                    fos.close();
                    throw new Exception();

                   // System.out.println("encode: " + outputStream.size());
                } else {
                 //   System.out.println("null");
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }


}
