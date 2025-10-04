[English](./README.md) | [한국어](./README.ko.md) | [日本語](./README.ja.md)

---

# 🛒 Rookiejangter - Second-hand Trading Platform

> A second-hand trading web service benchmarked against Daangn Market and Bunjang.

## 📋 Project Overview

- **Development Period**: 2025.05.23 ~ 2025.06.09
- **Team Composition**: 5 members (1 PM, 2 Backend, 2 Frontend)
- **Key Features**: Second-hand item trading, chat, user authentication

## 👥 Team Composition & Roles

| Role         | Name         | Responsibilities                                        | GitHub                                                |
| ------------ | ------------ | ------------------------------------------------------- | ----------------------------------------------------- |
| **PM**       | Jahyeon Ji   | Project management, scheduling, requirements definition | [J-millar99](https://github.com/J-millar99)           |
| **Backend**  | Yushik Oh    | API development, database design, server setup          | [oyushik](https://github.com/oyushik)                 |
| **Backend**  | Minjeong Kim | Authentication/Security, business logic, testing        | [ReadyToStarting](https://github.com/ReadyToStarting) |
| **Frontend** | Woojun Kim   | React components, UI/UX implementation                  | [Ra1nJun](https://github.com/Ra1nJun)                 |
| **Frontend** | Taekyung Ahn | State management, API integration, admin page           | [Ahn-TaeKyung](https://github.com/Ahn-TaeKyung)       |

## 🎯 Key Features

### Core Features

- **User Management**: Sign-up, login, profile management
- **Product Management**: Product registration, modification, deletion, detailed view
- **Transaction Management**: Wishlist, transaction status management, transaction reviews
- **Real-time Chat**: Communication between buyer and seller
- **Admin Features**: User/product management, report handling

### Additional Features

## 🛠 Tech Stack

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
- **Admin**: Thymeleaf (Admin page)

### DevOps & Tools

- **Version Control**: Git + GitHub
- **IDE**: IntelliJ IDEA, VS Code
- **API Testing**: Postman
- **Design**: Figma
- **Communication**: Notion

## 📁 Project Structure

```
SK_Rookies_Mini_Project2-main/
├── backend/                        # Spring Boot Backend
│   ├── src/main/java/
│   │   ├── controller/             # REST API Controllers
│   │   ├── dto/                    # Data Transfer Objects
│   │   ├── service/                # Business Logic
│   │   ├── repository/             # Data Access Layer
│   │   ├── entity/                 # JPA Entities
│   │   ├── config/                 # Configuration Classes
│   │   └── exception/              # Exception Handling
│   │   │   └── advice/             # Controller Advice Definitions
│   │   └── provider/               # JWT Token related definitions
│   ├── src/main/resources/
│   │   ├── application.yml         # Common application settings
│   │   ├── application-prod.yml    # Application settings: Production environment
│   │   ├── application-test.yml    # Application settings: Development (test) environment
│   │   ├── db/                     # DB schema management settings
│   │   │   └── migration/          # Flyway initial schema, data settings
│   │   ├── static/                 # Static resources
│   │   └── templates/              # Thymeleaf templates
│   └── build.gradle
├── frontend/                       # React Frontend
│   ├── public/
│   ├── src/
│   │   ├── api/                    # Backend API integration
│   │   ├── assets/                 # Simple assets like icons
│   │   ├── components/             # React Components
│   │   ├── features/               # Common features
│   │   │   └── auth/               # User authentication related features
│   │   ├── hooks/                  # Custom Hooks
│   │   ├── pages/                  # Page Components
│   │   ├── store/                  # Redux Store
│   │   ├── services/               # API Services
│   │   └── utils/                  # Utility functions
│   ├── package.json
│   └── vite.config.js
├── docs/                           # Project documents
└── README.md
```

## 🚀 Getting Started

### Prerequisites

- Docker Desktop
- Java 17+
- Git

### Integrated Backend & Frontend Execution

```bash
docker-compose up --build -d
```

## 📖 API Documentation

SK_Rookies_Mini_Project2-main/docs/RESTAPI.md

## 🤝 Collaboration Rules

### Git Branch Strategy

- **main**: Stable version for deployment
- **feature/feature-name**: Branch for feature development
- **hotfix/bug-name**: Branch for bug fixes

### Commit Message Convention

```
type#issue number: subject

feat: Add a new feature
bugfix: Fix a bug
docs: Modify documentation
style: Change code style
refactor: Refactor code
test: Add test code

Example: feat#13: Implement user login feature
```

### Code Review

- All PRs require review from at least one person.
- Reviewers check code style, logic, and test coverage.
- Merge after approval.

### Issue Management

- Task management using GitHub Issues.
- Classification using labels (bug, enhancement, question, etc.).
- Sprint management with milestones.

## 📚 References

### Benchmarking Services

- [Daangn Market](https://www.daangn.com/)
- [Bunjang](https://m.bunjang.co.kr/)

### Technical Documents

- [Spring Boot Official Docs](https://spring.io/projects/spring-boot)
- [React Official Docs](https://react.dev/)
- [Material-UI Docs](https://mui.com/)

### Design References

- [Figma Design System](link-to-figma)
