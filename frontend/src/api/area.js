import axios from 'axios';

const BASE_URL = 'http://localhost:8080';

export const getAreas = async () => {
  try {
    const response = await axios.get(`${BASE_URL}/areas`);
    return response.data; // ApiResponseWrapper를 제외한 data만 return
  } catch (error) {
    console.error('지역 목록을 가져오는 데 실패했습니다.', error);
    throw error;
  }
};
