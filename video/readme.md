# Video Stream

Command to start a video stream server:

```bash
cvlc v4l2:///dev/video2 --sout "#transcode{vcodec=theo}:http{mux=ogg,dst=:9222}"
```

This starts a server on port 9222. Connect to it with VLC or a web browser.

And now in Docker (the device flag didn't work?):

```bash
sudo docker run -it --privileged -v /dev/video2:/dev/video2 -p 9222:9222 quay.io/galexrt/vlc:latest v4l2:///dev/video2 --sout "#transcode{vcodec=theo,vb=1600,scale=1}:http{mux=ogg,dst=:9222}"
```
