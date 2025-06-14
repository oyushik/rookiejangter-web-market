import React, { useState, useEffect, useRef } from 'react';
import { useParams } from 'react-router-dom';
import {
  Box,
  TextField,
  Button,
  Typography,
  Paper,
  CircularProgress,
  Divider,
} from '@mui/material';
import axios from 'axios';
import SockJS from 'sockjs-client';
import { Stomp } from '@stomp/stompjs';
import { jwtDecode } from 'jwt-decode';

const CHAT_API_BASE_URL = 'http://localhost:8080/api/chats'; // 실제 API 엔드포인트로 변경 필요

// 시간 포맷터 유틸리티 (예시, 실제 프로젝트의 FormatTime 사용)
const formatMessageTime = (isoString) => {
  if (!isoString) return '';
  const date = new Date(isoString);
  const options = { hour: '2-digit', minute: '2-digit', hour12: true };
  return date.toLocaleTimeString('ko-KR', options); // 한국 시간 형식
};

const ChatRoomPage = () => {
  const { chatId } = useParams();

  // 현재 로그인한 사용자 ID를 토큰에서 직접 파싱
  const [currentUserId, setCurrentUserId] = useState(null);
  useEffect(() => {
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
  }, []);

  const [messages, setMessages] = useState([]);
  const [newMessage, setNewMessage] = useState('');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [chatInfo, setChatInfo] = useState(null); // 채팅방 정보 저장
  const stompClient = useRef(null); // STOMP 클라이언트 인스턴스 저장
  const messagesEndRef = useRef(null); // 메시지 스크롤을 위한 ref

  // 메시지 목록의 최하단으로 스크롤
  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  };

  // 컴포넌트 마운트 시 메시지 로드 및 WebSocket 연결
  useEffect(() => {
    const token = localStorage.getItem('accessToken');
    if (!token) {
      setError('로그인이 필요합니다. 토큰이 없습니다.');
      setLoading(false);
      return;
    }
    const headers = { Authorization: `Bearer ${token}` };

    const fetchChatData = async () => {
      try {
        // 1. 채팅방 정보 가져오기 (판매자/구매자 닉네임 등을 표시하기 위함)
        const chatResponse = await axios.get(`${CHAT_API_BASE_URL}/${chatId}`, { headers });
        if (chatResponse.data.success) {
          setChatInfo(chatResponse.data.data);
        } else {
          setError(`채팅방 정보 로드 실패: ${chatResponse.data.message}`);
          setLoading(false);
          return;
        }

        // 2. 기존 메시지 로드
        const messageResponse = await axios.get(`${CHAT_API_BASE_URL}/${chatId}/messages`, {
          headers,
        });
        if (messageResponse.data.success) {
          const sortedMessages = messageResponse.data.data.content.sort(
            (a, b) => new Date(a.createdAt) - new Date(b.createdAt)
          );
          setMessages(sortedMessages);
        } else {
          setError(`메시지 로드 실패: ${messageResponse.data.message}`);
        }
      } catch (err) {
        console.error('Failed to load chat data:', err);
        setError('채팅 데이터를 불러오는 중 오류가 발생했습니다.');
      } finally {
        setLoading(false);
      }
    };

    fetchChatData();

    // 3. WebSocket 연결
    stompClient.current = Stomp.over(() => new SockJS('http://localhost:8080/ws/chat'));

    stompClient.current.connect(
      headers,
      () => {
        console.log('WebSocket connected!');
        // 메시지 구독 부분
        stompClient.current.subscribe(`/sub/chat/room/${chatId}`, (message) => {
          const receivedMessage = JSON.parse(message.body);
          console.log('Received message:', receivedMessage);

          setMessages((prevMessages) => {
            // receivedMessage.messageId가 없는 경우도 처리 (안전장치)
            const isDuplicate = prevMessages.some(
              (msg) => msg.messageId === receivedMessage.messageId
            );
            if (isDuplicate) {
              console.log('Duplicate message received, ignoring:', receivedMessage);
              return prevMessages; // 중복이면 이전 상태 반환 (추가하지 않음)
            }
            return [...prevMessages, receivedMessage]; // 중복이 아니면 새 메시지 추가
          });
        });
      },
      (error) => {
        console.error('WebSocket connection error:', error);
        setError('실시간 채팅 연결에 실패했습니다. ' + error.message);
      }
    );

    // 컴포넌트 언마운트 시 WebSocket 연결 해제
    return () => {
      if (stompClient.current && stompClient.current.connected) {
        stompClient.current.disconnect(() => {
          console.log('WebSocket disconnected!');
        });
      }
    };
  }, [chatId]); // currentUserId 제거 (초기 로드만)

  // 메시지가 업데이트될 때마다 최하단으로 스크롤
  useEffect(() => {
    scrollToBottom();
  }, [messages]);

  const handleSendMessage = (e) => {
    e.preventDefault();
    if (newMessage.trim() === '' || !stompClient.current || !stompClient.current.connected) {
      return;
    }

    const token = localStorage.getItem('accessToken');
    const sendHeaders = {
      Authorization: `Bearer ${token}`, // 메시지 전송 시에도 토큰 포함
    };

    const messageToSend = {
      content: newMessage,
      // senderId는 백엔드에서 토큰으로 추출하므로 여기서는 보낼 필요 없음
    };

    stompClient.current.send(
      `/pub/chat/message/${chatId}`, // 백엔드 Pub 경로
      sendHeaders,
      JSON.stringify(messageToSend)
    );

    setNewMessage('');
  };

  if (loading) {
    return <CircularProgress sx={{ display: 'block', margin: '20px auto' }} />;
  }

  if (error) {
    return <Typography color="error">{error}</Typography>;
  }

  // 채팅 상대방의 이름을 표시
  const getChatPartnerName = () => {
    if (!chatInfo || currentUserId === null) return '상대방'; // currentUserId 로드 전에는 기본값

    // chatInfo.buyer, chatInfo.seller 객체에 userName이 있다고 가정
    // DTO 구조: chatResponse.data.data.buyer.userName, chatResponse.data.data.seller.userName
    if (chatInfo.buyer && chatInfo.buyer.id === currentUserId) {
      return chatInfo.seller ? chatInfo.seller.userName : '판매자';
    } else if (chatInfo.seller && chatInfo.seller.id === currentUserId) {
      return chatInfo.buyer ? chatInfo.buyer.userName : '구매자';
    }
    return '상대방'; // 일치하는 ID가 없을 경우
  };

  return (
    <Box sx={{ display: 'flex', flexDirection: 'column', height: '85vh', p: 2 }}>
      <Typography variant="h5" sx={{ mb: 2 }}>
        {chatInfo ? `${getChatPartnerName()}님과의 채팅` : '채팅방'}
      </Typography>
      <Divider sx={{ mb: 2 }} />

      <Box
        sx={{
          flexGrow: 1,
          overflowY: 'auto',
          p: 1,
          border: '1px solid #e0e0e0',
          borderRadius: '4px',
        }}
      >
        {messages.length === 0 && (
          <Typography variant="body2" color="textSecondary" align="center" sx={{ mt: 2 }}>
            아직 메시지가 없습니다. 대화를 시작해보세요!
          </Typography>
        )}
        {messages.map((msg) => (
          <Box
            key={msg.messageId}
            sx={{
              display: 'flex',
              justifyContent: msg.senderId === currentUserId ? 'flex-end' : 'flex-start',
              mb: 1,
            }}
          >
            <Paper
              variant="outlined"
              sx={{
                p: 1,
                maxWidth: '70%',
                backgroundColor: msg.senderId === currentUserId ? 'primary.light' : 'grey.200',
                color: msg.senderId === currentUserId ? 'white' : 'black',
                borderRadius: '10px',
                borderTopRightRadius: msg.senderId === currentUserId ? '0px' : '10px', // 자기 메시지는 오른쪽 위 각짐
                borderTopLeftRadius: msg.senderId === currentUserId ? '10px' : '0px', // 상대 메시지는 왼쪽 위 각짐
              }}
            >
              <Typography variant="caption" sx={{ display: 'block', mb: 0.5 }}>
                {msg.senderId === currentUserId ? '나' : getChatPartnerName()}
              </Typography>
              <Typography variant="body1">{msg.content}</Typography>
              <Typography variant="caption" display="block" align="right" sx={{ mt: 0.5 }}>
                {formatMessageTime(msg.createdAt)}{' '}
                {msg.isRead ? '' : msg.senderId !== currentUserId ? '' : '읽지않음'}
              </Typography>
            </Paper>
          </Box>
        ))}
        <div ref={messagesEndRef} /> {/* 스크롤 위치를 위한 더미 div */}
      </Box>

      <Box component="form" onSubmit={handleSendMessage} sx={{ display: 'flex', mt: 2 }}>
        <TextField
          fullWidth
          variant="outlined"
          placeholder="메시지를 입력하세요..."
          value={newMessage}
          onChange={(e) => setNewMessage(e.target.value)}
          sx={{ mr: 1 }}
        />
        <Button type="submit" variant="contained" color="primary">
          전송
        </Button>
      </Box>
    </Box>
  );
};

export default ChatRoomPage;
