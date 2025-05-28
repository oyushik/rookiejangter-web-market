package com.miniproject.rookiejangter.repository;

public enum PostStatus {
    FOR_SALE("판매 중"),
    RESERVED("예약 중"),
    SOLD("거래 완료"),
    UNAVAILABLE("거래 불가"),
    DRAFT("임시 저장");

    private final String description;

    PostStatus(String description) {
        this.description = description;
    }
    public String getDescription() {
        return description;
    }
}
