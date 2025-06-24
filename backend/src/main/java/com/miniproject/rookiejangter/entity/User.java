package com.miniproject.rookiejangter.entity;

import com.miniproject.rookiejangter.exception.BusinessException;
import com.miniproject.rookiejangter.exception.ErrorCode;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.security.core.GrantedAuthority;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@ToString
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "area_id")
    private Area area;

    @Column(name = "login_id", length = 20, nullable = false, unique = true)
    private String loginId;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "user_name", length = 12, nullable = false)
    private String userName;

    @Column(name = "phone", length = 12, nullable = false, unique = true)
    private String phone;

    @Column(name = "is_banned")
    private Boolean isBanned;

    @Column(name = "is_admin")
    private Boolean isAdmin;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Product> products = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Report> reports = new ArrayList<>();

    @OneToMany(mappedBy = "buyer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> buyerReviews = new ArrayList<>();

    @OneToMany(mappedBy = "seller", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> sellerReviews = new ArrayList<>();

    @OneToMany(mappedBy = "buyer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reservation> buyerReservations = new ArrayList<>();

    @OneToMany(mappedBy = "seller", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reservation> sellerReservations = new ArrayList<>();

    @OneToMany(mappedBy = "buyer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Chat> buyerChats = new ArrayList<>();

    @OneToMany(mappedBy = "seller", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Chat> sellerChats = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Dibs> dibsList = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Notification> notifications = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Ban> bans = new ArrayList<>();

    @OneToMany(mappedBy = "buyer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Complete> buyerCompletes = new ArrayList<>();

    @OneToMany(mappedBy = "seller", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Complete> sellerCompletes = new ArrayList<>();

    public User(String subject, String s, Collection<? extends GrantedAuthority> authorities) {
    }

    /** 
     * 사용자 정보를 업데이트합니다.
     * 
     * @param newArea       새 지역 정보
     * @param newUserName   새 사용자 이름
     * @param newPhone      새 전화번호
     * @throws BusinessException 지역 정보가 null인 경우, 사용자 이름이 12자를 초과하는 경우, 전화번호가 20자를 초과하는 경우
    */
    public void updateUserInfo(Area newArea, String newUserName, String newPhone) {
        if (newArea == null) {
            throw new BusinessException(ErrorCode.INVALID_AREA);
        }
        if (newUserName.length() > 12) {
            throw new BusinessException(ErrorCode.USERNAME_TOO_LONG);
        }
        if (newPhone.length() > 20) {
            throw new BusinessException(ErrorCode.PHONE_TOO_LONG);
        }

        this.area = newArea;
        this.userName = newUserName;
        this.phone = newPhone;
    }

    // 사용자 제재 상태 변경
    public void changeBanStatus(boolean isBanned) {
        this.isBanned = isBanned;
    }

    // 관리자 권한 변경
    public void changeAdminStatus(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

}