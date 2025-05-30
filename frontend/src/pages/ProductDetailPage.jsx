import { useParams } from "react-router-dom";
import { products } from "../constants/ExpProductDB";
import { Box, Typography, Divider } from "@mui/material";
import NotFound from "../err/NotFound";

const ProductDetailPage = () => {
  const { product_id } = useParams();
  const product = products.find(p => String(p.id) === String(product_id));

  if (!product) {
    return (
      <NotFound />
    );
  }

  return (
    <Box sx={{ p: 4 }}>
      <Typography variant="h4" sx={{ mb: 2 }}>{product.title}</Typography>
      <Divider sx={{ mb: 2 }} />
      <Typography variant="subtitle1" sx={{ mb: 1 }}>
        가격: {product.price.toLocaleString()}원
      </Typography>
      <Typography variant="subtitle1" sx={{ mb: 1 }}>
        카테고리: {product.category}
      </Typography>
      <Typography variant="subtitle1" sx={{ mb: 1 }}>
        상태: {product.status}
      </Typography>
      <Typography variant="subtitle1" sx={{ mb: 1 }}>
        지역: {product.area || "지역정보 없음"}
      </Typography>
      <Typography variant="body1" sx={{ mt: 2 }}>
        {product.description}
      </Typography>
      <Typography variant="caption" color="text.secondary" sx={{ mt: 2, display: 'block' }}>
        등록일: {product.createdAt}
      </Typography>
    </Box>
  );
};

export default ProductDetailPage;
