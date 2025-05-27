import React from 'react';
import { useNavigate } from 'react-router-dom';
import { Button, Container, Typography } from '@mui/material';

const HomePage = () => {
  const navigate = useNavigate();

  return (
    <Container>
      <Typography variant="h4" gutterBottom>환영합니다!</Typography>
      <Button variant="contained" onClick={() => navigate('/register')}>
        회원가입
      </Button>
    </Container>
  );
};

export default HomePage;
