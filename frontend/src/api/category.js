import axios from 'axios';

const BASE_URL = 'http://localhost:8080';

export const getCategories = async () => {
  try {
    const response = await axios.get(`${BASE_URL}/categories`);
    return response.data; // ApiResponseWrapper를 제외한 data만 return
  } catch (error) {
    console.error('카테고리 목록을 가져오는 데 실패했습니다.', error);
    throw error;
  }
};
