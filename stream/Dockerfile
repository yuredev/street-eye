FROM alpine:3.22

RUN apk add --no-cache ffmpeg

RUN mkdir -p /output

COPY ["input/video.mp4", "/input/video.mp4"]

CMD ["ffmpeg", "-re", "-stream_loop", "-1", "-i", "/input/video.mp4", "-c:v", "libx264", "-c:a", "aac", "-f", "hls", "-hls_time", "4", "-hls_list_size", "5", "-hls_flags", "delete_segments+append_list+discont_start", "-hls_segment_filename", "/output/%03d.ts", "-hls_base_url", "/video/", "/output/stream.m3u8"]

