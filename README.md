# 🛒 Rookiejangter - 중고거래 플랫폼 (루키 장터)

> 당근마켓, 번개장터를 벤치마킹한 중고거래 웹 서비스

## 📋 프로젝트 개요

- **개발 기간**: 2025.05.23 ~ 2025.06.09
- **팀 구성**: 5명 (PM 1명, Backend 2명, Frontend 2명)  
- **주요 기능**: 중고물품 거래, 채팅, 사용자 인증

## 👥 팀 구성 및 역할

| 역할         | 이름   | 담당 업무                               | GitHub                                                |
| ------------ | ------ | --------------------------------------- | ----------------------------------------------------- |
| **PM**       | 지재현 | 프로젝트 관리, 일정 조율, 요구사항 정의 | [J-millar99](https://github.com/J-millar99)           |
| **Backend**  | 오유식 | API 개발, 데이터베이스 설계, 서버 구축  | [oyushik](https://github.com/oyushik)                 |
| **Backend**  | 김민정 | 인증/보안, 비즈니스 로직, 테스트        | [ReadyToStarting](https://github.com/ReadyToStarting) |
| **Frontend** | 김우준 | React 컴포넌트, UI/UX 구현              | [Ra1nJun](https://github.com/Ra1nJun)                 |
| **Frontend** | 안태경 | 상태관리, API 연동, 관리자 페이지       | [Ahn-TaeKyung](https://github.com/Ahn-TaeKyung)       |

## 🎯 주요 기능

### 핵심 기능

- **사용자 관리**: 회원가입, 로그인, 프로필 관리
- **상품 관리**: 상품 등록, 수정, 삭제, 상세보기
- **거래 관리**: 찜하기, 거래 상태 관리, 거래 후기
- **실시간 채팅**: 구매자-판매자 간 소통
- **관리자 기능**: 사용자/상품 관리, 신고 처리

### 추가 기능

## 🛠 기술 스택

### Backend

- **Framework**: Spring Boot 3.4.6
- **Language**: Java 17
- **Database**: MariaDB 11.4
- **ORM**: JPA/Hibernate
- **Security**: Spring Security + JWT
- **Build Tool**: Gradle

### Frontend

- **Framework**: React 18 + Vite
- **Language**: JavaScript (ES6+)
- **UI Library**: Material-UI (MUI)
- **State Management**: Redux Toolkit
- **HTTP Client**: Axios
- **Routing**: React Router
- **Admin**: Thymeleaf (관리자 페이지)

### DevOps & Tools

- **Version Control**: Git + GitHub
- **IDE**: IntelliJ IDEA, VS Code
- **API Testing**: Postman
- **Design**: Figma
- **Communication**: Notion

## 📁 프로젝트 구조

```
SK_Rookies_Mini_Project2-main/
├── backend/                        # Spring Boot 백엔드
│   ├── src/main/java/
│   │   ├── controller/             # REST API 컨트롤러
│   │   ├── dto/                    # 데이터 전송 객체
│   │   ├── service/                # 비즈니스 로직
│   │   ├── repository/             # 데이터 접근 계층
│   │   ├── entity/                 # JPA 엔티티
│   │   ├── config/                 # 설정 클래스
│   │   └── exception/              # 예외 처리
│   │   │   └── advice/             # 컨트롤러 어드바이스 정의
│   │   └── provider/               # JWT 토큰 관련 정의
│   ├── src/main/resources/
│   │   ├── application.yml         # 애플리케이션 공통 설정
│   │   ├── application-prod.yml    # 애플리케이션 설정: 운영 환경
│   │   ├── application-test.yml    # 애플리케이션 설정: 개발(테스트) 환경
│   │   ├── db/                     # DB 형상관리 설정
│   │   │   └── migration/          # Flyway 초기 스키마, 데이터 설정
│   │   ├── static/                 # 정적 리소스
│   │   └── templates/              # Thymeleaf 템플릿
│   └── build.gradle
├── frontend/                       # React 프론트엔드
│   ├── public/
│   ├── src/
│   │   ├── api/                    # 백엔드 API 연동
│   │   ├── assets/                 # 아이콘 등 단순 asset
│   │   ├── components/             # React 컴포넌트
│   │   ├── features/               # 공통 기능
│   │   │   └── auth/               # 사용자 인증 관련 기능
│   │   ├── hooks/                  # 커스텀 훅
│   │   ├── pages/                  # 페이지 컴포넌트
│   │   ├── store/                  # Redux 스토어
│   │   ├── services/               # API 서비스
│   │   └── utils/                  # 유틸리티 함수
│   ├── package.json
│   └── vite.config.js
├── docs/                           # 프로젝트 문서
└── README.md
```

## 🚀 시작하기

### 환경 요구사항

- Docker Desktop
- Java 17+
- Git

### 백엔드, 프론트엔드 통합 실행

```bash
docker-compose up --build -d
```

## 📖 API 문서

SK_Rookies_Mini_Project2-main/docs/RESTAPI.md

## 🤝 협업 규칙

### Git 브랜치 전략

- **main**: 배포 가능한 안정 버전
- **feature/기능명**: 기능 개발 브랜치
- **hotfix/버그명**: 버그 수정

### 커밋 메시지 규칙

```
type#issue number: subject

feat: 새로운 기능 추가
bugfix: 버그 수정
docs: 문서 수정
style: 코드 스타일 변경
refactor: 코드 리팩토링
test: 테스트 코드 추가

예시: feat#13: 사용자 로그인 기능 구현
```

### 코드 리뷰

- 모든 PR은 최소 1명 이상의 리뷰 필요
- 리뷰어는 코드 스타일, 로직, 테스트 여부 확인
- 승인 후 merge 진행

### 이슈 관리

- GitHub Issues를 활용한 작업 관리
- 라벨을 활용한 분류 (bug, enhancement, question 등)
- 마일스톤으로 스프린트 관리

## 📚 참고 자료

### 벤치마킹 서비스

- [당근마켓](https://www.daangn.com/)
- [번개장터](https://m.bunjang.co.kr/)

### 기술 문서

- [Spring Boot 공식 문서](https://spring.io/projects/spring-boot)
- [React 공식 문서](https://react.dev/)
- [Material-UI 문서](https://mui.com/)

### 디자인 참고

- [Figma 디자인 시스템](link-to-figma)
