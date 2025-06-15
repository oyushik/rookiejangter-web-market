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

const CHAT_API_BASE_URL = 'http://localhost:8080/api/chats'; // 실제 API 엔드포인트로 변경 필요

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
  useEffect(() => {
    document.body.style.overflow = 'hidden';
    document.body.style.overflowY = 'hidden';
    document.body.style.overflowX = 'auto';
    return () => {
      document.body.style.overflowY = 'hidden';
      document.body.style.overflowX = 'auto';
    };
  }, []);

  const [chatRooms, setChatRooms] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [currentUserId, setCurrentUserId] = useState(null);
  const navigate = useNavigate();

  useEffect(() => {
    // JWT 토큰에서 현재 사용자 ID 추출
    try {
      const accessToken = localStorage.getItem('accessToken');
      if (accessToken) {
        const decodedToken = jwtDecode(accessToken);
        setCurrentUserId(decodedToken.sub ? parseInt(decodedToken.sub, 10) : null);
      }
    } catch (error) {
      console.error('Failed to decode JWT token from local storage:', error);
      setCurrentUserId(null);
    }

    const fetchChatRooms = async () => {
      setLoading(true);
      setError(null);
      try {
        const token = localStorage.getItem('accessToken');
        if (!token) {
          setError('로그인이 필요합니다. 토큰이 없습니다.');
          setLoading(false);
          return;
        }
        const headers = { Authorization: `Bearer ${token}` };
        // getChatsByUserId 호출 (페이징은 기본값 사용)
        const response = await axios.get(CHAT_API_BASE_URL, { headers });
        if (response.data.success) {
          setChatRooms(response.data.data.content);
        } else {
          setError(`채팅방 목록 로드 실패: ${response.data.message}`);
        }
      } catch (err) {
        console.error('Failed to load chat rooms:', err);
        setError('채팅방 목록을 불러오는 중 오류가 발생했습니다.');
      } finally {
        setLoading(false);
      }
    };

    fetchChatRooms();
  }, []);

  const handleEnterChat = (chatId) => {
    navigate(`/chats/${chatId}`); // ChatRoomPage로 이동
  };

  if (loading) {
    return <CircularProgress sx={{ display: 'block', margin: '20px auto' }} />;
  }

  if (error) {
    return (
      <Typography color="error" sx={{ textAlign: 'center', mt: 4 }}>
        {error}
      </Typography>
    );
  }

  return (
    <Box sx={{ p: 3, maxWidth: 800, margin: 'auto' }}>
      <Typography variant="h4" component="h1" gutterBottom sx={{ mb: 3, textAlign: 'center' }}>
        내 채팅방 목록
      </Typography>
      <Paper elevation={3} sx={{ borderRadius: '8px', overflow: 'hidden' }}>
        {chatRooms.length === 0 ? (
          <Typography variant="body1" color="textSecondary" sx={{ p: 4, textAlign: 'center' }}>
            참여하고 있는 채팅방이 없습니다.
          </Typography>
        ) : (
          <List>
            {chatRooms.map((chat) => (
              <React.Fragment key={chat.chatId}>
                <ListItem
                  alignItems="flex-start"
                  sx={{ py: 2, '&:hover': { backgroundColor: 'action.hover' } }}
                >
                  <ListItemText
                    primary={
                      <Typography variant="h6" component="span">
                        {`채팅방 (상품 ID: ${chat.productId})`}
                      </Typography>
                    }
                    secondary={
                      <Box
                        sx={{
                          display: 'flex',
                          justifyContent: 'space-between',
                          alignItems: 'flex-end',
                          mt: 1,
                        }}
                      >
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
                      sx={{ minWidth: '100px', mr: 2 }} // 오른쪽 margin 추가로 버튼 위치 조절
                    >
                      채팅 참여
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
