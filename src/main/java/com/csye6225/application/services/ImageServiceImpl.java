package com.csye6225.application.services;

import com.csye6225.application.objects.Image;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.apache.http.entity.ContentType.*;

@Service
public class ImageServiceImpl implements ImageService{
    private final FileStore fileStore;

    public ImageServiceImpl(FileStore fileStore) {
        this.fileStore = fileStore;
    }

    @Override
    public Image saveImage(String path, String fileName, MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalStateException("Cannot upload empty file");
        }
        //Check if the file is an image
        if (!Arrays.asList(IMAGE_PNG.getMimeType(),
                IMAGE_BMP.getMimeType(),
                IMAGE_GIF.getMimeType(),
                IMAGE_JPEG.getMimeType()).contains(file.getContentType())) {
            throw new IllegalStateException("FIle uploaded is not an image");
        }
        //get file metadata
        Map<String, String> metadata = new HashMap<>();
        metadata.put("Content-Type", file.getContentType());
        metadata.put("Content-Length", String.valueOf(file.getSize()));
        try {
            String url=  fileStore.upload(path, fileName, Optional.of(metadata), file.getInputStream());
        } catch (IOException e) {
            throw new IllegalStateException("Failed to upload file", e);
        }

        return null;
    }

    public Image deleteImage(String path, String fileName) {
        try {
            fileStore.delete(path,fileName);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to upload file", e);
        }
        return null;
    }
}
