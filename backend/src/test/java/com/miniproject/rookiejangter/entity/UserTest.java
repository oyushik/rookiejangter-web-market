package com.miniproject.rookiejangter.entity;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import jakarta.persistence.EntityManager;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserTest {

    @Autowired
    private EntityManager entityManager;

    @Test
    void createUser() {
        // (기존 createUser 테스트 코드)
        User user = User.builder()
                .loginId("testuser")
                .password("password")
                .userName("테스트유저")
                .phone("010-1234-5678")
                .isBanned(false)
                .isAdmin(false)
                .build();

        entityManager.persist(user);
        entityManager.flush();
        entityManager.clear();
        User savedUser = entityManager.find(User.class, user.getUserId());

        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getUserId()).isNotNull();
        assertThat(savedUser.getLoginId()).isEqualTo("testuser");
        assertThat(savedUser.getUserName()).isEqualTo("테스트유저");
        assertThat(savedUser.getIsBanned()).isFalse();
        assertThat(savedUser.getIsAdmin()).isFalse();
    }

    @Test
    void checkUserAreaAssociation() {
        // (기존 checkUserAreaAssociation 테스트 코드)
        Area area = Area.builder()
                .areaName("서울 강남구")
                .build();
        entityManager.persist(area);

        User user = User.builder()
                .area(area)
                .loginId("userInGangnam")
                .password("pwd")
                .userName("강남유저")
                .phone("010-1111-1111")
                .build();
        entityManager.persist(user);

        entityManager.flush();
        entityManager.clear();

        User foundUser = entityManager.find(User.class, user.getUserId());

        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getArea()).isNotNull();
        assertThat(foundUser.getArea().getAreaName()).isEqualTo("서울 강남구");
    }

    @Test
    void checkUserProductAssociation() {
        // (기존 checkUserProductAssociation 테스트 코드)
        User user = User.builder()
                .loginId("sellerUser")
                .password("pwd")
                .userName("판매자")
                .phone("010-2222-2222")
                .build();
        entityManager.persist(user);

        Category category = Category.builder()
                .categoryName("의류")
                .build();
        entityManager.persist(category);

        Product product1 = Product.builder()
                .user(user)
                .category(category)
                .title("티셔츠")
                .content("새 티셔츠")
                .price(20000)
                .build();
        entityManager.persist(product1);

        Product product2 = Product.builder()
                .user(user)
                .category(category)
                .title("바지")
                .content("새 바지")
                .price(40000)
                .build();
        entityManager.persist(product2);

        entityManager.flush();
        entityManager.clear();

        User foundUser = entityManager.find(User.class, user.getUserId());

        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getProducts()).hasSize(2);
        assertThat(foundUser.getProducts()).extracting("title").containsExactly("티셔츠", "바지");
        assertThat(product1.getUser()).isEqualTo(foundUser);
        assertThat(product2.getUser()).isEqualTo(foundUser);
    }

    @Test
    void checkUserReportAssociation() {
        // given
        User reporter = User.builder()
                .loginId("reporter")
                .password("pwd")
                .userName("신고자")
                .phone("010-3333-3333")
                .build();
        entityManager.persist(reporter);

        ReportReason reportReason = ReportReason.builder()
                .reportReasonType("스팸")
                .build();
        entityManager.persist(reportReason);

        Report report1 = Report.builder()
                .user(reporter)
                .reportReason(reportReason)
                .targetId(1L)
                .targetType("user")
                .reportDetail("스팸 계정 같습니다.")
                .isProcessed(false)
                .build();
        entityManager.persist(report1);

        Report report2 = Report.builder()
                .user(reporter)
                .reportReason(reportReason)
                .targetId(101L)
                .targetType("product")
                .reportDetail("광고 같습니다.")
                .isProcessed(false)
                .build();
        entityManager.persist(report2);

        entityManager.flush();
        entityManager.clear();

        // when
        User foundReporter = entityManager.find(User.class, reporter.getUserId());

        // then
        assertThat(foundReporter).isNotNull();
        assertThat(foundReporter.getReports()).hasSize(2);
        assertThat(foundReporter.getReports()).extracting("reportDetail")
                .containsExactly("스팸 계정 같습니다.", "광고 같습니다.");
        assertThat(report1.getUser()).isEqualTo(foundReporter);
        assertThat(report2.getUser()).isEqualTo(foundReporter);
    }

    @Test
    void checkUserReviewAssociation() {
        // given
        User user1 = User.builder()
                .loginId("user1")
                .password("pwd")
                .userName("사용자1")
                .phone("010-4444-4444")
                .build();
        entityManager.persist(user1);

        User user2 = User.builder()
                .loginId("user2")
                .password("pwd")
                .userName("사용자2")
                .phone("010-5555-5555")
                .build();
        entityManager.persist(user2);

        Category category = Category.builder()
                .categoryName("기타")
                .build();
        entityManager.persist(category);

        Product product1 = Product.builder()
                .user(user1)
                .category(category)
                .title("테스트 상품1")
                .content("테스트 상품 내용1")
                .price(10000)
                .build();
        entityManager.persist(product1);

        Product product2 = Product.builder()
                .user(user1)
                .category(category)
                .title("테스트 상품2")
                .content("테스트 상품 내용2")
                .price(20000)
                .build();
        entityManager.persist(product2);

        Complete complete1 = Complete.builder()
                .product(product1)
                .buyer(user1)
                .seller(user2)
                .build();
        entityManager.persist(complete1);

        Complete complete2 = Complete.builder()
                .product(product2)
                .buyer(user2)
                .seller(user1)
                .build();
        entityManager.persist(complete2);

        Review buyerReview = Review.builder()
                .buyer(complete1.getBuyer())
                .seller(complete1.getSeller())
                .complete(complete1)
                .rating(5)
                .content("user1의 구매자 리뷰")
                .build();
        entityManager.persist(buyerReview);

        Review sellerReview = Review.builder()
                .buyer(complete2.getBuyer())
                .seller(complete2.getSeller())
                .complete(complete2)
                .rating(4)
                .content("user1의 판매자 리뷰")
                .build();
        entityManager.persist(sellerReview);

        entityManager.flush();
        entityManager.clear();

        // when
        User foundBuyer = entityManager.find(User.class, user1.getUserId());

        // then
        assertThat(foundBuyer).isNotNull();
        assertThat(foundBuyer.getBuyerReviews()).hasSize(1);
        assertThat(foundBuyer.getBuyerReviews()).extracting("content").containsExactly("user1의 구매자 리뷰");
        assertThat(foundBuyer.getSellerReviews()).hasSize(1);
        assertThat(foundBuyer.getSellerReviews()).extracting("content").containsExactly("user1의 판매자 리뷰");

        assertThat(buyerReview.getBuyer()).isEqualTo(foundBuyer);
        assertThat(buyerReview.getSeller()).isEqualTo(sellerReview.getBuyer());
    }

    @Test
    void checkUserReservationAssociation() {
        // given
        User buyer = User.builder()
                .loginId("resBuyer")
                .password("pwd")
                .userName("예약구매자")
                .phone("010-6666-6666")
                .build();
        entityManager.persist(buyer);

        User seller = User.builder()
                .loginId("resSeller")
                .password("pwd")
                .userName("예약판매자")
                .phone("010-7777-7777")
                .build();
        entityManager.persist(seller);

        Product product = Product.builder()
                .user(seller)
                .title("예약 상품")
                .content("예약 판매 상품")
                .price(10000)
                .build();
        entityManager.persist(product);

        Reservation reservation1 = Reservation.builder()
                .buyer(buyer)
                .seller(seller)
                .product(product)
                .status(Reservation.TradeStatus.REQUESTED)
                .build();
        entityManager.persist(reservation1);

        Reservation reservation2 = Reservation.builder()
                .buyer(buyer)
                .seller(seller)
                .product(product)
                .status(Reservation.TradeStatus.ACCEPTED)
                .build();
        entityManager.persist(reservation2);

        entityManager.flush();
        entityManager.clear();

        // when
        User foundBuyer = entityManager.find(User.class, buyer.getUserId());
        User foundSeller = entityManager.find(User.class, seller.getUserId());

        // then
        assertThat(foundBuyer).isNotNull();
        assertThat(foundBuyer.getBuyerReservations()).hasSize(2);
        assertThat(foundSeller).isNotNull();
        assertThat(foundSeller.getSellerReservations()).hasSize(2);
        assertThat(reservation1.getBuyer()).isEqualTo(foundBuyer);
        assertThat(reservation2.getBuyer()).isEqualTo(foundBuyer);
        assertThat(reservation1.getSeller()).isEqualTo(foundSeller);
        assertThat(reservation2.getSeller()).isEqualTo(foundSeller);
    }

    // 필요하다면 다른 연관 관계 (chats, dibsList, notifications, bans, completes)에 대한 테스트도 추가할 수 있습니다.
}