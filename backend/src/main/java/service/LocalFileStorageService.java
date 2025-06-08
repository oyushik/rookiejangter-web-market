package service;

import com.miniproject.rookiejangter.exception.BusinessException;
import com.miniproject.rookiejangter.exception.ErrorCode;
import com.miniproject.rookiejangter.service.FileStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class LocalFileStorageService implements FileStorageService {

    @Value("${image.upload.dir}")
    private String uploadDir;

    @Override
    public String uploadFile(MultipartFile file) throws BusinessException {
        if (file.isEmpty()) {
            throw new BusinessException(ErrorCode.FILE_EMPTY);
        }

        Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String originalFilename = file.getOriginalFilename();
            String fileExtension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String fileName = UUID.randomUUID().toString() + fileExtension;
            Path targetLocation = uploadPath.resolve(fileName);

            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return "/images/" + fileName;
        } catch (IOException e) {
            // IOException 발생 시 BusinessException으로 캡슐화하여 던짐
            throw new BusinessException(ErrorCode.FILE_UPLOAD_FAILED, "File upload failed: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteFile(String fileUrl) throws BusinessException {
        if (fileUrl == null || fileUrl.isEmpty()) {
            return;
        }

        String fileName = fileUrl.replace("/images/", "");
        Path filePath = Paths.get(uploadDir).toAbsolutePath().normalize().resolve(fileName);

        try {
            if (Files.exists(filePath)) {
                Files.delete(filePath);
            } else {
                // 파일이 존재하지 않는 경우를 명시적인 예외로 처리할 수도 있습니다.
                // 여기서는 로그만 남기고 에러는 던지지 않음 (삭제 요청 대상이 없는 경우)
                System.out.println("File not found for deletion: " + filePath.toString());
            }
        } catch (IOException e) {
            // IOException 발생 시 BusinessException으로 캡슐화하여 던짐
            throw new BusinessException(ErrorCode.FILE_DELETE_FAILED, "File deletion failed: " + e.getMessage(), e);
        }
    }
}