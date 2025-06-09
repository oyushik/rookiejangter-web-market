package com.miniproject.rookiejangter.service;

import com.miniproject.rookiejangter.exception.BusinessException; // BusinessException 임포트
import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    /**
     * 파일을 업로드하고 URL을 반환합니다.
     *
     * @param file 업로드할 파일
     * @return 업로드된 파일의 URL
     * @throws BusinessException 파일 업로드 중 오류 발생 시 예외
     */
    String uploadFile(MultipartFile file) throws BusinessException;

    /**
     * 파일을 삭제합니다.
     *
     * @param fileUrl 삭제할 파일의 URL
     * @throws BusinessException 파일 삭제 중 오류 발생 시 예외
     */
    void deleteFile(String fileUrl) throws BusinessException;
}