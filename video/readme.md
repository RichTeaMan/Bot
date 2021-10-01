# Video Stream

Command to start a video stream server:

```bash
cvlc v4l2:///dev/video2 --sout "#transcode{vcodec=theo}:http{mux=ogg,dst=:9222}"
```

This starts a server on port 9222. Connect to it with VLC or a web browser.

