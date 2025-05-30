package com.miniproject.rookiejangter.service;

import com.miniproject.rookiejangter.controller.dto.PostDTO;
import com.miniproject.rookiejangter.entity.*;
import com.miniproject.rookiejangter.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ImageRepository imageRepository;
    private final DibsRepository dibsRepository;
    private final BumpRepository bumpRepository;

    @Transactional
    public PostDTO.Response createPost(PostDTO.Request requestDto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다: " + userId));
        Category category = categoryRepository.findById(requestDto.getCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("카테고리를 찾을 수 없습니다: " + requestDto.getCategoryId()));

        Post post = Post.builder()
                .user(user)
                .category(category)
                .title(requestDto.getTitle())
                .content(requestDto.getContent())
                .price(requestDto.getPrice())
                .viewCount(0)
                .isBumped(false)
                .isReserved(false)
                .isCompleted(false)
                .build();
        Post savedPost = postRepository.save(post);

        List<Image> savedImages = new ArrayList<>();
        if (requestDto.getImages() != null && !requestDto.getImages().isEmpty()) {
            for (String imageUrl : requestDto.getImages()) {
                Image image = Image.builder()
                        .post(savedPost)
                        .imageUrl(imageUrl)
                        .build();
                savedImages.add(imageRepository.save(image));
            }
        }
        return mapToPostDTOResponse(savedPost, savedImages, userId);
    }

    @Transactional
    public PostDTO.Response getPostById(Long postId, Long currentUserId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다: " + postId));

        post.setViewCount(post.getViewCount() + 1);
        postRepository.save(post);

        List<Image> images = imageRepository.findByPost_PostId(postId);
        return mapToPostDTOResponse(post, images, currentUserId);
    }

    @Transactional
    public PostDTO.Response updatePost(Long postId, PostDTO.UpdateRequest requestDto, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다: " + postId));

        if (!post.getUser().getUserId().equals(userId)) {
            throw new SecurityException("게시글 수정 권한이 없습니다.");
        }

        if (requestDto.getTitle() != null) post.setTitle(requestDto.getTitle());
        if (requestDto.getContent() != null) post.setContent(requestDto.getContent());
        if (requestDto.getPrice() != null) post.setPrice(requestDto.getPrice());


        if (requestDto.getCategoryId() != null) {
            Category category = categoryRepository.findById(requestDto.getCategoryId())
                    .orElseThrow(() -> new EntityNotFoundException("카테고리를 찾을 수 없습니다: " + requestDto.getCategoryId()));
            post.setCategory(category);
        }

        List<Image> updatedImages;
        if (requestDto.getImages() != null) {
            imageRepository.deleteAll(imageRepository.findByPost_PostId(postId));
            updatedImages = new ArrayList<>();
            for (String imageUrl : requestDto.getImages()) {
                Image image = Image.builder()
                        .post(post)
                        .imageUrl(imageUrl)
                        .build();
                updatedImages.add(imageRepository.save(image));
            }
        } else {
            updatedImages = imageRepository.findByPost_PostId(postId);
        }

        Post updatedPost = postRepository.save(post);
        return mapToPostDTOResponse(updatedPost, updatedImages, userId);
    }

    @Transactional
    public void deletePost(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다: " + postId));

        if (!post.getUser().getUserId().equals(userId)) {
            throw new SecurityException("게시글 삭제 권한이 없습니다.");
        }

        imageRepository.deleteAll(imageRepository.findByPost_PostId(postId));
        dibsRepository.deleteAll(dibsRepository.findByPost_PostId(postId));
        bumpRepository.deleteAll(bumpRepository.findByPost_PostId(postId));


        postRepository.delete(post);
    }

    @Transactional(readOnly = true)
    public PostDTO.PostListData getAllPosts(Pageable pageable, Long currentUserId) {
        Page<Post> postPage = postRepository.findAllByOrderByCreatedAtDesc(pageable); //
        return convertToPostListData(postPage, currentUserId);
    }

    @Transactional(readOnly = true)
    public PostDTO.PostListData getPostsByCategory(Integer categoryId, Pageable pageable, Long currentUserId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new EntityNotFoundException("카테고리를 찾을 수 없습니다: " + categoryId));
        Page<Post> postPage = postRepository.findByCategory(category, pageable); //
        return convertToPostListData(postPage, currentUserId);
    }

    @Transactional(readOnly = true)
    public PostDTO.PostListData getPostsByUser(Long targetUserId, Pageable pageable, Long currentUserId) {
        User user = userRepository.findById(targetUserId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다: " + targetUserId));
        Page<Post> postPage = postRepository.findByUser(user, pageable); //
        return convertToPostListData(postPage, currentUserId);
    }

    @Transactional(readOnly = true)
    public PostDTO.PostListData searchPostsByTitle(String title, Pageable pageable, Long currentUserId) {
        List<Post> postList = postRepository.findByTitleContainsIgnoreCase(title); //

        Page<Post> postPage = paginateList(postList, pageable);
        return convertToPostListData(postPage, currentUserId);
    }

    @Transactional(readOnly = true)
    public PostDTO.PostListData searchPostsByKeyword(String keyword, Pageable pageable, Long currentUserId) {
        List<Post> postList = postRepository.findByTitleContainsIgnoreCaseOrContentContainsIgnoreCase(keyword, keyword);

        Page<Post> postPage = paginateList(postList, pageable);
        return convertToPostListData(postPage, currentUserId);
    }

    private Page<Post> paginateList(List<Post> list, Pageable pageable) {
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), list.size());
        if (start > end) {
            return new PageImpl<>(Collections.emptyList(), pageable, list.size());
        }
        return new PageImpl<>(list.subList(start, end), pageable, list.size());
    }

    private PostDTO.PostListData convertToPostListData(Page<Post> postPage, Long currentUserId) {
        List<PostDTO.Response> postResponses = postPage.getContent().stream()
                .map(post -> {
                    List<Image> images = imageRepository.findByPost_PostId(post.getPostId());
                    return mapToPostDTOResponse(post, images, currentUserId);
                })
                .collect(Collectors.toList());

        PostDTO.PostListPagination pagination = PostDTO.PostListPagination.builder()
                .page(postPage.getNumber())
                .size(postPage.getSize())
                .totalElements(postPage.getTotalElements())
                .totalPages(postPage.getTotalPages())
                .first(postPage.isFirst())
                .last(postPage.isLast())
                .build();

        return PostDTO.PostListData.builder()
                .content(postResponses)
                .pagination(pagination)
                .build();
    }

    private PostDTO.Response mapToPostDTOResponse(Post post, List<Image> images, Long currentUserId) {
        List<PostDTO.ImageResponse> imageResponses = images.stream()
                .map(PostDTO.ImageResponse::fromEntity)
                .collect(Collectors.toList());

        boolean isLiked = false;
        if (currentUserId != null) {
            isLiked = dibsRepository.existsByUser_UserIdAndPost_PostId(currentUserId, post.getPostId());
        }
        long likeCount = dibsRepository.findByPost_PostId(post.getPostId()).size();


        return PostDTO.Response.builder()
                .id(post.getPostId())
                .title(post.getTitle())
                .description(post.getContent())
                .price(post.getPrice())
                .categoryName(post.getCategory() != null ? post.getCategory().getCategoryName() : null)
                .status(post.getIsCompleted() != null && post.getIsCompleted() ? "COMPLETED" :
                        post.getIsReserved() != null && post.getIsReserved() ? "RESERVED" : "SALE")
                .images(imageResponses)
                .seller(PostDTO.SellerInfo.fromEntity(post.getUser()))
                .createdAt(post.getCreatedAt().atOffset(java.time.ZoneOffset.UTC))
                .updatedAt(post.getUpdatedAt() != null ? post.getUpdatedAt().atOffset(java.time.ZoneOffset.UTC) : null)
                .viewCount(post.getViewCount())
                .likeCount((int) likeCount)
                .isLiked(isLiked)
                .build();
    }

    @Transactional
    public void updatePostStatus(Long postId, Boolean isReserved, Boolean isCompleted, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다: " + postId));

        if (!post.getUser().getUserId().equals(userId)) {
            throw new SecurityException("게시글 상태 변경 권한이 없습니다.");
        }
        if (isReserved != null) {
            post.setIsReserved(isReserved);
        }
        if (isCompleted != null) {
            post.setIsCompleted(isCompleted);
        }
        postRepository.save(post);
    }
}