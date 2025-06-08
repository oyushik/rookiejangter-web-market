import React, { useState } from 'react';
import { Modal, Box, Typography, Button, MenuItem, TextField, Select, FormControl, InputLabel } from '@mui/material';
import axios from 'axios';
import FormSnackbar from './FormSnackbar';

const modalStyle = {
  position: 'absolute',
  top: '50%',
  left: '50%',
  transform: 'translate(-50%, -50%)',
  width: 340,
  bgcolor: 'background.paper',
  borderRadius: 2,
  boxShadow: 24,
  p: 4,
  outline: 'none',
  display: 'flex',
  flexDirection: 'column',
  alignItems: 'center',
};

const REASONS = [
  { id: 1, label: '불쾌한 언어 사용' },
  { id: 2, label: '광고/스팸' },
  { id: 3, label: '부적절한 게시물' },
  { id: 4, label: '거래 무단 파기' }
];

const ReportModal = ({ open, onClose, onSubmit, targetUserId }) => {
  const [reportReasonId, setReportReasonId] = useState(REASONS[0].id);
  const [detail, setDetail] = useState('');
  const [snackbar, setSnackbar] = useState({
    open: false,
    message: '',
    severity: 'info',
  });

  const handleReasonChange = (e) => setReportReasonId(Number(e.target.value));
  const handleDetailChange = (e) => setDetail(e.target.value);

  // 신고하기 버튼 클릭 시 POST 요청
  const handleSubmit = async () => {
    // 토큰 확인
    const token = localStorage.getItem('accessToken');
    if (!token) {
      setSnackbar({
        open: true,
        message: '로그인이 필요합니다.',
        severity: 'warning',
      });
      return;
    }
    try {
      const payload = {
        reportReasonId,
        targetId: targetUserId,
        targetType: 'USER',
        reportDetail: detail,
      };
      const response = await axios.post(
        'http://localhost:8080/api/reports',
        payload,
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );
      setSnackbar({
        open: true,
        message: '신고가 정상적으로 접수되었습니다.',
        severity: 'success',
      });
      console.log('신고 접수 응답:', response.data);
      if (onSubmit) onSubmit({ reportReasonId, detail });
      setReportReasonId(REASONS[0].id);
      setDetail('');
      onClose();
    } catch (error) {
      let msg = '신고 접수에 실패했습니다.';
      if (error.response && error.response.data && error.response.data.message) {
        msg = error.response.data.message;
      }
      setSnackbar({
        open: true,
        message: msg,
        severity: 'error',
      });
      console.error('신고 접수 실패:', error);
    }
  };

  const handleClose = () => {
    onClose();
    setReportReasonId(REASONS[0].id);
    setDetail('');
  };

  return (
    <>
      <Modal open={open} onClose={handleClose}>
        <Box sx={modalStyle}>
          <Typography variant="h6" sx={{ mb: 2, fontWeight: 700 }}>
            신고하기
          </Typography>

          <FormControl fullWidth sx={{ mb: 2 }}>
            <InputLabel id="report-reason-label">신고 사유</InputLabel>
            <Select
              labelId="report-reason-label"
              value={reportReasonId}
              label="신고 사유"
              onChange={handleReasonChange}
              size="small"
            >
              {REASONS.map((r) => (
                <MenuItem key={r.id} value={r.id}>{r.label}</MenuItem>
              ))}
            </Select>
          </FormControl>
          <TextField
            label="자세한 내용"
            multiline
            minRows={3}
            fullWidth
            value={detail}
            onChange={handleDetailChange}
            sx={{ mb: 3 }}
          />
          <Box sx={{ display: 'flex', gap: 2 }}>
            <Button variant="outlined" color="primary" onClick={handleClose}>
              취소
            </Button>
            <Button variant="contained" color="error" onClick={handleSubmit}>
              신고하기
            </Button>
          </Box>
        </Box>
      </Modal>
      <FormSnackbar
        open={snackbar.open}
        message={snackbar.message}
        severity={snackbar.severity}
        onClose={() => setSnackbar({ ...snackbar, open: false })}
      />
    </>
  );
};

export default ReportModal;