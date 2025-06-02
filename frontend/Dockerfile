# Node.js LTS 이미지 사용
FROM node:lts

# 작업 디렉토리 설정
WORKDIR /app

# package.json 파일 복사
COPY package*.json ./

# 모든 의존성 설치 (dev 포함)
RUN npm install

# 앱 소스 복사
COPY . .

# Vite 개발 서버는 5173 포트를 사용
EXPOSE 5173

# 개발 서버 실행 (--host는 외부 접근 허용)
CMD ["npm", "run", "dev", "--", "--host"]
