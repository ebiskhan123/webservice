package com.csye6225.application.services;

import com.csye6225.application.objects.Image;
import org.springframework.web.multipart.MultipartFile;

public interface ImageService
{
    Image saveImage(String path, String fileName, MultipartFile file);
}
