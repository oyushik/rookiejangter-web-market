import axios from 'axios';

const NOTIFICATION_API_BASE_URL = 'http://localhost:8080/api/notifications';

const notificationService = {
  // 사용자의 알림 목록을 페이지네이션하여 가져오는 메서드
  getNotifications: async (page = 0, size = 10, sortBy = 'sentAt,desc') => {
    try {
      const response = await axios.get(NOTIFICATION_API_BASE_URL, {
        params: {
          page: page,
          size: size,
          sort: sortBy,
        },
      });
      return { success: true, data: response.data };
    } catch (error) {
      console.error('알림 목록을 가져오는 데 실패했습니다:', error);
      return { success: false, error: error.response?.data || error.message };
    }
  },

  // 읽지 않은 알림의 개수를 가져오는 메서드
  getUnreadNotificationsCount: async () => {
    try {
      const response = await axios.get(`${NOTIFICATION_API_BASE_URL}/unread-count`);
      return { success: true, data: response.data };
    } catch (error) {
      console.error('읽지 않은 알림 수를 가져오는 데 실패했습니다:', error);
      return { success: false, error: error.response?.data || error.message };
    }
  },

  // 특정 알림을 읽음 처리하는 메서드
  markNotificationAsRead: async (notificationId) => {
    try {
      await axios.patch(`${NOTIFICATION_API_BASE_URL}/${notificationId}/read`);
      return { success: true };
    } catch (error) {
      console.error('알림을 읽음 처리하는 데 실패했습니다:', error);
      return { success: false, error: error.response?.data || error.message };
    }
  },

  // 특정 알림을 삭제하는 메서드
  deleteNotification: async (notificationId) => {
    try {
      await axios.delete(`${NOTIFICATION_API_BASE_URL}/${notificationId}`);
      return { success: true };
    } catch (error) {
      console.error('알림을 삭제하는 데 실패했습니다:', error);
      return { success: false, error: error.response?.data || error.message };
    }
  },
};

export default notificationService;

export const {
  getNotifications,
  getUnreadNotificationsCount,
  markNotificationAsRead,
  deleteNotification,
} = notificationService;
