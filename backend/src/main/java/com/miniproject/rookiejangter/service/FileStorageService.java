package com.miniproject.rookiejangter.service;

import com.miniproject.rookiejangter.exception.BusinessException; // BusinessException 임포트
import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    String uploadFile(MultipartFile file) throws BusinessException;
    void deleteFile(String fileUrl) throws BusinessException;
}