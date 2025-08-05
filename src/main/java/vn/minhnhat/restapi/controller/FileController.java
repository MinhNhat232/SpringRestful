package vn.minhnhat.restapi.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import vn.minhnhat.restapi.domain.response.ResUploadFileDTO;
import vn.minhnhat.restapi.service.FileService;
import vn.minhnhat.restapi.util.error.StorageException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/v1")
public class FileController {

    @Value("${mn7.upload-file.base-uri}")
    private String baseUri;

    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping("/files")
    public ResponseEntity<ResUploadFileDTO> uploadFile(@RequestParam("file") MultipartFile file,
            @RequestParam(name = "folder", required = false) String folder)
            throws URISyntaxException, IOException, StorageException {

        // validate
        if (file == null || file.isEmpty()) {
            throw new StorageException("File is empty, please upload a file");
        }
        String fileName = file.getOriginalFilename();
        List<String> allowedExtensions = Arrays.asList("pdf", "jpg", "jpeg", "png", "docs", "txt");
        boolean isValid = allowedExtensions.stream().anyMatch(item -> fileName.toLowerCase().endsWith(item));

        if (!isValid) {
            throw new StorageException("Invalid file name extension. Only allow " + allowedExtensions.toString());
        }

        this.fileService.createDirectory(baseUri + folder);

        String uploadFile = this.fileService.store(file, folder);

        ResUploadFileDTO res = new ResUploadFileDTO(uploadFile, Instant.now());

        return ResponseEntity.ok().body(res);
    }

}
