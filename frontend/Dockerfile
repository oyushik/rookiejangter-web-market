# Node.js 18 Alpine 이미지 사용 (가벼운 버전)
FROM node:18-alpine

# 작업 디렉토리 설정
WORKDIR /app

# package.json과 package-lock.json 복사 (캐시 최적화를 위해)
COPY package*.json ./

# 의존성 설치
RUN npm install

# 소스 코드 복사
COPY . .

# 3000번 포트 노출
EXPOSE 3000

# 개발 서버 실행
CMD ["npm", "run", "dev"]