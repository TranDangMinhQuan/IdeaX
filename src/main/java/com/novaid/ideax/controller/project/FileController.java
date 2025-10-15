package com.novaid.ideax.controller.project;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/uploads") // URL gốc cho tất cả file
public class FileController {

    private final Path fileStorageLocation = Paths.get("uploads").toAbsolutePath().normalize();

    // URL sẽ có dạng /uploads/project-files/ten-file.pdf
    @GetMapping("/{subDir}/{fileName:.+}")
    public ResponseEntity<Resource> serveFile(@PathVariable String subDir, @PathVariable String fileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(subDir).resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                // Trả về file để trình duyệt hiển thị trực tiếp (inline)
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                        .contentType(MediaType.parseMediaType("application/octet-stream")) // Kiểu mặc định
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}