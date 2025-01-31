package yuredev.street_eye_backend.controllers;

import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

@RestController
public class CameraStreamController {
    private static final String VIDEO_DIRECTORY = "src/main/resources/videos/";

    @GetMapping("/video")
    public ResponseEntity<Resource> getPlaylist() throws IOException {
        Path filePath = Paths.get(VIDEO_DIRECTORY).resolve("video.m3u8");

        if (!Files.exists(filePath)) {
            return ResponseEntity.notFound().build();
        }
        System.out.println("getVideo");
        Resource resource = new UrlResource(filePath.toUri());
        return ResponseEntity.ok()
                .contentType(MediaType.valueOf("application/vnd.apple.mpegurl"))
                .body(resource);
    }

    @GetMapping("/{segmentName}.ts")
    public ResponseEntity<Resource> getSegment(@PathVariable String segmentName) throws IOException {
        Path filePath = Paths.get(VIDEO_DIRECTORY).resolve(segmentName + ".ts");

        if (!Files.exists(filePath)) {
            return ResponseEntity.notFound().build();
        }
        System.out.println("getSegment" + " " + filePath);
        Resource resource = new UrlResource(filePath.toUri());
        return ResponseEntity.ok()
                .contentType(MediaType.valueOf("video/mp2t"))
                .body(resource);
    }
}
