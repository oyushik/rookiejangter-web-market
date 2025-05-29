import React from 'react';
import { useParams } from 'react-router-dom';
import { Container, Typography } from '@mui/material';

const ProductDetailPage = () => {
  const { id } = useParams();
  return (
    <Container>
      <Typography variant="h4" sx={{ mt: 4 }}>상품 상세 - ID: {id}</Typography>
    </Container>
  );
};

export default ProductDetailPage;
