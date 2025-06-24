import React, { useState, useEffect } from 'react';
import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Button,
  TextField,
  Typography,
  Select,
  MenuItem,
  InputLabel,
  FormControl,
  CircularProgress,
} from '@mui/material';

import { cancelReservation } from '../api/reservationService';

const CancelReservationModal = ({
  open,
  onClose,
  chatId,
  currentUserId,
  chatInfo, // chatInfo 객체를 받아서 productId, buyerId, sellerId 추출
  onSuccess, // 예약 취소 성공 시 호출될 콜백 함수
}) => {
  const [cancelReasonId, setCancelReasonId] = useState(1); // 임시로 1로 설정
  const [cancelDetail, setCancelDetail] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  // 모달이 열릴 때마다 상태 초기화 (선택 사항, 필요에 따라)
  useEffect(() => {
    if (open) {
      setCancelReasonId(1);
      setCancelDetail('');
      setError(null);
    }
  }, [open]);

  const handleConfirmCancel = async () => {
    if (!chatInfo || !chatId || !chatInfo.productId) {
      setError('예약을 취소하는 데 필요한 정보가 부족합니다. 잠시 후 다시 시도해주세요.');
      return;
    }

    if (currentUserId !== chatInfo.sellerId) {
      setError('예약 취소 권한이 없습니다. 예약은 판매자만 취소할 수 있습니다.');
      return;
    }

    setLoading(true);
    setError(null); // 에러 초기화

    const token = localStorage.getItem('accessToken');

    const cancelationRequestData = {
      cancelationReasonId: cancelReasonId,
      cancelationDetail: cancelDetail,
      productId: chatInfo.productId,
      buyerId: chatInfo.buyerId,
      sellerId: chatInfo.sellerId,
      isCanceledByBuyer: currentUserId === chatInfo.buyerId,
    };

    try {
      const result = await cancelReservation(chatId, cancelationRequestData, token);

      if (result.success) {
        onSuccess(); // 부모 컴포넌트에게 성공 알림
        onClose(); // 모달 닫기
      } else {
        setError(result.error || '예약 취소 중 알 수 없는 오류가 발생했습니다.');
      }
    } catch (err) {
      console.error('Error canceling reservation:', err);
      setError('예약 취소 중 네트워크 오류가 발생했습니다.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <Dialog open={open} onClose={onClose} fullWidth maxWidth="sm">
      <DialogTitle>예약 취소</DialogTitle>
      <DialogContent>
        <Typography variant="body2" color="textSecondary" sx={{ mb: 2 }}>
          예약을 취소하는 사유를 선택하고, 자세한 내용을 입력해주세요.
          {error && (
            <Typography color="error" variant="body2" sx={{ mt: 1 }}>
              {error}
            </Typography>
          )}
        </Typography>
        <FormControl fullWidth margin="dense" required>
          <InputLabel id="cancel-reason-label">취소 사유</InputLabel>
          <Select
            labelId="cancel-reason-label"
            id="cancel-reason-select"
            value={cancelReasonId}
            label="취소 사유"
            onChange={(e) => setCancelReasonId(e.target.value)}
            disabled={loading}
          >
            {/* 실제 취소 사유 목록은 API를 통해 받아오는 것이 좋습니다. */}
            <MenuItem value={1}>구매자와 협의 완료</MenuItem>
            {/* <MenuItem value={2}>개인적인 사정</MenuItem> */}
            {/* <MenuItem value={3}>상품 문제</MenuItem> */}
          </Select>
        </FormControl>
        <TextField
          autoFocus
          margin="dense"
          id="cancelDetail"
          label="취소 상세 내용 (선택 사항)"
          type="text"
          fullWidth
          multiline
          rows={4}
          value={cancelDetail}
          onChange={(e) => setCancelDetail(e.target.value)}
          inputProps={{ maxLength: 255 }}
          helperText={`${cancelDetail.length}/255`}
          disabled={loading}
          sx={{ mt: 2 }}
        />
        {loading && <CircularProgress size={24} sx={{ mt: 2, display: 'block', margin: 'auto' }} />}
      </DialogContent>
      <DialogActions>
        <Button onClick={onClose} color="primary" disabled={loading}>
          취소
        </Button>
        <Button onClick={handleConfirmCancel} color="warning" disabled={loading}>
          예약 취소 확정
        </Button>
      </DialogActions>
    </Dialog>
  );
};

export default CancelReservationModal;
