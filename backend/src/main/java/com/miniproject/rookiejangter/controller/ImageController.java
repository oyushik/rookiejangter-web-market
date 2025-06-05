package com.miniproject.rookiejangter.controller;

import com.miniproject.rookiejangter.controller.dto.ImageDTO;
import com.miniproject.rookiejangter.exception.BusinessException;
import com.miniproject.rookiejangter.exception.ErrorCode;
import com.miniproject.rookiejangter.service.ImageService;
import com.miniproject.rookiejangter.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/images")
public class ImageController {

    private final ImageService imageService;
    private final FileStorageService fileStorageService;

    @PostMapping
    public ResponseEntity<List<ImageDTO.Response>> uploadImages(
            @RequestParam("productId") Long productId,
            @RequestPart("images") List<MultipartFile> files
    ) {
        if (files == null || files.isEmpty() || files.stream().allMatch(MultipartFile::isEmpty)) {
            return ResponseEntity.badRequest().body(new ArrayList<>());
        }

        List<ImageDTO.Response> uploadedImages = new ArrayList<>();

        for (MultipartFile file : files) {
            if (file.isEmpty()) {
                continue;
            }
            try {
                // 1. 파일 저장 서비스 호출: BusinessException을 던질 수 있음
                String imageUrl = fileStorageService.uploadFile(file);

                // 2. ImageService 호출: BusinessException을 던질 수 있음
                ImageDTO.Response imageResponse = imageService.createImage(productId, imageUrl);
                uploadedImages.add(imageResponse);

            } catch (BusinessException e) { // IOException 대신 BusinessException만 catch
                System.err.println("Business Exception during image upload/creation: " + e.getMessage() + " Code: " + e.getErrorCode());
                // 파일 업로드 실패 또는 DB 저장 실패 시 해당 에러 코드를 클라이언트에게 전달하거나 로그
                // 개별 파일 처리 실패 시 uploadedImages에 추가하지 않고 다음 파일로 진행
            } catch (Exception e) { // 예측 못한 다른 런타임 예외
                System.err.println("Unexpected error during image processing: " + e.getMessage());
                // 이 또한 처리 방식은 서비스 정책에 따라 달라집니다.
            }
        }

        if (uploadedImages.isEmpty() && !files.isEmpty()) {
            // 파일을 받았지만, 모든 파일 처리에서 오류가 발생한 경우 (BusinessException 포함)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ArrayList<>());
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(uploadedImages);
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<ImageDTO.Response>> getImagesByProductId(@PathVariable Long productId) {
        List<ImageDTO.Response> images = imageService.getImagesByProductId(productId);
        if (images.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(images);
    }

    @GetMapping("/{imageId}")
    public ResponseEntity<ImageDTO.Response> getImageByImageId(@PathVariable Long imageId) {
        // ImageService.getImageByImageId는 BusinessException을 던질 수 있음
        ImageDTO.Response image = imageService.getImageByImageId(imageId);
        return ResponseEntity.ok(image);
    }

    @DeleteMapping("/{imageId}")
    public ResponseEntity<Void> deleteImage(@PathVariable Long imageId) {
        try {
            ImageDTO.Response imageToDelete = imageService.getImageByImageId(imageId);

            // 실제 파일 저장소에서 파일 삭제 (BusinessException을 던질 수 있음)
            fileStorageService.deleteFile(imageToDelete.getImageUrl());

            // DB에서 이미지 정보 삭제
            imageService.deleteImage(imageId);

            return ResponseEntity.noContent().build();
        } catch (BusinessException e) { // BusinessException만 catch
            System.err.println("Business Exception during image deletion: " + e.getMessage() + " Code: " + e.getErrorCode());
            if (e.getErrorCode() == ErrorCode.IMAGE_NOT_FOUND) {
                return ResponseEntity.notFound().build();
            } else if (e.getErrorCode() == ErrorCode.FILE_DELETE_FAILED) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
            throw e; // 그 외 BusinessException은 Global Exception Handler로
        } catch (Exception e) {
            System.err.println("Unexpected error during image deletion: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/product/{productId}")
    public ResponseEntity<Void> deleteImagesByProductId(@PathVariable Long productId) {
        try {
            List<ImageDTO.Response> imagesToDelete = imageService.getImagesByProductId(productId);
            if (imagesToDelete.isEmpty()) {
                return ResponseEntity.noContent().build();
            }

            for (ImageDTO.Response image : imagesToDelete) {
                try {
                    fileStorageService.deleteFile(image.getImageUrl());
                } catch (BusinessException e) {
                    System.err.println("Failed to delete image file from storage: " + image.getImageUrl() + ", Error: " + e.getMessage() + " Code: " + e.getErrorCode());
                    // 이미지 개별 삭제 실패는 전체 프로세스를 멈추지 않도록 처리
                }
            }

            imageService.deleteImagesByProductId(productId);

            return ResponseEntity.noContent().build();
        } catch (BusinessException e) {
            System.err.println("Business Exception during product images deletion: " + e.getMessage() + " Code: " + e.getErrorCode());
            if (e.getErrorCode() == ErrorCode.PRODUCT_NOT_FOUND) {
                return ResponseEntity.notFound().build();
            }
            throw e;
        } catch (Exception e) {
            System.err.println("Unexpected error during product images deletion: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}