import { useParams, useNavigate } from "react-router-dom";
import { products } from "../constants/ExpProductDB"; //예시 데이터베이스
// import axios from "axios";
import { Box, Typography, Divider, Grid } from "@mui/material";
import NotFound from "../err/NotFound";
import { FormatTime } from "../utils/FormatTime";
import React, { useState, useEffect } from "react";
import ProductsList from "../components/ProductsList";
import ProductImageSlider from "../components/ProductImageSlider";
import ProductActions from "../components/ProductActions";

const ProductDetailPage = () => {
  const { product_id } = useParams();
  const navigate = useNavigate();
  const product = products.find(p => String(p.id) === String(product_id)); // 백엔드 연동 시 삭제
  // const [product, setProduct] = useState(null);
  const [imgIdx, setImgIdx] = useState(0);

  useEffect(() => {
    window.scrollTo(0, 0);
  }, []);
  // 백엔드 연결 시 아래 코드 사용용
  // useEffect(() => {
  //   window.scrollTo(0, 0);
  //   // 백엔드에서 상품 정보 가져오기
  //   axios.get(`/api/products/${product_id}`)
  //     .then(res => {
  //       setProduct(res.data.data); // 백엔드 응답 구조에 따라 조정
  //       setLoading(false);
  //     })
  //     .catch(err => {
  //       setLoading(false);
  //       if (err.response && err.response.status === 404) {
  //         navigate("/err/NotFound");
  //       }
  //     });
  // }, [product_id, navigate]);

  // if (loading) return <div>로딩 중...</div>;

  if (!product) {
    return <NotFound />;
  }

  const images = product.images || [];

  // 비슷한 상품 리스트 (같은 카테고리, 자기 자신 제외, 최근순 정렬, 5개 제한)
  const similarProducts = products
    .filter(
      p =>
        p.category === product.category &&
        p.id !== product.id
    )
    .sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt))
    .slice(0, 5);

  const handlePrev = (e) => {
    e.stopPropagation();
    setImgIdx(idx => idx > 0 ? idx - 1 : idx);
  };

  const handleNext = (e) => {
    e.stopPropagation();
    setImgIdx(idx => idx < images.length - 1 ? idx + 1 : idx);
  };

  const handleReport = () => {
    alert('신고 처리가 완료되었습니다.');
  };

  return (
    <Box sx={{ px: 5, py: 4 }}>
      <Grid container spacing={4}>
        {/* 이미지와 텍스트를 한 줄로 묶는 Box */}
        <Grid item xs={12}>
          <Box
            sx={{
              display: 'flex',
              flexDirection: { xs: 'column', md: 'row' },
              alignItems: 'flex-start',
              gap: 4,
              minHeight: 400,
              position: 'relative'
            }}
          >
            {/* 왼쪽: 이미지 영역 (컴포넌트 분리) */}
            <ProductImageSlider
              images={images}
              imgIdx={imgIdx}
              onPrev={handlePrev}
              onNext={handleNext}
              title={product.title}
            />
            {/* 오른쪽: 상세 정보 */}
            <Box sx={{ flex: 1, textAlign: 'left', display: 'flex', flexDirection: 'column' }}>
              <Typography variant="h4" sx={{ mb: 2 }}>{product.title}</Typography>
              <Typography variant="subtitle1" sx={{ mb: 1, color: '#777', display: 'flex', alignItems: 'center', gap: 2 }}>
                카테고리: {product.category}
                <Divider orientation="vertical" flexItem sx={{ mx: 2 }} />
                상태: {product.status}
                <Divider orientation="vertical" flexItem sx={{ mx: 2 }} />
                등록일: {FormatTime(product.createdAt)}
              </Typography>
              <Divider sx={{ mb: 2, width: 725 }} />
              <Typography variant="h3" sx={{ mb: 2, fontWeight: 700 }}>
                {product.price.toLocaleString()}원
              </Typography>
              <Typography variant="subtitle2" sx={{ mb: 1 }}>
                지역: {product.area || "지역정보 없음"}
              </Typography>
            </Box>
            {/* 버튼 영역: 컴포넌트 분리 */}
            <ProductActions />
          </Box>
        </Grid>
        {/* 설명은 아래에 별도 Row로 */}
        <Grid item xs={12}>
          {/* 비슷한 상품 추천 영역 */}
          <Box sx={{ mb: 4 }}>
            <Typography variant="h6" sx={{ fontWeight: 700, mb: 2, textAlign: 'left' }}>
              이 상품과 비슷해요
            </Typography>
            <ProductsList
              products={similarProducts}
              onProductClick={id => navigate(`/products/${id}`)}
              formatTime={FormatTime}
            />
          </Box>
          <Box>
            <Divider sx={{ mb: 2, width: 1120 , bgcolor: 'black' }} />
            <Box sx={{ display: 'flex', gap: 4, alignItems: 'flex-start' }}>
              {/* 상품정보 */}
              <Box sx={{ flex: 2 }}>
                <Typography variant="h6" sx={{ fontWeight: 700, mb: 2 }}>
                  상품정보
                </Typography>
                <Divider sx={{ mb: 2, width: 800 }} />
                <Typography variant="body1" sx={{ mt: 2, whiteSpace: 'pre-line' }}>
                  {product.description}
                </Typography>
              </Box>
              {/* 판매자 정보 */}
              <Box sx={{
                flex: 1,
                minWidth: 280,
                maxWidth: 340,
                bgcolor: '#fff',
                borderRadius: 2,
                boxShadow: 1,
                p: 3,
                border: '1px solid #eee'
              }}>
                <Typography variant="h6" sx={{ fontWeight: 700, mb: 2 }}>
                  판매자 정보
                </Typography>
                <Box sx={{ display: 'flex', flexDirection: 'column', alignItems: 'center', mb: 2 }}>
                  <Box
                    component="img"
                    src={product.seller?.profileImage || '/default-profile.png'}
                    alt="판매자 프로필"
                    sx={{ width: 64, height: 64, borderRadius: '50%', mb: 1, objectFit: 'cover' }}
                  />
                  <Typography sx={{ fontWeight: 600 }}>
                    {product.seller?.name || '판매자명'}
                  </Typography>
                  <Typography sx={{ color: '#888', fontSize: 14 }}>
                    평균 평점 | -
                  </Typography>
                  <button
                    style={{
                      marginTop: 40,
                      padding: '6px 18px',
                      border: '1px solid #EA002C',
                      borderRadius: 4,
                      background: '#fff',
                      color: '#EA002C',
                      cursor: 'pointer',
                      fontWeight: 600
                    }}
                    onClick={handleReport}
                  >
                    신고하기
                  </button>
                </Box>
              </Box>
            </Box>
          </Box>
        </Grid>
      </Grid>
    </Box>
  );
};

export default ProductDetailPage;