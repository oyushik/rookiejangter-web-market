import React, { useState } from 'react';
import { Modal, Box, Typography, Button, MenuItem, TextField, Select, FormControl, InputLabel } from '@mui/material';

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
    '불쾌한 언어 사용',
    '광고/스팸',
    '부적절한 게시물',
    '거래 무단 파기'
];

const ReportModal = ({ open, onClose, onSubmit }) => {
  const [reason, setReason] = useState(REASONS[0]);
  const [detail, setDetail] = useState('');

  const handleReasonChange = (e) => setReason(e.target.value);
  const handleDetailChange = (e) => setDetail(e.target.value);

  const handleSubmit = () => {
    onSubmit({ reason, detail });
    setReason(REASONS[0]);
    setDetail('');
  };

  const handleClose = () => {
    onClose();
    setReason(REASONS[0]);
    setDetail('');
  };

  return (
    <Modal open={open} onClose={handleClose}>
      <Box sx={modalStyle}>
        <Typography variant="h6" sx={{ mb: 2, fontWeight: 700 }}>
          신고하기
        </Typography>

        <FormControl fullWidth sx={{ mb: 2 }}>
          <InputLabel id="report-reason-label">신고 사유</InputLabel>
          <Select
            labelId="report-reason-label"
            value={reason}
            label="신고 사유"
            onChange={handleReasonChange}
            size="small"
          >
            {REASONS.map((r) => (
              <MenuItem key={r} value={r}>{r}</MenuItem>
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
  );
};

export default ReportModal;