package yuredev.street_eye_backend.controllers;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

@RestController
@RequestMapping("/video")
public class CameraStreamController {
    @Value("${STREAM_OUTPUT_DIRECTORY:../stream/output}")
    private String videoDirectory;

    @GetMapping()
    public ResponseEntity<?> getPlaylist() throws IOException {
        try {
            var filePath = Paths.get(videoDirectory).resolve("stream.m3u8");
            if (!Files.exists(filePath)) {
                return ResponseEntity.notFound().build();
            }
            var resource = new UrlResource(filePath.toUri());
            return ResponseEntity.ok()
                    .contentType(MediaType.valueOf("application/vnd.apple.mpegurl"))
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("");
        }
    }

    @GetMapping("{nomeArquivo}")
    public ResponseEntity<Resource> getSegment(@PathVariable String nomeArquivo) throws IOException {
        var filePath = Paths.get(videoDirectory).resolve(nomeArquivo);

        if (!Files.exists(filePath)) {
            return ResponseEntity.notFound().build();
        }
        var resource = new UrlResource(filePath.toUri());
        return ResponseEntity.ok()
                .contentType(MediaType.valueOf("video/mp2t"))
                .body(resource);
    }
}
