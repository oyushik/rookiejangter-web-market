import { useParams, useNavigate } from 'react-router-dom';
import axios from 'axios';
import { Box, Typography, Divider, Grid, Button } from '@mui/material';
import NotFound from '../err/NotFound';
import { FormatTime } from '../utils/FormatTime';
import React, { useState, useEffect } from 'react';
import { useSelector } from 'react-redux';
import ProductsList from '../components/ProductsList';
import ProductImageSlider from '../components/ProductImageSlider';
import ProductActions from '../components/ProductActions';
import ReportModal from '../components/ReportModal';

const ProductDetailPage = () => {
  const { product_id } = useParams();
  const navigate = useNavigate();
  const currentUser = useSelector((state) => state.auth.identityInfo); // 로그인한 유저 정보
  const authState = useSelector((state) => state.auth);
  const [error, setError] = useState(null);
  const [images, setImages] = useState([]);
  const [reportOpen, setReportOpen] = useState(false);
  const [similarProducts, setSimilarProducts] = useState([]);

  console.log('🧪 auth state:', authState);

  const [product, setProduct] = useState(null);
  const [loading, setLoading] = useState(true);
  const [imgIdx, setImgIdx] = useState(0);

  useEffect(() => {
    console.log('🔍 currentUser:', currentUser);
    console.log('🔍 currentUser.user_id:', currentUser?.user_id);
    console.log('🔍 product.seller:', product?.seller);
    console.log('🔍 product.seller.id:', product?.seller?.id);
  }, [currentUser, product]);
  useEffect(() => {
    window.scrollTo(0, 0);
  }, []);

  // 백엔드 연동 시:
  useEffect(() => {
    axios
      .get(`http://localhost:8080/api/products/${product_id}`)
      .then((res) => {
        console.log('상품 상세 응답:', res.data); // 응답 콘솔 출력
        setProduct(res.data.data);
        setLoading(false);
      })
      .catch((err) => {
        setLoading(false);
        if (err.response && err.response.status === 404) {
          setError('notfound');
        } else {
          setError('unknown');
        }
      });
  }, [product_id, navigate]);

  // 상품 이미지 별도 호출
  useEffect(() => {
    if (!product?.id) return;
    axios
      .get(`http://localhost:8080/images/product/${product.id}`)
      .then((res) => {
        const imgArr = Array.isArray(res.data)
          ? res.data.map((img) =>
              img.imageUrl.startsWith('http')
                ? img.imageUrl.replace('http://localhost:3000', 'http://localhost:8080')
                : `http://localhost:8080${img.imageUrl}`
            )
          : [];
        setImages(imgArr);
      })
      .catch(() => setImages([]));
  }, [product?.id]);

  // 같은 카테고리의 상품들
  useEffect(() => {
    if (!product?.categoryName || !product?.id) return;
    axios
      .get(
        `http://localhost:8080/api/products?categoryName=${encodeURIComponent(
          product.categoryName
        )}`
      )
      .then((res) => {
        const arr = Array.isArray(res.data.data?.content) ? res.data.data.content : [];
        const filtered = arr.filter(
          (p) => p.id !== product.id && p.categoryName === product.categoryName
        );
        setSimilarProducts(filtered);
      })
      .catch(() => setSimilarProducts([]));
  }, [product?.categoryName, product?.id]);

  if (loading) return <div>로딩 중...</div>;
  if (error === 'notfound') return <NotFound />;
  if (error) return <div>알 수 없는 오류가 발생했습니다.</div>;
  if (!product) return null;

  const handlePrev = (e) => {
    e.stopPropagation();
    setImgIdx((idx) => (idx > 0 ? idx - 1 : idx));
  };

  const handleNext = (e) => {
    e.stopPropagation();
    setImgIdx((idx) => (idx < images.length - 1 ? idx + 1 : idx));
  };

  const handleReport = () => {
    setReportOpen(true);
  };

  const handleReportSubmit = () => {
    setReportOpen(false);
    alert('신고 처리가 완료되었습니다.');
    // 실제 신고 API 호출은 이곳에서 처리
  };

  const handleReportClose = () => {
    setReportOpen(false);
  };

  const isOwner = currentUser?.id === product.seller?.id;

  return (
    <Box sx={{ px: 5, py: 4 }}>
      <Grid container spacing={4}>
        <Grid item xs={12}>
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
                지역:{' '}
                {product.seller?.area?.areaName || product.seller?.areaName || '지역정보 없음'}
              </Typography>
            </Box>
            <ProductActions />
          </Box>
        </Grid>

        <Grid item xs={12}>
          <Box sx={{ mb: 4 }}>
            <Typography variant="h6" sx={{ fontWeight: 700, mb: 2, textAlign: 'left' }}>
              이 상품과 비슷해요
            </Typography>
            <ProductsList
              products={similarProducts}
              onProductClick={(id) => navigate(`/products/${id}`)}
              formatTime={FormatTime}
            />
          </Box>

          <Box>
            <Divider sx={{ mb: 2, width: 1120, bgcolor: 'black' }} />
            <Box sx={{ display: 'flex', gap: 4, alignItems: 'flex-start' }}>
              <Box sx={{ flex: 2 }}>
                <Typography variant="h6" sx={{ fontWeight: 700, mb: 2 }}>
                  상품정보
                </Typography>
                <Divider sx={{ mb: 2, width: 800 }} />
                <Typography variant="body1" sx={{ mt: 2, whiteSpace: 'pre-line' }}>
                  {product.content}
                </Typography>
              </Box>

              <Box
                sx={{
                  flex: 1,
                  minWidth: 280,
                  maxWidth: 340,
                  bgcolor: '#fff',
                  borderRadius: 2,
                  boxShadow: 1,
                  p: 3,
                  border: '1px solid #eee',
                }}
              >
                <Typography variant="h6" sx={{ fontWeight: 700, mb: 2 }}>
                  판매자 정보
                </Typography>
                <Box sx={{ display: 'flex', flexDirection: 'column', alignItems: 'center', mb: 2 }}>
                  <Box
                    sx={{
                      width: 64,
                      height: 64,
                      borderRadius: '50%',
                      mb: 1,
                      display: 'flex',
                      alignItems: 'center',
                      justifyContent: 'center',
                      background: (() => {
                        const colors = [
                          '#FFB6C1',
                          '#FFD700',
                          '#87CEFA',
                          '#90EE90',
                          '#FFA07A',
                          '#B39DDB',
                          '#FFCC80',
                          '#80CBC4',
                        ];
                        if (!product.seller?.userName) return '#ccc';
                        const idx = product.seller.userName.charCodeAt(0) % colors.length;
                        return colors[idx];
                      })(),
                      fontSize: 32,
                      fontWeight: 700,
                      color: '#fff',
                      objectFit: 'cover',
                      userSelect: 'none',
                    }}
                  >
                    {product.seller?.userName ? product.seller.userName[0] : '?'}
                  </Box>
                  <Typography sx={{ fontWeight: 600 }}>
                    {product.seller?.userName || '판매자명'}
                  </Typography>

                  <Button
                    variant="outlined"
                    sx={{
                      marginTop: 3,
                      padding: '6px 18px',
                      borderRadius: 2,
                      fontSize: 16,
                      fontWeight: 700,
                      border: `2px solid ${isOwner ? '#1976d2' : '#EA002C'}`,
                      color: isOwner ? '#1976d2' : '#EA002C',
                      background: '#fff',
                      '&:hover': {
                        background: isOwner ? '#e3f2fd' : '#fff0f3',
                        borderColor: isOwner ? '#1976d2' : '#EA002C',
                      },
                    }}
                    onClick={
                      isOwner ? () => navigate(`/my-products/${product.id}/edit`) : handleReport
                    }
                  >
                    {isOwner ? '상품 수정' : '신고하기'}
                  </Button>
                </Box>
              </Box>
            </Box>
          </Box>
        </Grid>
      </Grid>
      {/* 신고 모달 */}
      <ReportModal open={reportOpen} onClose={handleReportClose} onSubmit={handleReportSubmit} />
    </Box>
  );
};

export default ProductDetailPage;
