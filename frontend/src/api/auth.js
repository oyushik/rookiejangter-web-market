import axios from 'axios';

const BASE_URL = '/api/auth'; // 엔드포인트는 실제 백엔드에 맞게 조정

export const requestSmsCode = (phoneNumber) =>
    axios.post(`${BASE_URL}/send-code`, { phoneNumber });

export const verifySmsCode = ({ phoneNumber, code }) =>
    axios.post(`${BASE_URL}/verify-code`, { phoneNumber, code });

export const registerUser = (formData) =>
    axios.post(`${BASE_URL}/signup`, formData);

export const fetchVerificationResult = (identityVerificationId) =>
    axios.post(`${BASE_URL}/identity-verifications`, { identityVerificationId });

export const signup = (userData) =>
    axios.post(`${BASE_URL}/signup`, userData);

export const loginUser = (userData) =>
    axios.post(`${BASE_URL}/login`, userData);

export const logoutUser = () =>
    axios.post(`${BASE_URL}/logout`);