import { Card, CardContent, Typography, Box, Chip, Divider } from '@mui/material';
import { useNavigate } from 'react-router-dom';

const CARD_WIDTH = 210;
const CARD_HEIGHT = 284;
const IMAGE_HEIGHT = 200;

const ProductCard = ({ product, onClick, formatTime }) => {
  const navigate = useNavigate();

  const handleClick = () => {
    navigate(`/products/${product.id}`);
  };

  return (
    <Card
      onClick={onClick || handleClick}
      sx={{
        cursor: 'pointer',
        height: CARD_HEIGHT,
        display: 'flex',
        flexDirection: 'column',
        width: CARD_WIDTH,
      }}
    >
      {/* 이미지 영역 */}
      <Box
        sx={{
          position: 'relative',
          height: IMAGE_HEIGHT,
          bgcolor: '#eee',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          width: CARD_WIDTH,
        }}
      >
        {product.badge && (
          <Chip
            label={product.badge}
            size="small"
            sx={{
              position: 'absolute',
              top: 8,
              left: 8,
              bgcolor: 'rgba(0,0,0,0.7)',
              color: '#fff',
              fontWeight: 700,
            }}
          />
        )}
        {product.images && product.images[0] ? (
          <img
            src={product.images[0]}
            alt={product.title}
            style={{
              width: '100%',
              height: '100%',
              objectFit: 'cover',
            }}
          />
        ) : (
          <Typography variant="body2" color="text.secondary">
            [이미지]
          </Typography>
        )}
      </Box>
      <CardContent
        sx={{
          flexGrow: 1,
          display: 'flex',
          flexDirection: 'column',
          width: CARD_WIDTH,
          maxHeight: 120,
          boxSizing: 'border-box',
          pb: '0 !important',
          pt: 1,
        }}
      >
        <Typography
          variant="body2"
          sx={{
            fontWeight: 500,
            mb: 0.5,
            overflow: 'hidden',
            textOverflow: 'ellipsis',
            whiteSpace: 'nowrap',
            fontSize: '0.95rem',
            textAlign: 'left',
          }}
        >
          {product.title}
        </Typography>
        {/* 가격(왼쪽) - 시간(오른쪽) */}
        <Box
          sx={{
            display: 'flex',
            justifyContent: 'space-between',
            alignItems: 'center',
            mb: 0.1,
          }}
        >
          <Typography sx={{ fontWeight: 700, fontSize: '0.95rem' }} color="primary">
            {product.price.toLocaleString()}
            <span style={{ fontWeight: 400, color: 'inherit', fontSize: '0.85rem' }}>원</span>
          </Typography>
          <Typography variant="caption" color="text.secondary" sx={{ fontSize: '0.75rem' }}>
            {formatTime ? formatTime(product.createdAt) : product.createdAt}
          </Typography>
        </Box>
        <Divider sx={{ my: 0.2 }} />
        {/* 위치정보 한 줄 추가 */}
        <Box sx={{ display: 'flex', alignItems: 'center', mb: 1 }}>
          <Typography variant="caption" color="text.secondary" sx={{ fontSize: '0.75rem' }}>
            {product.area || '지역정보 없음'}
          </Typography>
        </Box>
      </CardContent>
    </Card>
  );
};

export default ProductCard;