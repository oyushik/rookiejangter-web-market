import React, { useState, useEffect } from 'react';
import {
  Box,
  Typography,
  List,
  ListItem,
  ListItemText,
  ListItemSecondaryAction,
  Button,
  Divider,
  CircularProgress,
  Paper,
} from '@mui/material';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import { jwtDecode } from 'jwt-decode';

const CHAT_API_BASE_URL = 'http://localhost:8080/api/chats';

// 시간 포맷터 유틸리티 (ChatRoomPage와 동일하게 사용)
const formatChatListTime = (isoString) => {
  if (!isoString) return '';
  const date = new Date(isoString);
  const options = {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    hour12: true,
  };
  return date.toLocaleTimeString('ko-KR', options); // 한국 시간 형식
};

const ChatListPage = () => {
  const [chatRooms, setChatRooms] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [currentUserId, setCurrentUserId] = useState(null);
  const navigate = useNavigate();

  useEffect(() => {
    const token = localStorage.getItem('accessToken');
    if (token) {
      try {
        const decodedToken = jwtDecode(token);
        setCurrentUserId(decodedToken.sub); // assuming 'sub' claim holds the userId
      } catch (error) {
        console.error('Invalid token:', error);
        setError('유효하지 않은 인증 정보입니다.');
        setLoading(false);
        return;
      }
    } else {
      setError('로그인이 필요합니다.');
      setLoading(false);
      return;
    }

    const fetchChatRooms = async () => {
      try {
        const response = await axios.get(`${CHAT_API_BASE_URL}`, {
          headers: {
            Authorization: `Bearer ${token}`,
          },
          params: {
            page: 0, // 첫 페이지 요청
            size: 10, // 페이지 당 10개
            sort: 'createdAt,desc', // 최신순 정렬
          },
        });
        if (response.data.success) {
          // 'content' 배열이 존재하는지 확인하고, 없다면 빈 배열로 초기화
          setChatRooms(response.data.data.content || []);
        } else {
          setError(response.data.message || '채팅방 목록을 불러오는데 실패했습니다.');
        }
      } catch (err) {
        console.error('Error fetching chat rooms:', err);
        setError('채팅방 목록을 불러오는 중 오류가 발생했습니다.');
      } finally {
        setLoading(false);
      }
    };

    fetchChatRooms();
  }, []);

  const handleEnterChat = (chatId) => {
    navigate(`/chats/${chatId}`);
  };

  if (loading) {
    return (
      <Box display="flex" justifyContent="center" alignItems="center" minHeight="80vh">
        <CircularProgress />
      </Box>
    );
  }

  if (error) {
    return (
      <Box display="flex" justifyContent="center" alignItems="center" minHeight="80vh">
        <Typography color="error">{error}</Typography>
      </Box>
    );
  }

  return (
    <Box
      sx={{
        width: '100%',
        maxWidth: 800,
        margin: 'auto',
        mt: 4,
        px: 2,
      }}
    >
      <Typography variant="h4" component="h1" gutterBottom sx={{ textAlign: 'center', mb: 4 }}>
        내 채팅 목록
      </Typography>
      <Paper elevation={3} sx={{ p: 2 }}>
        {chatRooms.length === 0 ? (
          <Typography variant="body1" sx={{ textAlign: 'center', py: 4 }}>
            아직 참여 중인 채팅방이 없습니다.
          </Typography>
        ) : (
          <List>
            {chatRooms.map((chat) => (
              <React.Fragment key={chat.chatId}>
                <ListItem alignItems="flex-start">
                  <ListItemText
                    primary={
                      <Typography
                        sx={{ fontWeight: 'bold' }}
                        component="span"
                        variant="h6" // 채팅방 목록에서 상대방 이름과 상품명을 더 강조
                        color="text.primary"
                      >
                        {chat.otherParticipantName}님과의 채팅
                      </Typography>
                    }
                    secondary={
                      <Box
                        sx={{
                          display: 'flex',
                          flexDirection: 'column',
                          mt: 1,
                        }}
                      >
                        <Typography
                          sx={{ display: 'inline', mb: 0.5 }}
                          component="span"
                          variant="body1" // 상품명도 일반 텍스트보다 살짝 강조
                          color="text.secondary"
                        >
                          상품명: {chat.productTitle || '상품 정보 없음'}{' '}
                          {/* 백엔드에서 productTitle을 내려줘야 합니다. */}
                        </Typography>
                        <Typography
                          sx={{ display: 'inline' }}
                          component="span"
                          variant="body2"
                          color="text.primary"
                        >
                          {chat.lastMessage
                            ? `최근 메시지: ${chat.lastMessage}`
                            : '메시지가 없습니다.'}
                          <br />
                          <Typography variant="caption" color="text.secondary">
                            {formatChatListTime(chat.createdAt)}
                          </Typography>
                          <br />
                        </Typography>
                      </Box>
                    }
                  />
                  <ListItemSecondaryAction>
                    <Button
                      variant="contained"
                      color="primary"
                      onClick={() => handleEnterChat(chat.chatId)}
                      sx={{ minWidth: '100px', mr: 2 }}
                    >
                      들어가기
                    </Button>
                  </ListItemSecondaryAction>
                </ListItem>
                <Divider component="li" />
              </React.Fragment>
            ))}
          </List>
        )}
      </Paper>
    </Box>
  );
};

export default ChatListPage;
