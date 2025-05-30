package com.miniproject.rookiejangter.service;

import com.miniproject.rookiejangter.controller.dto.ImageDTO;
import com.miniproject.rookiejangter.entity.Image;
import com.miniproject.rookiejangter.entity.Post;
import com.miniproject.rookiejangter.repository.ImageRepository;
import com.miniproject.rookiejangter.repository.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final ImageRepository imageRepository;
    private final PostRepository postRepository;

    @Transactional
    public ImageDTO.Response createImage(Long postId, String imageUrl) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다: " + postId));

        Image image = Image.builder()
                .post(post)
                .imageUrl(imageUrl)
                .build();
        Image savedImage = imageRepository.save(image);
        return ImageDTO.Response.fromEntity(savedImage);
    }

    @Transactional(readOnly = true)
    public List<ImageDTO.Response> getImagesByPostId(Long postId) {
        if (!postRepository.existsById(postId)) {
            throw new EntityNotFoundException("게시글을 찾을 수 없습니다: " + postId);
        }
        return imageRepository.findByPost_PostId(postId).stream()
                .map(ImageDTO.Response::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteImage(Long imageId) {
        Image image = imageRepository.findById(imageId)
                .orElseThrow(() -> new EntityNotFoundException("이미지를 찾을 수 없습니다: " + imageId));
        imageRepository.delete(image);
    }

    @Transactional
    public void deleteImagesByPostId(Long postId) {
        if (!postRepository.existsById(postId)) {
            throw new EntityNotFoundException("게시글을 찾을 수 없습니다: " + postId);
        }
        List<Image> images = imageRepository.findByPost_PostId(postId);
        imageRepository.deleteAll(images);
    }
}