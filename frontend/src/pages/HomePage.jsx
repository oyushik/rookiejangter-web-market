import React from 'react';
import { useNavigate } from 'react-router-dom';
import { Button, Container, Typography } from '@mui/material';
import ProductSearch from '../components/ProductSearch';

const HomePage = () => {
  const navigate = useNavigate();

  return (
    <Container>
      <Typography variant="h4" gutterBottom>루키장터 </Typography>
      <Button variant="contained" onClick={() => navigate('/register')}>
        회원가입1
      </Button>
      <Button variant="contained" onClick={() => navigate('/signup')}>
        회원가입2
      </Button>
      <ProductSearch />
    </Container>
  );
};

export default HomePage;
