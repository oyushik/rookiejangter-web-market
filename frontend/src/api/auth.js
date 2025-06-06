import axios from 'axios';
const BASE_URL = 'http://localhost:8080/api/auth'; // 엔드포인트는 실제 백엔드에 맞게 조정

export const requestSmsCode = (phoneNumber) => axios.post(`${BASE_URL}/send-code`, { phoneNumber });

export const verifySmsCode = ({ phoneNumber, code }) =>
  axios.post(`${BASE_URL}/verify-code`, { phoneNumber, code });

export const registerUser = (formData) => axios.post(`${BASE_URL}/signup`, formData);

export const fetchVerificationResult = (identityVerificationId) =>
  axios.post(`${BASE_URL}/identity-verifications`, { identityVerificationId });

export const signup = (userData) => axios.post(`${BASE_URL}/signup`, userData);

export const loginUser = (userData) => {
  return axios.post(`${BASE_URL}/login`, userData); // ✅ return 추가
};

export const logoutUser = () => {
  console.log('로그아웃 요청 중...');
  axios.post(`${BASE_URL}/logout`);
};
