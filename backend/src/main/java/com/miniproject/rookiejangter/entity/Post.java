package com.miniproject.rookiejangter.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Table(name = "posts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Post extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long postId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @NotBlank(message = "제목은 필수입니다.")
    @Size(max = 50, message = "제목은 최대 50자까지 가능합니다.")
    @Column(name = "title", length = 50, nullable = false)
    private String title;

    @NotBlank(message = "내용은 필수입니다.")
    @Size(max = 255, message = "내용은 최대 255자까지 가능합니다.")
    @Column(name = "content", length = 255, nullable = false)
    private String content;

    @Column(name = "view_count")
    private Integer viewCount;

    @Column(name = "is_bumped")
    private Boolean isBumped;

    @Column(name = "is_reserved")
    private Boolean isReserved;

    @Column(name = "is_completed")
    private Boolean isCompleted;

    @Override
    public String toString() {
        return "Post{" +
                "postId=" + postId +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", viewCount=" + viewCount +
                ", isBumped=" + isBumped +
                ", isReserved=" + isReserved +
                ", isCompleted=" + isCompleted +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Post post = (Post) o;
        return postId != null && postId.equals(post.postId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}