// HomePage.jsx
import React, { useState, useEffect } from 'react'; // useEffect 임포트
import { useNavigate } from 'react-router-dom';
import { Button, Container, Grid, Typography } from '@mui/material';
import ProductCard from '../components/ProductCard';
import InfiniteScroll from 'react-infinite-scroll-component';
import Layout from '../components/Layout';
import axios from 'axios'; // axios 임포트
import { FormatTime } from '../utils/FormatTime'; // FormatTime 유틸리티 임포트

// allProducts 더미 데이터는 제거합니다.
// const allProducts = Array.from({ length: 100 }).map((_, i) => ({
//   id: i + 1,
//   title: `상품 ${i + 1}`,
//   price: Math.floor(Math.random() * 100000) + 10000,
//   image: 'https://via.placeholder.com/200',
// }));

const HomePage = () => {
  const [products, setProducts] = useState([]); // 초기 상품 목록을 빈 배열로 시작
  const [hasMore, setHasMore] = useState(true);
  const [page, setPage] = useState(0); // 현재 페이지 번호
  const [loading, setLoading] = useState(true); // 초기 로딩 상태

  const navigate = useNavigate();

  // 초기 상품 데이터를 불러오는 useEffect
  useEffect(() => {
    // 상품 목록 초기화 (페이지네이션 상태 초기화)
    setProducts([]);
    setPage(0);
    setHasMore(true);
    setLoading(true); // 로딩 시작
    fetchMoreData(0); // 첫 페이지 데이터 불러오기
  }, []); // 의존성 배열을 비워 컴포넌트 마운트 시 한 번만 실행

  // 상품 데이터를 페이지네이션으로 불러오는 함수
  const fetchMoreData = async (currentPage) => {
    try {
      const response = await axios.get(
        `/api/products?page=${currentPage}&size=12&sort=createdAt,desc`
      );
      const { content, pagination } = response.data.data;

      console.log(`--- Fetching Page ${currentPage} Debug ---`);
      console.log('Received Content:', content);
      console.log('Current Products Before Update (Raw):', products); // 디버그용으로 Raw products 상태 로그

      setProducts((prevProducts) => {
        // 1. 기존 상품들을 Map에 추가하여 ID를 키로 매핑
        const uniqueProductsMap = new Map(prevProducts.map((product) => [product.id, product]));

        // 2. 새로 받아온 상품들을 Map에 추가.
        //    ID가 중복되면 새로운 상품 정보로 덮어씌워짐.
        content.forEach((newProduct) => {
          uniqueProductsMap.set(newProduct.id, newProduct);
        });

        // 3. Map의 값들만 다시 배열로 만들어 반환
        const updatedProducts = Array.from(uniqueProductsMap.values());
        console.log('Products After Duplicate Removal and Update:', updatedProducts); // 업데이트된 products 로그
        return updatedProducts;
      });

      setHasMore(!pagination.last);
      setPage(currentPage + 1);
    } catch (error) {
      console.error('상품 목록을 불러오는 데 실패했습니다.', error);
      setHasMore(false);
    } finally {
      setLoading(false);
    }
  };

  // InfiniteScroll의 next prop으로 전달될 함수
  const handleLoadMore = () => {
    if (!loading && hasMore) {
      fetchMoreData(page);
    }
  };

  // 토큰 값 유무 검사
  const handleRegisterClick = () => {
    const token = localStorage.getItem('accessToken');
    if (!token) {
      alert('로그인을 진행해주세요!');
      return;
    }
    navigate('/products/register');
  };

  return (
    <Layout>
      <Container sx={{ paddingTop: '80px' }}>
        <Button variant="contained" color="success" sx={{ ml: 2 }} onClick={handleRegisterClick}>
          상품 등록
        </Button>
        <Button
          variant="contained"
          color="primary"
          sx={{ ml: 2 }}
          onClick={() => navigate('/my-products')}
        >
          내가 올린 상품
        </Button>

        <Typography variant="h5" sx={{ mt: 4, mb: 2 }}>
          오늘의 상품 추천
        </Typography>
        {loading && products.length === 0 ? (
          <Typography sx={{ textAlign: 'center', mt: 4 }}>상품을 불러오는 중입니다...</Typography>
        ) : (
          <InfiniteScroll
            dataLength={products.length}
            next={handleLoadMore}
            hasMore={hasMore}
            loader={<Typography sx={{ textAlign: 'center', mt: 4 }}>로딩 중...</Typography>}
            endMessage={
              <Typography sx={{ textAlign: 'center', mt: 4 }}>
                모든 상품을 다 불러왔어요!
              </Typography>
            }
          >
            <Grid container spacing={2}>
              {products.map((product) => (
                <Grid item key={product.id} xs={6} sm={4} md={3}>
                  <ProductCard product={product} formatTime={FormatTime} />
                </Grid>
              ))}
            </Grid>
          </InfiniteScroll>
        )}
        {!loading && products.length === 0 && !hasMore && (
          <Typography sx={{ textAlign: 'center', mt: 4 }}>등록된 상품이 없습니다.</Typography>
        )}
      </Container>
    </Layout>
  );
};

export default HomePage;
