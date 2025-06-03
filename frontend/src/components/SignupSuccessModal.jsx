// src/components/SignupSuccessModal.jsx
import React from 'react';
import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Button,
  Box,
  Typography,
  Fade,
  Backdrop,
} from '@mui/material';
import { styled } from '@mui/material/styles';

const StyledDialog = styled(Dialog)(({ theme }) => ({
  '& .MuiDialog-paper': {
    backgroundColor: '#fff',
    borderRadius: '12px',
    boxShadow: theme.shadows[5],
  },
}));

const SignupSuccessModal = ({ open, onClose, onConfirm, children }) => {
  return (
    <StyledDialog
      open={open}
      onClose={onClose}
      aria-labelledby="signup-success-dialog"
      BackdropComponent={Backdrop}
      BackdropProps={{ timeout: 500 }}
    >
      <Fade in={open}>
        <Box>
          <DialogTitle
            id="signup-success-dialog"
            sx={{ textAlign: 'center', py: 2, bgcolor: '#f0f7ff' }}
          >
            <Typography variant="h6" component="div" sx={{ fontWeight: 'bold', color: '#1976d2' }}>
              회원가입 완료
            </Typography>
          </DialogTitle>
          <DialogContent sx={{ textAlign: 'center', py: 3, px: 4 }}>
            <Typography variant="body1" color="textSecondary">
              {children}
            </Typography>
          </DialogContent>
          <DialogActions sx={{ justifyContent: 'center', py: 2, bgcolor: '#f0f7ff' }}>
            <Button onClick={onConfirm} color="primary" variant="contained" autoFocus>
              확인
            </Button>
          </DialogActions>
        </Box>
      </Fade>
    </StyledDialog>
  );
};

export default SignupSuccessModal;
