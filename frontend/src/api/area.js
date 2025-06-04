// api/area.js
import axios from 'axios'; // 필요에 따라 fetch 대신 axios 등을 사용할 수 있습니다.

const BASE_URL = 'http://localhost:8080'; // 백엔드 API 기본 URL

export const getAreas = async () => {
  try {
    const response = await axios.get(`${BASE_URL}/areas`);
    return response.data; // ApiResponseWrapper 를 제외한 data 만 반환하도록 수정
  } catch (error) {
    console.error('지역 목록을 가져오는 데 실패했습니다.', error);
    throw error;
  }
};
