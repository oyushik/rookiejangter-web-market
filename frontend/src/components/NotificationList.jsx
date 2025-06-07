import React, { useEffect, useState, useRef, useCallback } from 'react';
import {
  Box,
  Typography,
  List,
  ListItem,
  ListItemText,
  CircularProgress,
  Alert,
  Paper,
  Button,
  Divider,
} from '@mui/material';
import {
  getNotifications,
  markNotificationAsRead,
  deleteNotification,
} from '../api/notificationService';
import { formatDistanceToNow, parseISO } from 'date-fns'; // date-fns에서 parseISO 임포트 추가
import { ko } from 'date-fns/locale'; // 한국어 로케일 (npm install date-fns)

const NotificationList = () => {
  const [notifications, setNotifications] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [hasMore, setHasMore] = useState(true); // 더 불러올 알림이 있는지 여부
  const [page, setPage] = useState(0); // 현재 페이지 번호 (0부터 시작)

  // Intersection Observer를 위한 ref
  const observer = useRef();
  const lastNotificationElementRef = useCallback(
    (node) => {
      if (loading) return; // 로딩 중이면 추가 요청 방지
      if (observer.current) observer.current.disconnect(); // 기존 옵저버 연결 해제
      observer.current = new IntersectionObserver((entries) => {
        // 엔트리가 뷰포트에 진입했고, 더 불러올 데이터가 있다면
        if (entries[0].isIntersecting && hasMore) {
          setPage((prevPage) => prevPage + 1); // 다음 페이지 로드 요청
        }
      });
      if (node) observer.current.observe(node); // 새로운 노드 관찰 시작
    },
    [loading, hasMore]
  ); // loading 또는 hasMore 상태가 변경될 때마다 콜백 함수 재생성

  const fetchNotifications = useCallback(async (pageNum) => {
    setLoading(true);
    setError(null);
    try {
      // getNotifications 함수 호출 시 page, size, sortBy 파라미터 전달
      const response = await getNotifications(pageNum, 10); // 한 페이지당 10개씩 가져오도록 설정

      if (response.success && response.data) {
        const { content: newNotifications, totalPages } = response.data; // 백엔드의 Page 객체에서 content와 totalPages 추출

        setNotifications((prevNotifications) => {
          // 새로운 알림을 기존 알림과 병합하되, 중복을 제거
          const uniqueNewNotifications = newNotifications.filter(
            (newNotif) =>
              !prevNotifications.some(
                (existingNotif) => existingNotif.notificationId === newNotif.notificationId
              )
          );
          return [...prevNotifications, ...uniqueNewNotifications];
        });

        // 현재 페이지가 전체 페이지 수보다 작으면 더 불러올 데이터가 있다고 판단
        setHasMore(pageNum < totalPages - 1);
      } else {
        setError(
          typeof response.error === 'object'
            ? response.error.message || JSON.stringify(response.error)
            : response.error || '알림 데이터를 불러오는데 실패했습니다.'
        );
        setHasMore(false);
      }
    } catch (err) {
      setError('알림 데이터를 불러오는 중 오류가 발생했습니다.');
      console.error('Fetch notifications error:', err);
      setHasMore(false); // 오류 발생 시 더 이상 불러올 데이터 없음
    } finally {
      setLoading(false);
    }
  }, []); // useCallback의 의존성 배열은 비워두어 컴포넌트 마운트 시 한 번만 생성되도록 합니다. (page 상태는 내부에서 관리)

  useEffect(() => {
    // page 상태가 변경될 때마다 알림을 새로 불러옴
    fetchNotifications(page);
  }, [page, fetchNotifications]); // page가 변경될 때마다 fetchNotifications 실행

  const handleNotificationClick = async (notificationId, isRead) => {
    if (isRead) {
      // 이미 읽은 알림이면 추가적인 읽음 처리 로직 불필요
      console.log(`Notification ${notificationId} is already read.`);
      return;
    }

    try {
      const response = await markNotificationAsRead(notificationId);
      if (response.success) {
        // UI에서 해당 알림의 isRead 상태를 true로 업데이트
        setNotifications((prevNotifications) =>
          prevNotifications.map((notif) =>
            notif.notificationId === notificationId ? { ...notif, isRead: true } : notif
          )
        );
        console.log(`Notification ${notificationId} marked as read.`);
        // 헤더의 읽지 않은 알림 개수 갱신을 위해 별도의 로직 필요 (예: Redux 상태 업데이트 또는 Header 컴포넌트 강제 재호출)
        // 여기서는 간단히 새로고침을 유도하거나, Header에서 주기적으로 개수를 가져오도록 할 수 있습니다.
        // 현재는 Header에서 주기적으로 가져오도록 가정하거나, 사용자가 페이지 이동 후 다시 들어올 때 갱신됩니다.
      } else {
        alert('알림을 읽음 처리하는 데 실패했습니다: ' + response.error);
      }
    } catch (error) {
      console.error('알림 읽음 처리 실패:', error);
      alert('알림을 읽음 처리하는 중 오류가 발생했습니다.');
    }
  };

  const handleDeleteNotification = async (notificationId) => {
    if (!window.confirm('정말로 이 알림을 삭제하시겠습니까?')) {
      return;
    }
    try {
      const response = await deleteNotification(notificationId);
      if (response.success) {
        setNotifications((prevNotifications) =>
          prevNotifications.filter((notif) => notif.notificationId !== notificationId)
        );
        console.log(`Notification ${notificationId} deleted.`);
        // 삭제 후에도 Header의 읽지 않은 알림 개수 갱신 필요
      } else {
        alert('알림 삭제에 실패했습니다: ' + response.error);
      }
    } catch (error) {
      console.error('알림 삭제 실패:', error);
      alert('알림을 삭제하는 중 오류가 발생했습니다.');
    }
  };

  if (loading && notifications.length === 0) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', mt: 4 }}>
        <CircularProgress />
      </Box>
    );
  }

  if (error) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', mt: 4 }}>
        <Alert severity="error">{error}</Alert>
      </Box>
    );
  }

  return (
    <Box sx={{ maxWidth: 800, mx: 'auto', mt: 4, p: 2 }}>
      <Typography variant="h4" component="h1" gutterBottom sx={{ mb: 4 }}>
        알림
      </Typography>
      {notifications.length === 0 && !loading ? (
        <Alert severity="info">새로운 알림이 없습니다.</Alert>
      ) : (
        <List component={Paper} elevation={1}>
          {notifications.map((notification, index) => {
            const isLastElement = notifications.length === index + 1; // 마지막 요소인지 확인
            return (
              <React.Fragment key={notification.notificationId}>
                <ListItem
                  ref={isLastElement ? lastNotificationElementRef : null} // 마지막 요소에만 ref 연결
                  sx={{
                    backgroundColor: notification.isRead ? '#f5f5f5' : '#ffffff', // 읽음 여부에 따른 배경색
                    cursor: 'pointer',
                    '&:hover': {
                      backgroundColor: notification.isRead ? '#eeeeee' : '#f0f0f0',
                    },
                    display: 'flex',
                    alignItems: 'center',
                    py: 1.5,
                    px: 2,
                  }}
                >
                  <ListItemText
                    onClick={() =>
                      handleNotificationClick(notification.notificationId, notification.isRead)
                    }
                    primary={
                      <Typography
                        variant="body1"
                        sx={{
                          fontWeight: notification.isRead ? 'normal' : 'bold', // 읽음 여부에 따른 폰트 굵기
                          color: notification.isRead ? 'text.secondary' : 'text.primary', // 읽음 여부에 따른 텍스트 색상
                        }}
                      >
                        {notification.message}
                      </Typography>
                    }
                    secondary={
                      <Typography
                        variant="caption"
                        color="text.secondary"
                        sx={{
                          color: notification.isRead ? 'text.disabled' : 'text.secondary',
                        }}
                      >
                        {/* sentAt이 ISO 8601 문자열이라고 가정 (예: "2023-10-26T10:00:00Z") */}
                        {notification.sentAt
                          ? formatDistanceToNow(parseISO(notification.sentAt), {
                              addSuffix: true,
                              locale: ko,
                            })
                          : '날짜 없음'}
                      </Typography>
                    }
                  />
                  <Box sx={{ ml: 2, display: 'flex', gap: 1 }}>
                    {!notification.isRead && (
                      <Button
                        variant="outlined"
                        size="small"
                        onClick={(e) => {
                          // 이벤트 버블링 방지
                          e.stopPropagation();
                          handleNotificationClick(notification.notificationId, notification.isRead);
                        }}
                      >
                        읽음 표시
                      </Button>
                    )}
                    <Button
                      variant="outlined"
                      color="error"
                      size="small"
                      onClick={(e) => {
                        // 이벤트 버블링 방지
                        e.stopPropagation();
                        handleDeleteNotification(notification.notificationId);
                      }}
                    >
                      삭제
                    </Button>
                  </Box>
                </ListItem>
                {/* 마지막 알림이 아니면 구분선 추가 */}
                {!isLastElement && <Divider component="li" />}
              </React.Fragment>
            );
          })}
          {loading && ( // 로딩 중일 때 로딩 스피너 표시
            <Box sx={{ display: 'flex', justifyContent: 'center', p: 2 }}>
              <CircularProgress size={24} />
            </Box>
          )}
          {!hasMore &&
            notifications.length > 0 && ( // 더 이상 불러올 알림이 없을 때 메시지 표시
              <Typography variant="body2" color="text.secondary" sx={{ textAlign: 'center', p: 2 }}>
                더 이상 알림이 없습니다.
              </Typography>
            )}
        </List>
      )}
    </Box>
  );
};

export default NotificationList;
