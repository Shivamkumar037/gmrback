package com.app.helper;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class FileUploadService {

    @Autowired
    private Cloudinary cloudinary;

    public String uploadFile(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return null;
        }
        // Upload to Cloudinary
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
        
        // Return the secure URL (https link)
        return (String) uploadResult.get("secure_url");
    }
}