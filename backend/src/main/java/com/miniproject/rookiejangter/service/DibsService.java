package com.miniproject.rookiejangter.service;

import com.miniproject.rookiejangter.controller.dto.DibsDTO;
import com.miniproject.rookiejangter.entity.Dibs;
import com.miniproject.rookiejangter.entity.Post;
import com.miniproject.rookiejangter.entity.User;
import com.miniproject.rookiejangter.repository.DibsRepository;
import com.miniproject.rookiejangter.repository.PostRepository;
import com.miniproject.rookiejangter.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DibsService {

    private final DibsRepository dibsRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    @Transactional
    public DibsDTO.Response addDibs(Long userId, Long postId) {
        if (dibsRepository.existsByUser_UserIdAndPost_PostId(userId, postId)) {
            throw new IllegalStateException("이미 찜한 상품입니다.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다: " + userId));
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다: " + postId));

        Dibs dibs = Dibs.builder()
                .user(user)
                .post(post)
                .addedAt(LocalDateTime.now())
                .build();
        Dibs savedDibs = dibsRepository.save(dibs);
        return DibsDTO.Response.fromEntity(savedDibs, true);
    }

    @Transactional
    public void removeDibs(Long userId, Long postId) {
        if (!dibsRepository.existsByUser_UserIdAndPost_PostId(userId, postId)) {
            throw new EntityNotFoundException("찜한 내역을 찾을 수 없습니다.");
        }
        dibsRepository.deleteByUser_UserIdAndPost_PostId(userId, postId);
    }

    @Transactional(readOnly = true)
    public DibsDTO.Response getDibsStatus(Long userId, Long postId) {
        boolean isLiked = dibsRepository.existsByUser_UserIdAndPost_PostId(userId, postId);
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다: " + postId));

        if (isLiked) {
            List<Dibs> dibsForPost = dibsRepository.findByPost_PostId(postId);
            Optional<Dibs> userDibsOpt = dibsForPost.stream()
                    .filter(d -> d.getUser().getUserId().equals(userId))
                    .findFirst();
            if (userDibsOpt.isPresent()) {
                return DibsDTO.Response.fromEntity(userDibsOpt.get(), true);
            }
            // 이론상 existsByUser_UserIdAndPost_PostId가 true면 여기서 찾아야 하지만, 동시성 문제 등 예외 케이스 방어
            Dibs tempDibs = Dibs.builder().post(post).addedAt(LocalDateTime.now()).build(); // 임시로 addedAt 설정
            return DibsDTO.Response.fromEntity(tempDibs, true); // isLiked는 true지만 정확한 dibs 객체를 못찾은 경우
        } else {
            Dibs tempDibs = Dibs.builder().post(post).build();
            return DibsDTO.Response.fromEntity(tempDibs, false);
        }
    }

    @Transactional(readOnly = true)
    public List<DibsDTO.Response.DibbedPost> getUserDibsList(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다: " + userId));

        List<Dibs> dibsList = dibsRepository.findByUser_UserId(userId);

        return dibsList.stream()
                .map(DibsDTO.Response.DibbedPost::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public long getDibsCountForPost(Long postId) {
        postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다: " + postId));
        return dibsRepository.findByPost_PostId(postId).size();
    }
}