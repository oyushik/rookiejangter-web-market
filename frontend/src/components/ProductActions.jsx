import React, { useState } from 'react';
import { Button } from '@mui/material';
import FavoriteBorderIcon from '@mui/icons-material/FavoriteBorder';
import FavoriteIcon from '@mui/icons-material/Favorite';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';

const buttonStyle = {
  width: 200,
  height: 60,
  padding: '8px 20px',
  borderRadius: 0,
  cursor: 'pointer',
  fontWeight: 700,
};

const ProductActions = ({ productId, isInitiallyLiked = false }) => {
  const [isLiked, setIsLiked] = useState(isInitiallyLiked);
  const [loading, setLoading] = useState(false);
  const [iconBump, setIconBump] = useState(false);
  const navigate = useNavigate();

  // 실제 서버 연동 코드(주석처리)
  const handleWishlist = async () => {
    setLoading(true);
    setIconBump(true); // 아이콘 scale 효과 시작
    setTimeout(() => setIconBump(false), 250); // 0.25초 후 원래 크기로
    try {
      const token = localStorage.getItem('accessToken');
      if (!token) {
        alert('먼저 로그인을 진행해주세요!');
        setLoading(false);
        return;
      }
      const res = await axios.put(
        `/api/wishlist/${productId}`,
        {},
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );
      if (res.data?.success) {
        setIsLiked(res.data.data.isLiked);
        alert('찜 목록에 추가되었습니다.');
      } else {
        alert('찜 목록에서 제거되었습니다.');
      }
    } catch (e) {
      if (e.response && e.response.status === 404) {
        navigate('/err/NotFound');
      } else {
        alert('찜하기 처리 중 오류가 발생했습니다.');
      }
    } finally {
      setLoading(false);
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
        fontSize: 22,
        fontWeight: 700,
      }}
    >
      <Button
        variant={isLiked ? 'contained' : 'outlined'}
        color={isLiked ? 'error' : 'inherit'}
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
            {isLiked ? (
              <FavoriteIcon sx={{ color: '#EA002C', fontSize: '36px' }} />
            ) : (
              <FavoriteBorderIcon sx={{ color: '#222', fontSize: '36px' }} />
            )}
          </span>
        }
      >
        {isLiked ? '찜 취소' : '찜하기'}
      </Button>

      <Button
        variant="contained"
        color="success"
        sx={{ ...buttonStyle, fontSize: 22, fontWeight: 700 }}
      >
        거래 신청
      </Button>
    </div>
  );
};

export default ProductActions;
