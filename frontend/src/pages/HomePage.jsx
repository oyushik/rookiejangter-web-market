import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { Button, Container, Grid, Typography } from '@mui/material';
import ProductCard from '../components/ProductCard';
import InfiniteScroll from 'react-infinite-scroll-component';
import Layout from '../components/Layout';
import axios from 'axios'; // axios 임포트
import { FormatTime } from '../utils/FormatTime'; // 시간 포맷팅 유틸리티 임포트

// allProducts 더미 데이터는 이제 필요 없으므로 제거됩니다.
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

  const fetchMoreData = async (currentPage) => {
    try {
      const response = await axios.get(
        `/api/products?page=${currentPage}&size=12&sort=createdAt,desc`
      );

      console.log('--- API Response Debug ---');
      console.log('1. Full Response Object:', response);
      console.log('2. response.data (Outer Data):', response.data);
      // console.log('3. response.data.data (Inner Data):', response.data.data); // 이 라인에서 에러가 발생할 수 있으니, 주석 처리하거나 조건부로 찍어보세요.
      console.log('--- End Debug ---');

      // 디버그 결과를 보고 아래 줄을 수정합니다.
      // 만약 response.data가 이미 ProductListData라면:
      // const { content, pagination } = response.data;
      // 만약 response.data가 ApiResponseWrapper이고, 그 안에 data 필드가 ProductListData라면:
      const { content, pagination } = response.data.data; // 현재 이 코드가 에러를 낸다고 하셨으니, 이 부분이 문제의 핵심입니다.

      // ... (이하 동일)
    } catch (error) {
      console.error('상품 목록을 불러오는 데 실패했습니다.', error);
      setHasMore(false);
    } finally {
      setLoading(false);
    }
  };

  // InfiniteScroll의 next prop으로 전달될 함수
  const handleLoadMore = () => {
    // 현재 페이지를 next prop에 전달
    if (!loading && hasMore) {
      // 로딩 중이 아니고, 더 불러올 데이터가 있을 때만 호출
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
        <Typography variant="h5" sx={{ mt: 4, mb: 2 }}>
          오늘의 상품 추천
        </Typography>
        {loading && products.length === 0 ? ( // 초기 로딩 중이고, 상품이 없을 때 로딩 메시지
          <Typography sx={{ textAlign: 'center', mt: 4 }}>상품을 불러오는 중입니다...</Typography>
        ) : (
          <InfiniteScroll
            dataLength={products.length}
            next={handleLoadMore} // 이제 page 상태를 직접 사용하지 않고, fetchMoreData가 내부적으로 관리하도록 함
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
                  {/* formatTime prop을 ProductCard에 전달 */}
                  <ProductCard product={product} formatTime={FormatTime} />
                </Grid>
              ))}
            </Grid>
          </InfiniteScroll>
        )}
        {!loading &&
          products.length === 0 &&
          !hasMore && ( // 로딩이 끝났고, 상품이 없을 때 (데이터 없음)
            <Typography sx={{ textAlign: 'center', mt: 4 }}>등록된 상품이 없습니다.</Typography>
          )}
      </Container>
    </Layout>
  );
};

export default HomePage;
