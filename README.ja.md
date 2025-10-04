[English](./README.md) | [한국어](./README.ko.md) | [日本語](./README.ja.md)

---

# 🛒 Rookiejangter - 中古取引プラットフォーム (ルーキー市場)

> Daangn Market、Bunjangをベンチマークした中古取引ウェブサービス

## 📋 プロジェクト概要

- **開発期間**: 2025.05.23 ~ 2025.06.09
- **チーム構成**: 5名 (PM 1名, Backend 2名, Frontend 2名)
- **主な機能**: 中古品取引、チャット、ユーザー認証

## 👥 チーム構成と役割

| 役割         | 名前         | 担当業務                               | GitHub                                                |
| ------------ | ------------ | --------------------------------------- | ----------------------------------------------------- |
| **PM**       | ジ・ジェヒョン | プロジェクト管理、スケジュール調整、要件定義 | [J-millar99](https://github.com/J-millar99)           |
| **Backend**  | オ・ユシク   | API開発、データベース設計、サーバー構築  | [oyushik](https://github.com/oyushik)                 |
| **Backend**  | キム・ミンジョン | 認証/セキュリティ、ビジネスロジック、テスト | [ReadyToStarting](https://github.com/ReadyToStarting) |
| **Frontend** | キム・ウジュン | Reactコンポーネント、UI/UX実装        | [Ra1nJun](https://github.com/Ra1nJun)                 |
| **Frontend** | アン・テギョン | 状態管理、API連携、管理者ページ       | [Ahn-TaeKyung](https://github.com/Ahn-TaeKyung)       |

## 🎯 主な機能

### コア機能

- **ユーザー管理**: 会員登録、ログイン、プロフィール管理
- **商品管理**: 商品登録、修正、削除、詳細表示
- **取引管理**: いいね、取引ステータス管理、取引レビュー
- **リアルタイムチャット**: 購入者-販売者間のコミュニケーション
- **管理者機能**: ユーザー/商品管理、通報処理

### 追加機能

## 🛠 技術スタック

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
- **Admin**: Thymeleaf (管理者ページ)

### DevOps & ツール

- **Version Control**: Git + GitHub
- **IDE**: IntelliJ IDEA, VS Code
- **API Testing**: Postman
- **Design**: Figma
- **Communication**: Notion

## 📁 プロジェクト構造

```
SK_Rookies_Mini_Project2-main/
├── backend/                        # Spring Boot バックエンド
│   ├── src/main/java/
│   │   ├── controller/             # REST API コントローラー
│   │   ├── dto/                    # データ転送オブジェクト
│   │   ├── service/                # ビジネスロジック
│   │   ├── repository/             # データアクセス層
│   │   ├── entity/                 # JPA エンティティ
│   │   ├── config/                 # 設定クラス
│   │   └── exception/              # 例外処理
│   │   │   └── advice/             # コントローラーアドバイス定義
│   │   └── provider/               # JWTトークン関連定義
│   ├── src/main/resources/
│   │   ├── application.yml         # アプリケーション共通設定
│   │   ├── application-prod.yml    # アプリケーション設定：本番環境
│   │   ├── application-test.yml    # アプリケーション設定：開発(テスト)環境
│   │   ├── db/                     # DBスキーマ管理設定
│   │   │   └── migration/          # Flyway初期スキーマ、データ設定
│   │   ├── static/                 # 静的リソース
│   │   └── templates/              # Thymeleaf テンプレート
│   └── build.gradle
├── frontend/                       # React フロントエンド
│   ├── public/
│   ├── src/
│   │   ├── api/                    # バックエンドAPI連携
│   │   ├── assets/                 # アイコンなどの単純なアセット
│   │   ├── components/             # React コンポーネント
│   │   ├── features/               # 共通機能
│   │   │   └── auth/               # ユーザー認証関連機能
│   │   ├── hooks/                  # カスタムフック
│   │   ├── pages/                  # ページコンポーネント
│   │   ├── store/                  # Redux ストア
│   │   ├── services/               # API サービス
│   │   └── utils/                  # ユーティリティ関数
│   ├── package.json
│   └── vite.config.js
├── docs/                           # プロジェクト文書
└── README.md
```

## 🚀 始め方

### 必要条件

- Docker Desktop
- Java 17+
- Git

### バックエンド、フロントエンド統合実行

```bash
docker-compose up --build -d
```

## 📖 API ドキュメント

SK_Rookies_Mini_Project2-main/docs/RESTAPI.md

## 🤝 協業ルール

### Git ブランチ戦略

- **main**: デプロイ可能な安定バージョン
- **feature/機能名**: 機能開発ブランチ
- **hotfix/バグ名**: バグ修正

### コミットメッセージ規則

```
type#issue number: subject

feat: 新機能追加
bugfix: バグ修正
docs: ドキュメント修正
style: コードスタイル変更
refactor: コードリファクタリング
test: テストコード追加

例: feat#13: ユーザーログイン機能の実装
```

### コードレビュー

- すべてのPRは最低1人以上のレビューが必要
- レビュアーはコードスタイル、ロジック、テストの有無を確認
- 承認後にマージ進行

### イシュー管理

- GitHub Issuesを活用したタスク管理
- ラベルを活用した分類 (bug, enhancement, question など)
- マイルストーンによるスプリント管理

## 📚 参考資料

### ベンチマークサービス

- [Daangn Market](https://www.daangn.com/)
- [Bunjang](https://m.bunjang.co.kr/)

### 技術文書

- [Spring Boot 公式ドキュメント](https://spring.io/projects/spring-boot)
- [React 公式ドキュメント](https://react.dev/)
- [Material-UI ドキュメント](https://mui.com/)

### デザイン参考

- [Figma デザインシステム](link-to-figma)
