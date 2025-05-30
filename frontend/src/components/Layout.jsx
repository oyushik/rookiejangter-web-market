// src/components/Layout.jsx
import React from 'react';
import { Box } from '@mui/material';

const Layout = ({ children }) => {
  return (
    <Box
      sx={{
        width: '100%',
        minHeight: '100vh',
        pt: '50px', // 헤더 높이만큼 여백
        px: 3,       // 좌우 패딩 (원하면 0으로도 가능)
        boxSizing: 'border-box',
        backgroundColor: '#f9f9f9', // 원하는 배경색
      }}
    >
      {children}
    </Box>
  );
};

export default Layout;
