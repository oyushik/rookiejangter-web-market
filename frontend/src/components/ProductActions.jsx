import React, { useState, useEffect } from 'react';
import { Button } from '@mui/material';
import FavoriteBorderIcon from '@mui/icons-material/FavoriteBorder';
import FavoriteIcon from '@mui/icons-material/Favorite';
import axios from 'axios';
import FormSnackbar from './FormSnackbar';
import { useNavigate } from 'react-router-dom';
import { useSelector } from 'react-redux';

const buttonStyle = {
  width: 200,
  height: 60,
  padding: '8px 20px',
  borderRadius: 0,
  cursor: 'pointer',
  fontSize: 22,
  fontWeight: 700,
};

const ProductActions = ({
  productId,
  isOwner = false,
  onEdit,
  onDelete,
  isInitiallyDibbed = false,
  product, // ProductDetailPage로부터 product 객체 받기
}) => {
  const navigate = useNavigate(); // useNavigate 훅 사용
  const currentUser = useSelector((state) => state.auth.identityInfo); // 로그인한 유저 정보

  const [isDibbed, setIsDibbed] = useState(isInitiallyDibbed);
  const [loading, setLoading] = useState(false);
  const [iconBump, setIconBump] = useState(false);

  // isInitiallyDibbed가 바뀌면 isDibbed도 동기화
  useEffect(() => {
    setIsDibbed(isInitiallyDibbed);
  }, [isInitiallyDibbed]);

  // 스낵바 상태 (ProductActions 내부에서 관리)
  const [snackbar, setSnackbar] = useState({
    open: false,
    message: '',
    severity: 'info',
  });

  const handleSnackbarClose = () => setSnackbar({ ...snackbar, open: false });

  // 찜하기 버튼 클릭 핸들러
  const handleWishlist = async () => {
    setLoading(true);
    setIconBump(true);
    setTimeout(() => setIconBump(false), 250);
    try {
      const token = localStorage.getItem('accessToken');
      if (!token) {
        setSnackbar({
          open: true,
          message: '먼저 로그인을 진행해주세요!',
          severity: 'error',
        });
        setLoading(false);
        return;
      }
      const res = await axios.put(
        `http://localhost:8080/api/dibs/${productId}`,
        {},
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );
      console.log('찜하기 응답:', res.data);
      if (res.data?.success) {
        setIsDibbed(res.data.data.dibbed);
        setSnackbar({
          open: true,
          message:
            res.data.message ||
            (res.data.data.dibbed ? '찜 목록에 추가되었습니다.' : '찜 목록에서 제거되었습니다.'),
          severity: res.data.data.dibbed ? 'success' : 'info',
        });
      } else {
        setSnackbar({
          open: true,
          message: '찜하기 처리 중 오류가 발생했습니다.',
          severity: 'error',
        });
      }
    } catch (e) {
      if (e.response && e.response.status === 404) {
        setSnackbar({
          open: true,
          message: 'ID를 찾을 수 없습니다!',
          severity: 'error',
        });
      } else {
        setSnackbar({
          open: true,
          message: '찜하기 처리 중 오류가 발생했습니다.',
          severity: 'error',
        });
      }
    } finally {
      setLoading(false);
    }
  };

  // 채팅 시작 핸들러 (ProductActions로 이동됨)
  const handleStartChat = async () => {
    const token = localStorage.getItem('accessToken');
    if (!token) {
      setSnackbar({
        open: true,
        message: '로그인 후 이용해주세요.',
        severity: 'warning',
      });
      navigate('/login'); // 로그인 페이지로 리디렉션
      return;
    }

    // 판매자 본인인지 확인
    if (currentUser?.id === product?.seller?.id) {
      setSnackbar({
        open: true,
        message: '자신과의 채팅은 불가능합니다.',
        severity: 'info',
      });
      return;
    }

    if (!product || !product.seller || !product.seller.id) {
      setSnackbar({
        open: true,
        message: '판매자 정보를 찾을 수 없습니다.',
        severity: 'error',
      });
      return;
    }

    const sellerId = product.seller.id;
    // ProductId는 ProductActions의 props로 이미 받으므로 productId 사용
    const currentProductId = productId;

    try {
      const response = await axios.post(
        'http://localhost:8080/api/chats',
        {
          sellerId: sellerId,
          productId: currentProductId,
        },
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );

      if (response.data.success) {
        const chatId = response.data.data.chatId;
        setSnackbar({
          open: true,
          message: '채팅방이 성공적으로 생성되었습니다.',
          severity: 'success',
        });
        navigate(`/chats/${chatId}`);
      } else {
        setSnackbar({
          open: true,
          message: `채팅방 생성 실패: ${response.data.message}`,
          severity: 'error',
        });
      }
    } catch (error) {
      console.error('채팅방 생성 요청 실패:', error);
      let errorMessage = '채팅방 생성 중 오류가 발생했습니다.';
      if (error.response && error.response.data && error.response.data.message) {
        errorMessage = error.response.data.message;
      }
      setSnackbar({
        open: true,
        message: errorMessage,
        severity: 'error',
      });
    }
  };

  return (
    <div
      style={{
        position: 'absolute',
        left: 0,
        bottom: 0,
        width: '100%',
        display: 'flex',
        justifyContent: 'flex-end',
        gap: 12,
        paddingRight: 40,
        boxSizing: 'border-box',
      }}
    >
      {isOwner ? (
        <>
          <Button
            variant="contained"
            color="info"
            sx={{
              ...buttonStyle,
            }}
            onClick={onEdit}
          >
            상품 수정
          </Button>
          <Button
            variant="contained"
            color="error"
            sx={{
              ...buttonStyle,
            }}
            onClick={onDelete}
          >
            상품 삭제
          </Button>
        </>
      ) : (
        <>
          <Button
            variant={isDibbed ? 'contained' : 'outlined'}
            color={isDibbed ? 'error' : 'inherit'}
            sx={{
              ...buttonStyle,
              border: '1.5px solid #e0e0e0',
              background: '#fff',
              color: '#222',
              opacity: loading ? 0.6 : 1,
              fontSize: 22,
              fontWeight: 700,
            }}
            onClick={handleWishlist}
            disabled={loading}
            endIcon={
              <span
                style={{
                  display: 'flex',
                  alignItems: 'center',
                  transition: 'transform 0.25s cubic-bezier(.4,2,.6,1)',
                  transform: iconBump ? 'scale(1.4)' : 'scale(1)',
                }}
              >
                {isDibbed ? (
                  <FavoriteIcon color="error" sx={{ fontSize: '36px' }} />
                ) : (
                  <FavoriteBorderIcon color="default" sx={{ fontSize: '36px' }} />
                )}
              </span>
            }
          >
            {isDibbed ? '찜 취소' : '찜하기'}
          </Button>
          <Button
            variant="contained"
            color="success"
            sx={{ ...buttonStyle, fontSize: 22, fontWeight: 700 }}
            onClick={handleStartChat}
          >
            채팅 시작하기
          </Button>
        </>
      )}
      <FormSnackbar
        open={snackbar.open}
        message={snackbar.message}
        severity={snackbar.severity}
        onClose={handleSnackbarClose}
      />
    </div>
  );
};

export default ProductActions;
