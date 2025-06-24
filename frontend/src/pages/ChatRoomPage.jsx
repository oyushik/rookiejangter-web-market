import React, { useState, useEffect, useRef } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
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

import { createReservation } from '../api/reservationService';
import CancelReservationModal from '../components/CancelReservationModal';

const CHAT_API_BASE_URL = 'http://localhost:8080/api/chats';
const PRODUCT_DETAIL_BASE_URL = '/products'; // 상품 상세 페이지 URL 접두사

// 시간 포맷터 유틸리티
const formatMessageTime = (isoString) => {
  if (!isoString) return '';
  const date = new Date(isoString);
  const options = { hour: '2-digit', minute: '2-digit', hour12: true };
  return date.toLocaleTimeString('ko-KR', options); // 한국 시간 형식
};

const ChatRoomPage = () => {
  const { chatId } = useParams();
  const navigate = useNavigate();

  const [currentUserId, setCurrentUserId] = useState(null);
  const [messages, setMessages] = useState([]);
  const [newMessage, setNewMessage] = useState('');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [chatInfo, setChatInfo] = useState(null); // chatInfo 상태 추가
  const [otherParticipantName, setOtherParticipantName] = useState('상대방');
  const [productTitle, setProductTitle] = useState('상품 정보 없음');
  const [productId, setProductId] = useState(null);
  const stompClient = useRef(null);
  const messagesEndRef = useRef(null);

  // 예약 취소 모달 관련 state: open/close만 여기서 관리
  const [openCancelModal, setOpenCancelModal] = useState(false);

  // 컴포넌트 마운트 시 현재 사용자 ID를 토큰에서 파싱
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

  // chatId 또는 currentUserId가 준비되면 채팅 데이터 로드 및 WebSocket 연결
  useEffect(() => {
    if (currentUserId === null || !chatId) {
      setLoading(false);
      return;
    }

    const token = localStorage.getItem('accessToken');
    if (!token) {
      setError('로그인이 필요합니다. 토큰이 없습니다.');
      setLoading(false);
      return;
    }
    const headers = { Authorization: `Bearer ${token}` };

    const fetchChatDataAndConnectWs = async () => {
      try {
        // 채팅방 정보 가져오기
        const chatResponse = await axios.get(`${CHAT_API_BASE_URL}/${chatId}`, { headers });
        if (chatResponse.data.success) {
          const chatData = chatResponse.data.data;
          setChatInfo(chatData); // chatInfo 스테이트 설정

          // 응답 구조에 따라 상품명 설정
          if (chatData.product && chatData.product.title) {
            setProductTitle(chatData.product.title);
          } else {
            setProductTitle('상품 정보 없음');
          }

          // productId 설정
          if (chatData.productId) {
            setProductId(chatData.productId);
          } else {
            setProductId(null);
          }

          // 응답 구조와 현재 사용자 ID에 따라 상대방 이름 설정
          if (currentUserId === chatData.buyerId) {
            setOtherParticipantName(chatData.seller?.userName || '상대방');
          } else if (currentUserId === chatData.sellerId) {
            setOtherParticipantName(chatData.buyer?.userName || '상대방');
          } else {
            setOtherParticipantName('상대방');
          }
        } else {
          setError(`채팅방 정보 로드 실패: ${chatResponse.data.message}`);
          setLoading(false);
          return;
        }

        // 기존 메시지 로드
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

      // WebSocket 연결
      stompClient.current = Stomp.over(() => new SockJS('http://localhost:8080/ws/chat'));

      stompClient.current.connect(
        headers,
        (frame) => {
          console.log('WebSocket connected:', frame);
          // 메시지 구독
          stompClient.current.subscribe(`/sub/chat/room/${chatId}`, (message) => {
            const receivedMessage = JSON.parse(message.body);
            console.log('Received message:', receivedMessage);

            setMessages((prevMessages) => {
              const isDuplicate =
                receivedMessage.messageId &&
                prevMessages.some((msg) => msg.messageId === receivedMessage.messageId);
              if (isDuplicate) {
                console.log('Duplicate message received, ignoring:', receivedMessage);
                return prevMessages;
              }
              return [...prevMessages, receivedMessage];
            });
          });
        },
        (error) => {
          console.error('WebSocket connection error:', error);
          setError('실시간 채팅 연결에 실패했습니다. ' + error.message);
        }
      );
    };

    fetchChatDataAndConnectWs();

    // 컴포넌트 언마운트 시 WebSocket 연결 해제
    return () => {
      if (stompClient.current && stompClient.current.connected) {
        stompClient.current.disconnect(() => {
          console.log('WebSocket disconnected!');
        });
      }
    };
  }, [chatId, currentUserId]);

  // 메시지가 업데이트될 때마다 최하단으로 스크롤
  useEffect(() => {
    if (messagesEndRef.current) {
      messagesEndRef.current.parentNode.scrollTo({
        top: messagesEndRef.current.offsetTop,
        behavior: 'smooth',
      });
    }
  }, [messages]);

  const handleSendMessage = (e) => {
    e.preventDefault();
    if (newMessage.trim() === '' || !stompClient.current || !stompClient.current.connected) {
      return;
    }

    const token = localStorage.getItem('accessToken');
    const sendHeaders = {
      Authorization: `Bearer ${token}`,
    };

    const messageToSend = {
      content: newMessage,
    };

    stompClient.current.publish({
      destination: `/pub/chat/message/${chatId}`,
      headers: sendHeaders,
      body: JSON.stringify(messageToSend),
    });

    setNewMessage('');
  };

  const handleGoToChatList = () => {
    navigate('/chats');
  };

  const handleViewProductDetails = () => {
    if (productId) {
      navigate(`${PRODUCT_DETAIL_BASE_URL}/${productId}`);
    } else {
      alert('상품 정보를 찾을 수 없습니다.');
    }
  };

  const handleLeaveChat = async () => {
    if (!chatId) {
      setError('채팅방 ID를 찾을 수 없습니다.');
      return;
    }

    const confirmLeave = window.confirm(
      '정말로 채팅방을 나가시겠습니까? 모든 대화 기록이 삭제됩니다.'
    );
    if (!confirmLeave) {
      return;
    }

    setLoading(true);
    const token = localStorage.getItem('accessToken');
    const headers = { Authorization: `Bearer ${token}` };

    try {
      const response = await axios.delete(`${CHAT_API_BASE_URL}/${chatId}`, { headers });
      if (response.data.success) {
        alert('채팅방이 성공적으로 삭제되었습니다.');
        navigate('/chats');
      } else {
        alert(`채팅방 삭제 실패: ${response.data.message || response.data.error}`);
        setError(`채팅방 삭제 실패: ${response.data.message || response.data.error}`);
      }
    } catch (err) {
      console.error('Error leaving chat:', err);
      if (err.response && err.response.data && err.response.data.message) {
        alert(`채팅방 삭제 중 오류 발생: ${err.response.data.message}`);
        setError(`채팅방 삭제 중 오류 발생: ${err.response.data.message}`);
      } else {
        alert('채팅방 삭제 중 알 수 없는 오류가 발생했습니다.');
        setError('채팅방 삭제 중 알 수 없는 오류가 발생했습니다.');
      }
    } finally {
      setLoading(false);
    }
  };

  // reservationService를 사용하여 예약 생성 로직을 간소화
  const handleCreateReservation = async () => {
    if (!chatInfo || !chatId || !productId) {
      alert('예약을 생성하는 데 필요한 정보가 부족합니다. 잠시 후 다시 시도해주세요.');
      return;
    }

    if (currentUserId !== chatInfo.sellerId) {
      alert('예약은 판매자만 생성할 수 있습니다.');
      return;
    }

    const confirmReservation = window.confirm(
      '이 채팅방에 대한 거래 예약을 생성하시겠습니까? 상품 상태가 "예약 중"으로 변경됩니다.'
    );
    if (!confirmReservation) {
      return;
    }

    setLoading(true);
    const token = localStorage.getItem('accessToken');

    try {
      const result = await createReservation(parseInt(chatId), token); // chatId를 숫자로 변환하여 전달

      if (result.success) {
        alert('거래 예약이 성공적으로 생성되었습니다. 상품 상태가 예약 중으로 변경됩니다.');
        // chatInfo 상태를 업데이트하여 UI 즉시 반영
        setChatInfo((prevChatInfo) => ({
          ...prevChatInfo,
          product: {
            ...prevChatInfo.product,
            isReserved: true, // isReserved 상태를 true로 변경
          },
        }));
      } else {
        alert(`예약 생성 실패: ${result.error || '알 수 없는 오류'}`);
        setError(`예약 생성 실패: ${result.error || '알 수 없는 오류'}`);
      }
    } catch (err) {
      console.error('Error creating reservation outside service:', err);
      alert('예약 생성 중 알 수 없는 오류가 발생했습니다.');
      setError('예약 생성 중 알 수 없는 오류가 발생했습니다.');
    } finally {
      setLoading(false);
    }
  };

  // 예약 취소 모달 열기 함수 (이제 모달 상태만 변경)
  const handleOpenCancelModal = () => {
    if (!chatInfo || !chatId || !productId) {
      alert('예약을 취소하는 데 필요한 정보가 부족합니다. 잠시 후 다시 시도해주세요.');
      return;
    }
    if (currentUserId !== chatInfo.sellerId) {
      alert('예약은 판매자만 취소할 수 있습니다.');
      return;
    }
    setOpenCancelModal(true);
  };

  // 예약 취소 모달 닫기 함수 (이제 모달 상태만 변경)
  const handleCloseCancelModal = () => {
    setOpenCancelModal(false);
  };

  // 예약 취소 성공 시 호출될 콜백 함수
  const handleCancelSuccess = () => {
    alert('예약이 성공적으로 취소되었습니다. 상품 상태가 판매 중으로 변경됩니다.');
    setChatInfo((prevChatInfo) => ({
      ...prevChatInfo,
      product: {
        ...prevChatInfo.product,
        isReserved: false, // isReserved 상태를 false로 변경
      },
    }));
    handleCloseCancelModal(); // 모달 닫기
  };

  if (loading) {
    return <CircularProgress sx={{ display: 'block', margin: '20px auto' }} />;
  }

  if (error) {
    return <Typography color="error">{error}</Typography>;
  }

  // 현재 로그인한 사용자가 이 채팅방의 판매자인지 확인
  const isCurrentUserSeller = chatInfo && currentUserId === chatInfo.sellerId;
  // 상품이 예약 중인지 확인 (chatInfo와 product, isReserved 필드가 모두 있을 때만)
  const isProductReserved = chatInfo?.product?.isReserved === true;

  return (
    <Box sx={{ display: 'flex', flexDirection: 'column', height: '85vh', p: 2 }}>
      {/* 제목 및 버튼 영역 */}
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 1 }}>
        <Box>
          <Typography variant="h5">{otherParticipantName}님과의 채팅</Typography>
          <Typography variant="subtitle1" color="text.secondary">
            상품명: {productTitle}
          </Typography>
        </Box>
        <Box sx={{ display: 'flex', gap: 1 }}>
          {/* 상품 상세 보기 버튼 */}
          <Button
            variant="outlined"
            color="info"
            onClick={handleViewProductDetails}
            disabled={!productId}
          >
            상품 상세 보기
          </Button>

          {/* 예약 관련 버튼 (판매자에게만 표시) */}
          {isCurrentUserSeller &&
            (isProductReserved ? (
              // 상품이 예약 중일 경우 '예약 취소' 버튼 표시
              <Button
                variant="contained"
                color="warning"
                onClick={handleOpenCancelModal} // 모달 열기 함수 호출
                disabled={loading}
              >
                예약 취소
              </Button>
            ) : (
              // 상품이 예약 중이 아닐 경우 '예약 생성' 버튼 표시
              <Button
                variant="contained"
                color="success"
                onClick={handleCreateReservation}
                disabled={loading}
              >
                예약 생성
              </Button>
            ))}

          <Button variant="outlined" color="primary" onClick={handleGoToChatList}>
            채팅 목록으로
          </Button>
          <Button variant="contained" color="error" onClick={handleLeaveChat}>
            채팅 나가기
          </Button>
        </Box>
      </Box>
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
                borderTopRightRadius: msg.senderId === currentUserId ? '0px' : '10px',
                borderTopLeftRadius: msg.senderId === currentUserId ? '10px' : '0px',
              }}
            >
              <Typography variant="caption" sx={{ display: 'block', mb: 0.5 }}>
                {msg.senderId === currentUserId ? '나' : otherParticipantName}
              </Typography>
              <Typography variant="body1">{msg.content}</Typography>
              <Typography variant="caption" display="block" align="right" sx={{ mt: 0.5 }}>
                {formatMessageTime(msg.createdAt)}
                {msg.isRead ? '' : msg.senderId !== currentUserId ? '' : ' 읽지않음'}
              </Typography>
            </Paper>
          </Box>
        ))}
        <div ref={messagesEndRef} />
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

      {/* 분리된 예약 취소 모달 컴포넌트 렌더링 */}
      {openCancelModal && ( // 모달이 열려있을 때만 렌더링
        <CancelReservationModal
          open={openCancelModal}
          onClose={handleCloseCancelModal}
          chatId={chatId}
          currentUserId={currentUserId}
          chatInfo={chatInfo} // chatInfo 전체를 props로 전달
          onSuccess={handleCancelSuccess} // 성공 콜백 함수 전달
        />
      )}
    </Box>
  );
};

export default ChatRoomPage;
