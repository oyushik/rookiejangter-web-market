package service;

import com.miniproject.rookiejangter.controller.dto.ImageDTO;
import com.miniproject.rookiejangter.entity.Image;
import com.miniproject.rookiejangter.entity.Product;
import com.miniproject.rookiejangter.exception.BusinessException;
import com.miniproject.rookiejangter.exception.ErrorCode;
import com.miniproject.rookiejangter.repository.ImageRepository;
import com.miniproject.rookiejangter.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final ImageRepository imageRepository;
    private final ProductRepository productRepository;

    @Transactional
    public ImageDTO.Response createImage(Long productId, String imageUrl) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND, productId));

        Image image = Image.builder()
                .product(product)
                .imageUrl(imageUrl)
                .build();
        Image savedImage = imageRepository.save(image);
        return ImageDTO.Response.fromEntity(savedImage);
    }

    @Transactional(readOnly = true)
    public ImageDTO.Response getImageByImageId(Long imageId) {
        Image image = imageRepository.findByImageId(imageId)
                .orElseThrow(() -> new BusinessException(ErrorCode.IMAGE_NOT_FOUND, imageId));
        return ImageDTO.Response.fromEntity(image);
    }

    @Transactional(readOnly = true)
    public List<ImageDTO.Response> getImagesByProductId(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new BusinessException(ErrorCode.PRODUCT_NOT_FOUND, productId);
        }
        return imageRepository.findByProduct_ProductId(productId).stream()
                .map(ImageDTO.Response::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteImage(Long imageId) {
        Image image = imageRepository.findById(imageId)
                .orElseThrow(() -> new BusinessException(ErrorCode.IMAGE_NOT_FOUND, imageId));
        imageRepository.delete(image);
    }

    @Transactional
    public void deleteImagesByProductId(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new BusinessException(ErrorCode.PRODUCT_NOT_FOUND, productId);
        }
        List<Image> images = imageRepository.findByProduct_ProductId(productId);
        imageRepository.deleteAll(images);
    }
}