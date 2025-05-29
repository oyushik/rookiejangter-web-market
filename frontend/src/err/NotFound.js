import { Box, Typography } from '@mui/material';
import React from 'react';

function NotFound() {
  return React.createElement(
    Box,
    { sx: { width: '100%', py: 10, display: 'flex', justifyContent: 'center', alignItems: 'center' } },
    React.createElement(
      Typography,
      { variant: 'h6', color: 'text.secondary' },
      '존재하지 않는 ID 입니다!'
    )
  );
}

export default NotFound;