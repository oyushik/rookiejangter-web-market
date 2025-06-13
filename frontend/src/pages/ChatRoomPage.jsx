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
import { getUserIdFromToken } from '../utils/jwtUtils';
import SockJS from 'sockjs-client';
import { Stomp } from '@stomp/stompjs';

// 시간 포맷터 유틸리티 (예시, 실제 프로젝트의 FormatTime 사용)
const formatMessageTime = (isoString) => {
  if (!isoString) return '';
  const date = new Date(isoString);
  const options = { hour: '2-digit', minute: '2-digit', hour12: true };
  return date.toLocaleTimeString('ko-KR', options); // 한국 시간 형식
};

const ChatRoomPage = () => {
  const { chatRoomId } = useParams();
  const currentUserId = getUserIdFromToken();
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
    const fetchMessages = async () => {
      if (!currentUserId) {
        setError('로그인이 필요합니다.');
        setLoading(false);
        return;
      }

      try {
        // 1. 채팅방 정보 가져오기 (판매자/구매자 닉네임 등을 표시하기 위함)
        const chatResponse = await axios.get(`/api/chats/${chatRoomId}`);
        if (chatResponse.data.success) {
          setChatInfo(chatResponse.data.data);
        } else {
          setError(`채팅방 정보 로드 실패: ${chatResponse.data.message}`);
          setLoading(false);
          return;
        }

        // 2. 기존 메시지 로드
        const messageResponse = await axios.get(`/api/chats/${chatRoomId}/messages`);
        if (messageResponse.data.success) {
          // 메시지 목록을 시간 순서대로 정렬 (가장 오래된 것이 위로)
          const sortedMessages = messageResponse.data.data.content.sort(
            (a, b) => new Date(a.sentAt) - new Date(b.sentAt)
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

    fetchMessages();

    // 3. WebSocket 연결
    const socket = new SockJS('http://localhost:8080/ws/chat'); // 백엔드 WebSocket 엔드포인트
    stompClient.current = Stomp.over(socket);

    // STOMP 연결 시 헤더에 JWT 토큰 포함
    const accessToken = localStorage.getItem('accessToken');
    const headers = accessToken ? { Authorization: `Bearer ${accessToken}` } : {};

    stompClient.current.connect(
      headers,
      () => {
        console.log('WebSocket connected!');
        // 메시지 구독: RedisSubscriber가 "/sub/chat/room/{chatRoomId}"로 메시지를 보냄
        stompClient.current.subscribe(`/sub/chat/room/${chatRoomId}`, (message) => {
          const receivedMessage = JSON.parse(message.body);
          console.log('Received message:', receivedMessage);
          setMessages((prevMessages) => [...prevMessages, receivedMessage]);
          // 메시지 수신 후 읽음 처리 API 호출 (선택 사항)
          // axios.patch(`/api/chats/${chatRoomId}/messages/${receivedMessage.messageId}/read`);
        });
      },
      (error) => {
        console.error('WebSocket connection error:', error);
        setError('실시간 채팅 연결에 실패했습니다.');
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
  }, [chatRoomId, currentUserId]); // chatRoomId 또는 currentUserId 변경 시 재실행

  // 메시지가 업데이트될 때마다 최하단으로 스크롤
  useEffect(() => {
    scrollToBottom();
  }, [messages]);

  const handleSendMessage = (e) => {
    e.preventDefault();
    if (newMessage.trim() === '' || !stompClient.current || !stompClient.current.connected) {
      return;
    }

    // STOMP 메시지 전송 (백엔드 StompChatController의 @MessageMapping 경로)
    const messageToSend = {
      content: newMessage,
      // senderId는 백엔드에서 토큰으로 추출하므로 여기서는 보낼 필요 없음
    };

    const headers = {
      Authorization: `Bearer ${localStorage.getItem('accessToken')}`, // 메시지 전송 시에도 토큰 포함
    };

    stompClient.current.send(
      `/pub/chat/message/${chatRoomId}`, // 백엔드 Pub 경로
      headers,
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
    if (!chatInfo || !currentUserId) return '상대방';
    if (chatInfo.buyerId === currentUserId) {
      // 현재 유저가 구매자라면 판매자 정보 반환 (API에서 seller 객체에 userName이 있다고 가정)
      return chatInfo.seller ? chatInfo.seller.userName : '판매자';
    } else {
      // 현재 유저가 판매자라면 구매자 정보 반환 (API에서 buyer 객체에 userName이 있다고 가정)
      return chatInfo.buyer ? chatInfo.buyer.userName : '구매자';
    }
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
        {messages.map((msg, index) => (
          <Box
            key={msg.messageId || index} // messageId가 없으면 index 사용 (임시)
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
                {formatMessageTime(msg.sentAt)}{' '}
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
