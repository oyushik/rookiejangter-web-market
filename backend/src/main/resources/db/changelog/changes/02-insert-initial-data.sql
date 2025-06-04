-- src/main/resources/db/changelog/changes/02-insert-initial-data.sql

-- liquibase formatted sql

-- changeset oyushik:1
-- comment: Insert initial data into areas table
INSERT INTO areas (area_id, area_name) VALUES
('서울특별시'),
('부산광역시'),
('대구광역시'),
('인천광역시'),
('광주광역시'),
('대전광역시'),
('울산광역시'),
('세종특별자치시'),
('경기도'),
('강원특별자치도'),
('충청북도'),
('충청남도'),
('전북특별자치도'),
('전라남도'),
('경상북도'),
('경상남도'),
('제주특별자치도');

-- changeset oyushik:2
-- comment: Insert initial data into notifications table
INSERT INTO categories (category_id, category_name) VALUES
('디지털기기'),
('생활가전'),
('가구/인테리어'),
('생활/주방'),
('유아동'),
('유아도서'),
('여성의류'),
('여성잡화'),
('남성패션/잡화'),
('뷰티/미용'),
('스포츠/레저'),
('취미/게임/음반'),
('도서'),
('티켓/교환권'),
('가공식품'),
('건강기능식품'),
('반려동물용품'),
('식물'),
('기타 중고물품'),
('삽니다');

-- changeset oyushik:3
-- comment: Insert initial data into cancelation_reason table
INSERT INTO cancelation_reasons (cancelation_reason_id, cancelation_reason_type) VALUES
('단순 변심'),
('상품 정보 오류'),
('판매자와 연락 불가'),
('질병'),
('사고 및 재난'),
('도난/사기 물품 의심');

-- changeset oyushik:4
-- comment: Insert initial data into report_reasons table
INSERT INTO report_reasons (report_reason_id, report_reason_type) VALUES
('불쾌한 언어 사용'),
('광고/스팸'),
('부적절한 게시물'),
('거래 무단 파기');
