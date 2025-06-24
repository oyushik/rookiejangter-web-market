// src/services/reservationService.js
import axios from 'axios';

const RESERVATION_API_BASE_URL = 'http://localhost:8080/api/reservations';

const getToken = () => localStorage.getItem('accessToken'); // 토큰 가져오는 헬퍼 함수

const reservationService = {
  /**
   * 구매자로서 참여한 모든 거래 예약 목록을 조회합니다.
   * @param {string} token - 인증을 위한 JWT 토큰
   * @returns {Promise<Object>} 예약 목록 (success: boolean, data?: Array<Object>, error?: any)
   */
  getReservationsByBuyer: async () => {
    try {
      const token = getToken();
      const response = await axios.get(`${RESERVATION_API_BASE_URL}/buyer`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      if (response.data.success) {
        return { success: true, data: response.data.data };
      } else {
        console.error('구매자 예약 목록 조회 실패 (백엔드 응답):', response.data.message);
        return { success: false, error: response.data.message || '알 수 없는 오류' };
      }
    } catch (error) {
      console.error('구매자 예약 목록 조회 실패:', error.response?.data || error.message);
      return { success: false, error: error.response?.data?.message || error.message };
    }
  },

  /**
   * 판매자로서 참여한 모든 거래 예약 목록을 조회합니다.
   * @param {string} token - 인증을 위한 JWT 토큰
   * @returns {Promise<Object>} 예약 목록 (success: boolean, data?: Array<Object>, error?: any)
   */
  getReservationsBySeller: async () => {
    try {
      const token = getToken();
      const response = await axios.get(`${RESERVATION_API_BASE_URL}/seller`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      if (response.data.success) {
        return { success: true, data: response.data.data };
      } else {
        console.error('판매자 예약 목록 조회 실패 (백엔드 응답):', response.data.message);
        return { success: false, error: response.data.message || '알 수 없는 오류' };
      }
    } catch (error) {
      console.error('판매자 예약 목록 조회 실패:', error.response?.data || error.message);
      return { success: false, error: error.response?.data?.message || error.message };
    }
  },

  /**
   * 특정 ID의 거래 예약 상세 정보를 조회합니다.
   * @param {number} reservationId - 조회할 예약의 ID
   * @param {string} token - 인증을 위한 JWT 토큰
   * @returns {Promise<Object>} 예약 상세 정보 (success: boolean, data?: Object, error?: any)
   */
  getReservationById: async (reservationId) => {
    try {
      const token = getToken();
      const response = await axios.get(`${RESERVATION_API_BASE_URL}/${reservationId}`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      if (response.data.success) {
        return { success: true, data: response.data.data };
      } else {
        console.error('예약 상세 정보 조회 실패 (백엔드 응답):', response.data.message);
        return { success: false, error: response.data.message || '알 수 없는 오류' };
      }
    } catch (error) {
      console.error(
        `예약 ID ${reservationId} 상세 정보 조회 실패:`,
        error.response?.data || error.message
      );
      return { success: false, error: error.response?.data?.message || error.message };
    }
  },

  /**
   * 채팅방 ID를 기반으로 거래 예약을 생성합니다.
   * 백엔드에서는 이 chatId를 통해 상품 정보를 찾아 예약 상태를 업데이트합니다.
   * @param {number} chatId - 예약을 생성할 채팅방의 ID
   * @returns {Promise<Object>} 거래 예약 생성 결과 (success: boolean, data?: object, error?: any)
   */
  createReservation: async (chatId) => {
    // token 매개변수 제거, 내부에서 getToken() 사용
    try {
      const token = getToken();
      const response = await axios.post(
        `${RESERVATION_API_BASE_URL}/${chatId}`, // URL 경로를 RESTful하게 chatId를 PathVariable로 전달
        {}, // 요청 본문은 빈 객체로 보냄 (백엔드에서 chatId를 PathVariable로 받으므로)
        { headers: { Authorization: `Bearer ${token}` } }
      );
      if (response.data.success) {
        return { success: true, data: response.data.data };
      } else {
        console.error('거래 예약 생성 실패 (백엔드 응답):', response.data.message);
        return { success: false, error: response.data.message || '알 수 없는 오류' };
      }
    } catch (error) {
      console.error('거래 예약 생성 실패:', error.response?.data || error.message);
      return { success: false, error: error.response?.data?.message || error.message };
    }
  },

  /**
   * 채팅방 ID를 기반으로 거래 예약을 취소합니다.
   * 백엔드에서는 이 chatId를 통해 상품 정보를 찾아 예약 상태를 해제합니다.
   * @param {number} chatId - 예약을 취소할 채팅방의 ID
   * @param {object} cancelationRequestData - 취소 사유 및 상세 정보
   * @param {string} token - 인증을 위한 JWT 토큰
   * @returns {Promise<Object>} 거래 예약 취소 결과 (success: boolean, data?: object, error?: any)
   */
  cancelReservation: async (chatId, cancelationRequestData) => {
    // token 매개변수 제거, getToken() 사용
    try {
      const token = getToken(); // 여기서 토큰을 가져옴
      const headers = {
        Authorization: `Bearer ${token}`,
        'Content-Type': 'application/json',
      };
      const response = await axios.delete(
        `${RESERVATION_API_BASE_URL}/${chatId}`, // **이 부분이 올바르게 수정되었습니다.**
        {
          headers: headers,
          data: cancelationRequestData,
        }
      );
      return { success: true, data: response.data.data };
    } catch (error) {
      console.error('Error canceling reservation:', error);
      if (error.response && error.response.data) {
        return {
          success: false,
          error:
            error.response.data.message || error.response.data.error || '예약 취소 중 오류 발생',
        };
      }
      return { success: false, error: '네트워크 오류' };
    }
  },
};

// 필요한 함수들만 export
export const {
  getReservationsByBuyer,
  getReservationsBySeller,
  getReservationById,
  createReservation,
  cancelReservation,
} = reservationService;
