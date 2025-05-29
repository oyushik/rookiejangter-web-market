import React from 'react';
import { Card, CardContent, CardMedia, Typography } from '@mui/material';
import { useNavigate } from 'react-router-dom';

const ProductCard = ({ product }) => {
  const navigate = useNavigate();

  const handleClick = () => {
    navigate(`/products/${product.id}`);
  };
  return <Card sx={{ width: 210 , cursor: 'pointer'}} onClick={handleClick}>
    <CardMedia component="img" height="200" image={product.image} alt={product.title} />
    <CardContent>
      <Typography variant="body1">{product.title}</Typography>
      <Typography variant="body2" color="text.secondary">{product.price}원</Typography>
    </CardContent>
  </Card>
};

export default ProductCard;