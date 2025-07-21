package com.TeachCode.Gestion_Courses.services;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface IFileStorageService {
    String uploadFile(MultipartFile file) throws IOException;
    void deleteFile(String fileUrl);

    // new resource methods
    String updloadDocument(MultipartFile file) throws IOException;
    String uploadVideo(MultipartFile file) throws IOException;
    void deleteDocument(String fileUrl);
    void deleteVideo(String fileUrl);

    String uploadQRCode(byte[] qrCode,String s) throws IOException;

}

