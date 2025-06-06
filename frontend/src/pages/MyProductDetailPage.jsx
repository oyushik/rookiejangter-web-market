import { useParams, useNavigate } from 'react-router-dom';
import axios from "axios";
import { Box, Typography, Divider, Grid, Button } from '@mui/material';
import { FormatTime } from '../utils/FormatTime';
import React, { useState, useEffect } from 'react';
import ProductImageSlider from '../components/ProductImageSlider';

const MyProductDetailPage = () => {
  const { productId } = useParams();
  const navigate = useNavigate();
  const [imgIdx, setImgIdx] = useState(0);
  const [product, setProduct] = useState(null);
  const [loading, setLoading] = useState(true);
  const [images, setImages] = useState([]);
  const token = localStorage.getItem("accessToken");

  useEffect(() => {
    axios
      .get(`http://localhost:8080/api/users/products/${productId}`, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      })
      .then((res) => {
        setProduct(res.data);
        setLoading(false);
      })
      .catch(() => {
        setLoading(false);
        if (err.response && err.response.status === 404) {
          navigate("/err/NotFound");
        }
      });
  }, [productId, token, navigate]);

  // 상품 이미지 별도 호출
  useEffect(() => {
    axios
      .get(`http://localhost:8080/images/product/${productId}`)
      .then((res) => {
        // 204 No Content일 때 res.data가 undefined일 수 있음
        const imgArr = Array.isArray(res.data)
          ? res.data.map(img =>
              img.imageUrl.startsWith('http')
                ? img.imageUrl.replace('http://localhost:3000', 'http://localhost:8080')
                : `http://localhost:8080${img.imageUrl}`
            )
        : [];
        setImages(imgArr);
      })
      .catch(() => setImages([]));
  }, [productId]);

  useEffect(() => {
    window.scrollTo(0, 0);
  }, []);

  if (loading) return <div>로딩 중...</div>;
  if (!product) return <div>상품 정보를 찾을 수 없습니다.</div>;

  const handlePrev = (e) => {
    e.stopPropagation();
    setImgIdx((idx) => (idx > 0 ? idx - 1 : idx));
  };

  const handleNext = (e) => {
    e.stopPropagation();
    setImgIdx((idx) => (idx < images.length - 1 ? idx + 1 : idx));
  };

  return (
    <Box sx={{ px: 5, py: 4 }}>
      <Grid container spacing={4}>
        <Grid item xs={12}>
          <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
            <Button
              variant="contained"
              onClick={() => navigate("/my-products")}
              sx={{ mr: 2 }}
            >
              목록으로
            </Button>
          </Box>
          <Box
            sx={{
              display: 'flex',
              flexDirection: { xs: 'column', md: 'row' },
              alignItems: 'flex-start',
              gap: 4,
              minHeight: 400,
              position: 'relative',
            }}
          >
            <ProductImageSlider
              images={images}
              imgIdx={imgIdx}
              onPrev={handlePrev}
              onNext={handleNext}
              title={product.title}
            />
            <Box
              sx={{
                flex: 1,
                textAlign: 'left',
                display: 'flex',
                flexDirection: 'column',
                position: 'relative',
                minHeight: 400,
                justifyContent: 'flex-start',
              }}
            >
              <Typography variant="h4" sx={{ mb: 2 }}>
                {product.title}
              </Typography>
              <Typography
                variant="subtitle1"
                sx={{ mb: 1, color: '#777', display: 'flex', alignItems: 'center', gap: 2 }}
              >
                카테고리: {product.categoryName}
                <Divider orientation="vertical" flexItem sx={{ mx: 2 }} />
                상태: &nbsp;
                  {product.isCompleted ? 'SOLD' : product.isReserved ? 'RESERVED' : 'SALE'}
                <Divider orientation="vertical" flexItem sx={{ mx: 2 }} />
                등록일: {FormatTime(product.createdAt)}
              </Typography>
              <Divider sx={{ mb: 2, width: 725 }} />
              <Typography variant="h3" sx={{ mb: 2, fontWeight: 700 }}>
                {product.price?.toLocaleString()}원
              </Typography>
              <Typography variant="subtitle2" sx={{ mb: 1 }}>
                지역: {product.seller?.area?.name || '지역정보 없음'}
              </Typography>
              <Box sx={{ position: 'absolute', right: 10, bottom: 0, display: 'flex', gap: 2 }}>
                <Button
                  variant="contained"
                  color="info"
                  size="large"
                  sx={{
                    width: 200,
                    height: 60,
                    padding: '8px 20px',
                    borderRadius: 0,
                    fontSize: 22,
                    fontWeight: 700,
                    color: 'white',
                  }}
                  onClick={() => navigate(`/my-products/${productId}/edit`)}
                >
                  상품 수정
                </Button>
                <Button
                  variant="contained"
                  color="error"
                  size="large"
                  sx={{
                    width: 200,
                    height: 60,
                    padding: '8px 20px',
                    borderRadius: 0,
                    fontSize: 22,
                    fontWeight: 700,
                    backgroundColor: '#d32f2f',
                    '&:hover': { backgroundColor: '#b71c1c' },
                  }}
                  onClick={() => {
                    if (window.confirm("정말로 이 상품을 삭제하시겠습니까?")) {
                      axios
                        .delete(`http://localhost:8080/api/users/products/${productId}`, {
                          headers: {
                            Authorization: `Bearer ${token}`,
                          },
                        })
                        .then(() => {
                          alert("상품이 삭제되었습니다.");
                          navigate("/my-products");
                        })
                        .catch(() => {
                          alert("상품 삭제에 실패했습니다.");
                        });
                    }
                  }}
                >
                  상품 삭제
                </Button>
              </Box>
            </Box>
          </Box>
        </Grid>

        <Grid item xs={12}>
          <Box sx={{ mb: 4 }}>
            <Typography variant="h6" sx={{ fontWeight: 700, mb: 2, textAlign: 'left' }}>
              상품정보
            </Typography>
            <Divider sx={{ mb: 2, width: 800 }} />
            <Typography variant="body1" sx={{ mt: 2, whiteSpace: 'pre-line' }}>
              {product.content}
            </Typography>
          </Box>
        </Grid>
      </Grid>
    </Box>
  );
};

export default MyProductDetailPage;